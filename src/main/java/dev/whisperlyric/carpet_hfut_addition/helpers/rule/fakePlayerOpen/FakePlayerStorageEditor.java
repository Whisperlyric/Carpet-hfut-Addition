package dev.whisperlyric.carpet_hfut_addition.helpers.rule.fakePlayerOpen;

import com.mojang.authlib.GameProfile;
import dev.whisperlyric.carpet_hfut_addition.FakePlayerOpenStorageSettings;
import dev.whisperlyric.carpet_hfut_addition.mixins.rule.fakePlayerOpen.PlayerListAccessor;
import dev.whisperlyric.carpet_hfut_addition.utils.CommandUtil;
import net.minecraft.commands.CommandSourceStack;
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
 * Loads a target's playerdata into a shadow {@link ServerPlayer} that never joins
 * the world, for editing via {@code /player <name> open}; every exit path writes it
 * back to disk before vanilla can read stale data. Exclusive access plus flushes at
 * join ({@link #flushFor}) and server stop ({@link #flushAll}) prevent duping.
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
     * Auth-backend lookup (blocking on first hit, cached afterwards); authlib-injector
     * setups resolve their registered users through the same call.
     */
    private static Optional<GameProfile> resolveViaBackend(MinecraftServer server, String name) {
        //#if MC >= 12110
        //$$ Optional<GameProfile> fetched = server.services().profileResolver().fetchByName(name);
        //$$ fetched.ifPresent(profile -> server.services().nameToIdCache().add(new NameAndId(profile)));
        //$$ return fetched;
        //#else
        return server.getProfileCache().get(name);
        //#endif
    }

    private static ServerPlayer newShadow(MinecraftServer server, ServerLevel level, UUID uuid, String name) {
        return new ServerPlayer(server, level, new GameProfile(uuid, name), ClientInformation.createDefault());
    }

    /**
     * Begins (or reuses) the editing session for {@code name}. The offline (v3)
     * identity is probed first with no network access; only when it holds no data is
     * the auth backend consulted, and the identity it resolves to decides the rule
     * (premium/v4 → real rule, otherwise fake). The identity rule is checked before
     * session reuse, so an existing session cannot bypass the real-player rule, and
     * the backend query itself is only reached by callers who would pass it.
     */
    public static BeginResult begin(CommandSourceStack source, ServerPlayer viewer, String name) {
        if (server == null) {
            return BeginResult.fail("hfut.fakePlayerOpen.no_server");
        }
        PlayerList list = server.getPlayerList();
        ServerLevel level = server.overworld();
        UUID uuid = UUIDUtil.createOfflinePlayerUUID(name);
        boolean premium = false;
        ServerPlayer shadow = newShadow(server, level, uuid, name);
        if (!loadPlayerData(list, shadow, uuid, name)) {
            // no offline data: the auth backend is only reached when the real rule is
            // enabled AND this caller passes it - anyone else (or a disabled rule)
            // reports "no data", keeping premium lookups and their network cost gated
            String realRule = FakePlayerOpenStorageSettings.commandRealPlayerOpenStorage;
            if ("false".equals(realRule) || !CommandUtil.canUseCommand(source, realRule)) {
                return BeginResult.fail("hfut.fakePlayerOpen.no_data", name);
            }
            Optional<GameProfile> fetched = resolveViaBackend(server, name);
            if (fetched.isEmpty()) {
                return BeginResult.fail("hfut.fakePlayerOpen.no_data", name);
            }
            //#if MC >= 12110
            //$$ uuid = fetched.get().id();
            //#else
            uuid = fetched.get().getId();
            //#endif
            premium = !uuid.equals(UUIDUtil.createOfflinePlayerUUID(name));
            shadow = newShadow(server, level, uuid, name);
            if (!loadPlayerData(list, shadow, uuid, name)) {
                return BeginResult.fail("hfut.fakePlayerOpen.no_data", name);
            }
        }
        if (list.getPlayer(uuid) != null) {
            return BeginResult.fail("hfut.fakePlayerOpen.online", name);
        }
        // the rule follows the identity the data was found under - checked BEFORE
        // session reuse so an existing session cannot bypass the real-player rule
        String ruleKey = premium
                ? FakePlayerOpenStorageSettings.commandRealPlayerOpenStorage
                : FakePlayerOpenStorageSettings.commandFakePlayerOpenStorage;
        if (!CommandUtil.canUseCommand(source, ruleKey)) {
            return BeginResult.fail("hfut.fakePlayerOpen.denied", name, ruleKey);
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
