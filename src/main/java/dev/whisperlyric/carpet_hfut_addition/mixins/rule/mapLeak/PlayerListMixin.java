package dev.whisperlyric.carpet_hfut_addition.mixins.rule.mapLeak;

import dev.whisperlyric.carpet_hfut_addition.HFUTSettings;
import dev.whisperlyric.carpet_hfut_addition.helpers.rule.mapLeak.MapLeakHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Root-side fix of the map recipient leak: vanilla's cleanup
 * only runs while the player is still being broadcast to, so departed players
 * stay pinned. {@code PlayerList#remove} fires on real disconnects only.
 */
@Mixin(PlayerList.class)
public abstract class PlayerListMixin {

    @Inject(method = "remove", at = @At("HEAD"))
    private void hfut$purgeMapRegistrations(ServerPlayer player, CallbackInfo ci) {
        if (HFUTSettings.mapRegistryLeakFix) {
            MapLeakHandler.purgePlayer(player);
        }
    }
}
