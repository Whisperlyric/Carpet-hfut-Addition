package dev.whisperlyric.carpet_hfut_addition.mixins.rule.blockPlacementIgnoreAttachment;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.whisperlyric.carpet_hfut_addition.HFUTSettings;
import dev.whisperlyric.carpet_hfut_addition.helpers.rule.blockPlacementIgnoreAttachment.PlacementScope;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.WallSkullBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * blockPlacementIgnoreAttachment: a wall skull needs a non-replaceable
 * neighbour, so mid-air placement falls back to the standing skull; pretend the
 * neighbour is solid.
 */
@Mixin(WallSkullBlock.class)
public abstract class WallSkullBlockMixin {

    @WrapOperation(method = "getStateForPlacement",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;canBeReplaced(Lnet/minecraft/world/item/context/BlockPlaceContext;)Z"))
    private boolean hfut$pretendNeighbourSolid(BlockState neighbour, BlockPlaceContext context,
                                               Operation<Boolean> original) {
        if (HFUTSettings.blockPlacementIgnoreAttachment && PlacementScope.active()) {
            return false;
        }
        return original.call(neighbour, context);
    }
}
