package dev.whisperlyric.carpet_hfut_addition.mixins.rule.resilientBuddingAmethyst;

import dev.whisperlyric.carpet_hfut_addition.HFUTSettings;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * resilientBuddingAmethyst, 1.21.1+ shape: raises budding amethyst's blast
 * resistance to 1557 (blast-proof). The target method is unchanged across
 * 1.21.1-26.3, so no version gating is needed.
 */
@Mixin(Block.class)
public abstract class BlockMixin {

    @Inject(method = "getExplosionResistance()F", at = @At("HEAD"), cancellable = true)
    private void hfut$resilientBlast(CallbackInfoReturnable<Float> cir) {
        if (HFUTSettings.resilientBuddingAmethyst && (Object) this == Blocks.BUDDING_AMETHYST) {
            cir.setReturnValue(1557.0F);
        }
    }
}
