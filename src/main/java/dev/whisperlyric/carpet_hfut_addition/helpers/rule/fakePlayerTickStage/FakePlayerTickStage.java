package dev.whisperlyric.carpet_hfut_addition.helpers.rule.fakePlayerTickStage;

import carpet.patches.EntityPlayerMPFake;
import dev.whisperlyric.carpet_hfut_addition.HFUTServer;
import dev.whisperlyric.carpet_hfut_addition.FakePlayerTickStageSettings;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Per-fake-player tick stage override of the global
 * {@code fakePlayerTicksLikeRealPlayer} rule (runtime-only). Without
 * carpet-tis-addition our own mixins implement the move; with it, TisBridge
 * writes the per-player value into TIS's field for the overridden tick.
 */
public class FakePlayerTickStage {
    /** GLOBAL: follow the rule (default); INVERT: opposite; ORIGIN: entity phase; LIKE_REAL: network phase. */
    public enum StageMode {
        GLOBAL, INVERT, ORIGIN, LIKE_REAL
    }

    private static final Map<UUID, StageMode> MODES = new ConcurrentHashMap<>();

    public static boolean isTisPresent() {
        return FabricLoader.getInstance().isModLoaded("carpet-tis-addition");
    }

    /** The global rule value, whichever mod owns it. */
    public static boolean globalTicksLikeRealPlayer() {
        if (isTisPresent()) {
            return TisBridge.shadowGlobal();
        }
        return FakePlayerTickStageSettings.fakePlayerTicksLikeRealPlayer;
    }

    public static StageMode modeOf(ServerPlayer player) {
        return MODES.getOrDefault(player.getUUID(), StageMode.GLOBAL);
    }

    /** The value that actually governs this fake player's tick stage. */
    public static boolean effectiveTicksLikeRealPlayer(ServerPlayer player) {
        boolean global = globalTicksLikeRealPlayer();
        return switch (modeOf(player)) {
            case INVERT -> !global;
            case ORIGIN -> false;
            case LIKE_REAL -> true;
            case GLOBAL -> global;
        };
    }

    public static StageMode setMode(EntityPlayerMPFake fake, StageMode mode) {
        if (mode == StageMode.GLOBAL) {
            MODES.remove(fake.getUUID());
        } else {
            MODES.put(fake.getUUID(), mode);
        }
        HFUTServer.LOGGER.debug("[HFUT] fakePlayerTickStage for {}: {}", fake.getName(), mode);
        return mode;
    }

    public static void clearOnServerStop() {
        MODES.clear();
    }
}
