package dev.whisperlyric.carpet_hfut_addition.helpers.compat.guglecarpetaddition;

import net.fabricmc.loader.api.FabricLoader;

/** Runtime presence check for Carpet GugleCarpetAddition (GCA). */
public final class GcaGuard {
    private GcaGuard() {
    }

    public static boolean present() {
        return FabricLoader.getInstance().isModLoaded("guglecarpetaddition");
    }
}
