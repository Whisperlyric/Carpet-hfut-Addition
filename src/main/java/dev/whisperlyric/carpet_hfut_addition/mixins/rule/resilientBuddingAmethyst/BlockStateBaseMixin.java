package dev.whisperlyric.carpet_hfut_addition.mixins.rule.resilientBuddingAmethyst;

import dev.whisperlyric.carpet_hfut_addition.HFUTSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * resilientBuddingAmethyst, 1.21.1+ shape: raises budding amethyst's hardness
 * (mining-time base) to 1557. The target method is unchanged across 1.21.1-26.3,
 * so no version gating is needed.
 */
@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateBaseMixin {

    @Inject(method = "getDestroySpeed(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)F",
            at = @At("HEAD"), cancellable = true)
    private void hfut$resilientHardness(CallbackInfoReturnable<Float> cir) {
        if (HFUTSettings.resilientBuddingAmethyst
                && ((BlockBehaviour.BlockStateBase) (Object) this).getBlock() == Blocks.BUDDING_AMETHYST) {
            cir.setReturnValue(1557.0F);
        }
    }
}
