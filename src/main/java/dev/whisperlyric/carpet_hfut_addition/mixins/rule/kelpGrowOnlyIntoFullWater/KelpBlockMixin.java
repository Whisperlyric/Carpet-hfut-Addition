package dev.whisperlyric.carpet_hfut_addition.mixins.rule.kelpGrowOnlyIntoFullWater;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.whisperlyric.carpet_hfut_addition.HFUTSettings;
import dev.whisperlyric.carpet_hfut_addition.helpers.water.FullWaterPredicate;
import net.minecraft.world.level.block.KelpBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

/**
 * kelpGrowOnlyIntoFullWater: vanilla grows kelp into any water block; with the
 * rule on, only a source or a full (falling) column qualifies. Every growth
 * path, random tick and bonemeal alike, goes through this method.
 */
@Mixin(KelpBlock.class)
public abstract class KelpBlockMixin {

    @WrapMethod(method = "canGrowInto")
    private boolean hfut$growOnlyIntoFullWater(BlockState state, Operation<Boolean> original) {
        return original.call(state) && (!HFUTSettings.kelpGrowOnlyIntoFullWater
                || FullWaterPredicate.isSourceOrFull(state.getFluidState()));
    }
}
