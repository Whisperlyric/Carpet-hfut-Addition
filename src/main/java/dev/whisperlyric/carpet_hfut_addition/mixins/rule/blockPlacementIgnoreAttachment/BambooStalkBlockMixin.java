package dev.whisperlyric.carpet_hfut_addition.mixins.rule.blockPlacementIgnoreAttachment;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.whisperlyric.carpet_hfut_addition.HFUTSettings;
import dev.whisperlyric.carpet_hfut_addition.helpers.rule.blockPlacementIgnoreAttachment.PlacementScope;
import net.minecraft.world.level.block.BambooStalkBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * blockPlacementIgnoreAttachment: bamboo with nothing to stand on falls back to
 * a bamboo sapling, which is not what "place this block here" means. When the
 * rule is on, keep the stalk the player is holding.
 */
@Mixin(BambooStalkBlock.class)
public abstract class BambooStalkBlockMixin {

    @ModifyReturnValue(method = "getStateForPlacement", at = @At("RETURN"))
    private BlockState hfut$keepStalk(BlockState placed) {
        if (placed != null && HFUTSettings.blockPlacementIgnoreAttachment
                && PlacementScope.active() && placed.is(Blocks.BAMBOO_SAPLING)) {
            return Blocks.BAMBOO.defaultBlockState();
        }
        return placed;
    }
}
