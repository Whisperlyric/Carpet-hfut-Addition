package dev.whisperlyric.carpet_hfut_addition.mixins.rule.singleFakePlayerLoadRange;

import dev.whisperlyric.carpet_hfut_addition.helpers.rule.singleFakePlayerLoadRange.SingleFakePlayerLoadRangeManager;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * {@code ChunkMap#skipPlayer} runs at the top of every move, once per chunk
 * update, and its result decides whether vanilla adds the player's own ticket.
 * Answering {@code true} there is what drops that ticket; it is also the one
 * place we refresh our own, so changes, repairs and resets all happen here.
 */
@Mixin(ChunkMap.class)
public abstract class ChunkMapMixin {

    @Inject(method = "skipPlayer", at = @At("HEAD"), cancellable = true)
    private void hfut$singleFakePlayerLoadRange(ServerPlayer player, CallbackInfoReturnable<Boolean> cir) {
        if (SingleFakePlayerLoadRangeManager.maintain(player)) {
            cir.setReturnValue(true);
        }
    }
}
