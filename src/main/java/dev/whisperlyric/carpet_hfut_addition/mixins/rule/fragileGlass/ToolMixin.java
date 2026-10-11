package dev.whisperlyric.carpet_hfut_addition.mixins.rule.fragileGlass;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.whisperlyric.carpet_hfut_addition.HFUTSettings;
import net.minecraft.core.HolderSet;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StainedGlassBlock;
import net.minecraft.world.level.block.StainedGlassPaneBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * fragileGlass, 1.21.1+ shape: makes glass, stained glass, tinted glass and glass
 * panes match the mineable/hoe rule, so hoes gain their tool speed bonus
 * (Efficiency included); drops are unchanged. The target and referenced classes are
 * identical across 1.21.1-26.3, so no version gating is needed.
 */
@Mixin(Tool.class)
public abstract class ToolMixin {

    @WrapOperation(method = "getMiningSpeed", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/core/HolderSet;)Z"))
    private static boolean hfut$hoeMatchesGlass(BlockState state, HolderSet<Block> blocks,
                                                Operation<Boolean> original) {
        boolean matched = original.call(state, blocks);
        if (!matched && HFUTSettings.fragileGlass
                && blocks instanceof HolderSet.Named named
                && named.key().equals(BlockTags.MINEABLE_WITH_HOE)) {
            Block block = state.getBlock();
            return state.is(Blocks.GLASS) || state.is(Blocks.TINTED_GLASS) || state.is(Blocks.GLASS_PANE)
                    || block instanceof StainedGlassBlock || block instanceof StainedGlassPaneBlock;
        }
        return matched;
    }
}
