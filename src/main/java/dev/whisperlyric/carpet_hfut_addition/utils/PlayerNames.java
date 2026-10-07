package dev.whisperlyric.carpet_hfut_addition.utils;

import net.minecraft.server.level.ServerPlayer;

/**
 * The one authlib-version branch of the project: GameProfile#getName was
 * renamed to name() in 1.21.10, and authlib is not part of the Minecraft
 * mapping chain, so the preprocessor cannot derive the rename (an
 * ExtraMapping entry fails with "Failed to find mapping for source
 * class"). Every display of a player name goes through here.
 */
public final class PlayerNames {

    private PlayerNames() {
    }

    public static String of(ServerPlayer player) {
        //#if MC >= 12110
        //$$ return player.getGameProfile().name();
        //#else
        return player.getGameProfile().getName();
        //#endif
    }
}
