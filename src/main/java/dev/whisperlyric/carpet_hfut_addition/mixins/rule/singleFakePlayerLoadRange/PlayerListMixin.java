package dev.whisperlyric.carpet_hfut_addition.mixins.rule.singleFakePlayerLoadRange;

import dev.whisperlyric.carpet_hfut_addition.helpers.rule.singleFakePlayerLoadRange.SingleFakePlayerLoadRangeManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Removing a fake player drops its load-range setting and ticket together, so a
 * removed fake cannot leave chunks pinned behind it.
 */
@Mixin(PlayerList.class)
public abstract class PlayerListMixin {

    @Inject(method = "remove", at = @At("HEAD"))
    private void hfut$dropSingleFakePlayerLoadRange(ServerPlayer player, CallbackInfo ci) {
        SingleFakePlayerLoadRangeManager.onPlayerRemoved(player);
    }
}
