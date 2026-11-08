package dev.whisperlyric.carpet_hfut_addition.mixins.rule.blockPlacementIgnoreAttachment;

import dev.whisperlyric.carpet_hfut_addition.HFUTSettings;
import dev.whisperlyric.carpet_hfut_addition.helpers.rule.blockPlacementIgnoreAttachment.PlacementScope;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * blockPlacementIgnoreAttachment: glow lichen and sculk veins reject a face in
 * getStateForPlacement via isValidStateForPlacement, never reaching canSurvive.
 */
@Mixin(MultifaceBlock.class)
public abstract class MultifaceBlockMixin {

    @Inject(method = "isValidStateForPlacement", at = @At("HEAD"), cancellable = true)
    private void hfut$allowAnyFace(BlockGetter level, BlockState state, BlockPos pos, Direction direction,
                                   CallbackInfoReturnable<Boolean> cir) {
        if (HFUTSettings.blockPlacementIgnoreAttachment && PlacementScope.active()) {
            cir.setReturnValue(true);
        }
    }
}
