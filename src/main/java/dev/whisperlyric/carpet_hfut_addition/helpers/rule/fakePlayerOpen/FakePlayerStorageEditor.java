package dev.whisperlyric.carpet_hfut_addition.helpers.rule.fakePlayerOpen;

import dev.whisperlyric.carpet_hfut_addition.mixins.rule.fakePlayerOpen.PlayerListAccessor;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.storage.PlayerDataStorage;

//#if MC >= 12110
//$$ import net.minecraft.server.players.NameAndId;
//$$ import net.minecraft.util.ProblemReporter;
//$$ import net.minecraft.world.level.storage.TagValueInput;
//#elseif MC >= 12106
//$$ import net.minecraft.util.ProblemReporter;
//$$ import net.minecraft.world.level.storage.ValueInput;
//#endif

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Offline storage editor for fake players ({@code /player <name> open
 * inventory|enderchest}): the target's playerdata is loaded into a
 * shadow {@link ServerPlayer} that never joins the world, and every exit path
 * writes it back to disk before vanilla can read stale data.
 *
 * <p>Duping is prevented by exclusive access (one viewer at a time) and by
 * flushing at join ({@link #flushFor} runs at {@code placeNewPlayer} HEAD, so
 * a joining entity reads the just-written data) and on server stop
 * ({@link #flushAll}). Version branches mirror vanilla's own login loader.
 */
public final class FakePlayerStorageEditor {
    public enum Kind { INVENTORY, ENDER_CHEST }

    /** One open editing session: the shadow player plus its current viewer. */
    public static final class Session {
        private final UUID uuid;
        private final String name;
        private final ServerPlayer shadow;
        private ServerPlayer viewer;
        private AbstractContainerMenu menu;

        Session(UUID uuid, String name, ServerPlayer shadow) {
            this.uuid = uuid;
            this.name = name;
            this.shadow = shadow;
        }

        public UUID uuid() {
            return uuid;
        }

        public String name() {
            return name;
        }

        public ServerPlayer shadow() {
            return shadow;
        }
    }

    public record BeginResult(Session session, String errorKey, Object[] errorArgs) {
        static BeginResult ok(Session session) {
            return new BeginResult(session, null, null);
        }

        static BeginResult fail(String key, Object... args) {
            return new BeginResult(null, key, args);
        }
    }

    private static final Map<UUID, Session> SESSIONS = new HashMap<>();
    private static MinecraftServer server;

    private FakePlayerStorageEditor() {
    }

    public static void attach(MinecraftServer minecraftServer) {
        server = minecraftServer;
    }

    public static void clear() {
        server = null;
        SESSIONS.clear();
    }

    /**
     * Begins (or reuses) the editing session for {@code name}. Fails when the
     * identity is online, someone else is viewing it, or no playerdata exists.
     */
    public static BeginResult begin(ServerPlayer viewer, String name) {
        if (server == null) {
            return BeginResult.fail("hfut.fakePlayerOpen.no_server");
        }
        UUID uuid = UUIDUtil.createOfflinePlayerUUID(name);
        PlayerList list = server.getPlayerList();
        if (list.getPlayer(uuid) != null) {
            return BeginResult.fail("hfut.fakePlayerOpen.online", name);
        }
        Session session = SESSIONS.get(uuid);
        if (session != null) {
            if (session.viewer != null) {
                if (session.viewer.hasDisconnected()) {
                    detach(session);
                } else {
                    return BeginResult.fail("hfut.fakePlayerOpen.in_use", name,
                            session.viewer.getName().getString());
                }
            }
            return BeginResult.ok(session);
        }
        ServerLevel level = server.overworld();
        ServerPlayer shadow = new ServerPlayer(server, level,
                UUIDUtil.createOfflineProfile(name), ClientInformation.createDefault());
        if (!loadPlayerData(list, shadow, uuid, name)) {
            return BeginResult.fail("hfut.fakePlayerOpen.no_data", name);
        }
        session = new Session(uuid, name, shadow);
        SESSIONS.put(uuid, session);
        return BeginResult.ok(session);
    }

    private static boolean loadPlayerData(PlayerList list, ServerPlayer shadow,
                                          UUID uuid, String name) {
        //#if MC >= 12110
        //$$ Optional<CompoundTag> tag = list.loadPlayerData(new NameAndId(uuid, name));
        //$$ tag.ifPresent(value -> shadow.load(TagValueInput.create(
        //$$         ProblemReporter.DISCARDING, server.registryAccess(), value)));
        //$$ return tag.isPresent();
        //#elseif MC >= 12106
        //$$ Optional<ValueInput> loaded = list.load(shadow, ProblemReporter.DISCARDING);
        //$$ loaded.ifPresent(shadow::load);
        //$$ return loaded.isPresent();
        //#else
        Optional<CompoundTag> tag = list.load(shadow);
        tag.ifPresent(shadow::load);
        return tag.isPresent();
        //#endif
    }

    /** Registers the viewer and its menu on a begun session. */
    public static void attach(Session session, ServerPlayer viewer, AbstractContainerMenu menu) {
        session.viewer = viewer;
        session.menu = menu;
    }

    /** Viewer closed the editor (or had it closed for them): write the shadow back. */
    public static void onViewerClose(AbstractContainerMenu menu, ServerPlayer player) {
        if (menu == null || server == null || SESSIONS.isEmpty()) {
            return;
        }
        for (Session session : SESSIONS.values()) {
            if (session.menu == menu && session.viewer == player) {
                detach(session);
                return;
            }
        }
    }

    private static void detach(Session session) {
        session.viewer = null;
        session.menu = null;
        writeBack(session);
    }

    private static void writeBack(Session session) {
        if (server == null) {
            return;
        }
        playerIo().save(session.shadow);
    }

    /** Real login or carpet fake spawn of this identity: flush, then let vanilla read fresh data. */
    public static void flushFor(UUID uuid) {
        Session session = SESSIONS.remove(uuid);
        if (session == null) {
            return;
        }
        if (server != null) {
            playerIo().save(session.shadow);
        }
        ServerPlayer viewer = session.viewer;
        session.viewer = null;
        session.menu = null;
        if (viewer != null && !viewer.hasDisconnected()) {
            viewer.doCloseContainer();
        }
    }

    /** Server stop: persist every open session. */
    public static void flushAll() {
        if (server != null) {
            PlayerDataStorage io = playerIo();
            for (Session session : SESSIONS.values()) {
                io.save(session.shadow);
            }
        }
        SESSIONS.clear();
    }

    /** True when {@code entity} is a shadow player of an open session (GCA buttons are suppressed for these). */
    public static boolean isShadowPlayer(Entity entity) {
        if (SESSIONS.isEmpty()) {
            return false;
        }
        for (Session session : SESSIONS.values()) {
            if (session.shadow == entity) {
                return true;
            }
        }
        return false;
    }

    private static PlayerDataStorage playerIo() {
        return ((PlayerListAccessor) server.getPlayerList()).hfut$playerIo();
    }
}
