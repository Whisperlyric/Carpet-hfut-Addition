package dev.whisperlyric.carpet_hfut_addition.mixins.rule.blockPlacementIgnoreAttachment;

import dev.whisperlyric.carpet_hfut_addition.HFUTSettings;
import dev.whisperlyric.carpet_hfut_addition.helpers.rule.blockPlacementIgnoreAttachment.PlacementScope;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.BambooStalkBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * blockPlacementIgnoreAttachment: bamboo's getStateForPlacement returns null
 * unless the block below is in {@code #supports_bamboo} and the spot is free of
 * fluid, so fall back to a plain stalk.
 */
@Mixin(BambooStalkBlock.class)
public abstract class BambooStalkBlockMixin {

    @Inject(method = "getStateForPlacement", at = @At("RETURN"), cancellable = true)
    private void hfut$bambooPlaceBySupport(BlockPlaceContext context, CallbackInfoReturnable<BlockState> cir) {
        if (!HFUTSettings.blockPlacementIgnoreAttachment || !PlacementScope.active()) {
            return;
        }
        BlockPos pos = context.getClickedPos();
        BlockState below = context.getLevel().getBlockState(pos.below());
        if (below.is(Blocks.BAMBOO) || below.is(Blocks.BAMBOO_SAPLING)) {
            return; // vanilla growth chain: keep the vanilla result as-is
        }
        if (below.isAir()) {
            cir.setReturnValue(Blocks.BAMBOO.defaultBlockState()); // plain stalk, no support
        } else {
            cir.setReturnValue(Blocks.BAMBOO_SAPLING.defaultBlockState()); // plant a shoot on any block
        }
    }
}
