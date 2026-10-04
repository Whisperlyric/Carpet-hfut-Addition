package dev.whisperlyric.carpet_hfut_addition.helpers.rule.fakePlayerTickStage;

import carpet.patches.EntityPlayerMPFake;
import dev.whisperlyric.carpet_hfut_addition.HFUTServer;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

/**
 * Bridge into carpet-tis-addition: TIS reads its own static rule field, so a
 * shadow of the true global value is kept and the per-player value is written
 * into TIS's field around each overridden fake player's tick.
 */
public final class TisBridge {
    private static final String SETTINGS_CLASS = "carpettisaddition.CarpetTISAdditionSettings";
    private static final String FIELD_NAME = "fakePlayerTicksLikeRealPlayer";

    private static volatile Field tisField;
    private static volatile boolean shadowGlobal;

    private TisBridge() {
    }

    /** The true global value of TIS's rule, unaffected by per-player flips. */
    public static boolean shadowGlobal() {
        return shadowGlobal;
    }

    public static void setShadowGlobal(boolean value) {
        shadowGlobal = value;
    }

    /** Re-reads TIS's field into the shadow; called at server load before any tick. */
    public static void refreshFromField() {
        Field f = field();
        if (f != null) {
            try {
                shadowGlobal = f.getBoolean(null);
            } catch (IllegalAccessException e) {
                HFUTServer.LOGGER.warn("[HFUT] Failed to read carpet-tis-addition's fakePlayerTicksLikeRealPlayer", e);
            }
        }
    }

    /** Writes the desired effective stage into TIS's field; call at HEAD of the tick. */
    public static void enterTick(EntityPlayerMPFake fake, boolean desiredEffective) {
        Field f = field();
        if (f == null) {
            return;
        }
        try {
            f.setBoolean(null, desiredEffective);
        } catch (IllegalAccessException e) {
            HFUTServer.LOGGER.warn("[HFUT] Failed to flip carpet-tis-addition's rule field", e);
        }
    }

    /** Restores TIS's field to the shadowed global; call at RETURN of the tick. */
    public static void exitTick() {
        Field f = field();
        if (f != null) {
            try {
                f.setBoolean(null, shadowGlobal);
            } catch (IllegalAccessException e) {
                HFUTServer.LOGGER.warn("[HFUT] Failed to restore carpet-tis-addition's rule field", e);
            }
        }
    }

    @Nullable
    private static Field field() {
        Field f = tisField;
        if (f == null) {
            try {
                f = Class.forName(SETTINGS_CLASS).getField(FIELD_NAME);
                tisField = f;
            } catch (ReflectiveOperationException e) {
                HFUTServer.LOGGER.warn("[HFUT] carpet-tis-addition settings class not found; bridge disabled", e);
            }
        }
        return f;
    }
}
