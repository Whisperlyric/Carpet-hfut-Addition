package dev.whisperlyric.carpet_hfut_addition.helpers.rule.fakePlayerOpen;

import com.mojang.authlib.GameProfile;
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

import java.util.EnumMap;
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
 * <p>Duping is prevented by exclusive access per view: the inventory and the
 * ender chest of one identity are tracked independently, so each can have at
 * most one viewer at a time. A single shadow player backs both views, and the
 * shadow is written back whenever a view is released, at join
 * ({@link #flushFor} runs at {@code placeNewPlayer} HEAD, so a joining entity
 * reads the just-written data) and on server stop ({@link #flushAll}). The
 * target identity is resolved the way carpet spawns fake players - the profile
 * cache (usercache.json) first, then the offline UUID derivation - and the
 * on-disk playerdata is probed for each candidate, so a fake player stored
 * under its authenticated UUID is still found. Version branches mirror
 * vanilla's own login loader.
 */
public final class FakePlayerStorageEditor {
    public enum Kind { INVENTORY, ENDER_CHEST }

    /** One open editing session: the shadow player plus one viewer per open view. */
    public static final class Session {
        private final UUID uuid;
        private final String name;
        private final ServerPlayer shadow;
        private final Map<Kind, Editor> editors = new EnumMap<>(Kind.class);

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

    /** The player currently viewing one {@link Kind} and the menu they hold. */
    private record Editor(ServerPlayer viewer, AbstractContainerMenu menu) {
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

    private static ServerPlayer newShadow(MinecraftServer server, ServerLevel level, UUID uuid, String name) {
        return new ServerPlayer(server, level, new GameProfile(uuid, name), ClientInformation.createDefault());
    }

    /**
     * Begins (or reuses) the editing session for {@code name} and reserves the
     * given view. Fails when the identity is online, someone else is already
     * viewing that view, or no playerdata exists.
     */
    public static BeginResult begin(String name, Kind kind) {
        if (server == null) {
            return BeginResult.fail("hfut.fakePlayerOpen.no_server");
        }
        PlayerList list = server.getPlayerList();
        if (list.getPlayerByName(name) != null) {
            return BeginResult.fail("hfut.fakePlayerOpen.online", name);
        }
        ServerLevel level = server.overworld();
        UUID cached = cachedIdentityId(name);
        ServerPlayer shadow = null;
        UUID uuid = null;
        if (cached != null) {
            ServerPlayer probe = newShadow(server, level, cached, name);
            if (loadPlayerData(list, probe, cached, name)) {
                shadow = probe;
                uuid = cached;
            }
        }
        if (shadow == null) {
            UUID offline = UUIDUtil.createOfflinePlayerUUID(name);
            ServerPlayer probe = newShadow(server, level, offline, name);
            if (loadPlayerData(list, probe, offline, name)) {
                shadow = probe;
                uuid = offline;
            }
        }
        if (shadow == null) {
            return BeginResult.fail("hfut.fakePlayerOpen.no_data", name);
        }
        Session session = SESSIONS.get(uuid);
        if (session != null) {
            Editor editor = session.editors.get(kind);
            if (editor != null) {
                if (editor.viewer().hasDisconnected()) {
                    release(session, kind);
                } else {
                    return BeginResult.fail("hfut.fakePlayerOpen.in_use", name,
                            editor.viewer().getName().getString());
                }
            }
            return BeginResult.ok(session);
        }
        session = new Session(uuid, name, shadow);
        SESSIONS.put(uuid, session);
        return BeginResult.ok(session);
    }

    /**
     * The identity's UUID as cached by this server (profile cache / usercache),
     * or {@code null} when unknown. Cache-only: never hits the auth backend.
     */
    private static UUID cachedIdentityId(String name) {
        //#if MC >= 12110
        //$$ return server.services().nameToIdCache().get(name).map(NameAndId::id).orElse(null);
        //#else
        return server.getProfileCache().get(name).map(GameProfile::getId).orElse(null);
        //#endif
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

    /** Registers the viewer and its menu on a begun session for the given view. */
    public static void attach(Session session, Kind kind, ServerPlayer viewer, AbstractContainerMenu menu) {
        session.editors.put(kind, new Editor(viewer, menu));
    }

    /** Viewer closed a view (or had it closed for them): write the shadow back. */
    public static void onViewerClose(Kind kind, AbstractContainerMenu menu, ServerPlayer player) {
        if (menu == null || server == null || SESSIONS.isEmpty()) {
            return;
        }
        for (Session session : SESSIONS.values()) {
            Editor editor = session.editors.get(kind);
            if (editor != null && editor.menu() == menu && editor.viewer() == player) {
                release(session, kind);
                return;
            }
        }
    }

    private static void release(Session session, Kind kind) {
        if (session.editors.remove(kind) != null) {
            writeBack(session);
        }
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
        for (Editor editor : session.editors.values()) {
            ServerPlayer viewer = editor.viewer();
            if (!viewer.hasDisconnected() && viewer.containerMenu == editor.menu()) {
                viewer.doCloseContainer();
            }
        }
        session.editors.clear();
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
