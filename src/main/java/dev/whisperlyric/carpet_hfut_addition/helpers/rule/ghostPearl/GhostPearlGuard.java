package dev.whisperlyric.carpet_hfut_addition.helpers.rule.ghostPearl;

import dev.whisperlyric.carpet_hfut_addition.GhostPearlFixSettings;
import dev.whisperlyric.carpet_hfut_addition.HFUTSettings;
import net.fabricmc.loader.api.FabricLoader;

/**
 * Ruling for the ghost pearl rules (MC-306936): the fix is active only when
 * Carpet IGNY Addition is absent; the trace is always active on 1.21.2+.
 */
public final class GhostPearlGuard {
    private GhostPearlGuard() {
    }

    public static boolean ignyPresent() {
        return FabricLoader.getInstance().isModLoaded("carpet-igny-addition");
    }

    public static boolean fixActive() {
        //#if MC >= 12102
        //$$ return !ignyPresent() && GhostPearlFixSettings.ghostEnderPearlFix;
        //#else
        return false;
        //#endif
    }

    public static boolean traceActive() {
        //#if MC >= 12102
        //$$ return HFUTSettings.ghostEnderPearlTrace;
        //#else
        return false;
        //#endif
    }
}
