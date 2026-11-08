package dev.whisperlyric.carpet_hfut_addition.mixins.rule.blockPlacementIgnoreAttachment;

import dev.whisperlyric.carpet_hfut_addition.HFUTSettings;
import dev.whisperlyric.carpet_hfut_addition.helpers.rule.blockPlacementIgnoreAttachment.PlacementScope;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * blockPlacementIgnoreAttachment: while the placement scope is open, every
 * attachment test answers yes. Outside it (neighbour updates, structure
 * placement) the vanilla check still runs.
 */
@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateBaseMixin {

    @Inject(method = "canSurvive", at = @At("HEAD"), cancellable = true)
    private void hfut$ignoreAttachmentCheck(LevelReader level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (HFUTSettings.blockPlacementIgnoreAttachment && PlacementScope.active()) {
            cir.setReturnValue(true);
        }
    }
}
