package dev.whisperlyric.carpet_hfut_addition.mixins.rule.blockPlacementIgnoreAttachment;

import dev.whisperlyric.carpet_hfut_addition.HFUTSettings;
import dev.whisperlyric.carpet_hfut_addition.helpers.rule.blockPlacementIgnoreAttachment.PlacementScope;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * blockPlacementIgnoreAttachment: pointed dripstone resolves its tip direction
 * through this check before canSurvive is ever consulted.
 */
@Mixin(PointedDripstoneBlock.class)
public abstract class PointedDripstoneBlockMixin {

    @Inject(method = "isValidPointedDripstonePlacement", at = @At("HEAD"), cancellable = true)
    private static void hfut$allowAnyPlacement(LevelReader level, BlockPos pos, Direction direction,
                                               CallbackInfoReturnable<Boolean> cir) {
        if (HFUTSettings.blockPlacementIgnoreAttachment && PlacementScope.active()) {
            cir.setReturnValue(true);
        }
    }
}
