package dev.whisperlyric.carpet_hfut_addition.mixins.rule.miningFatigue;

import dev.whisperlyric.carpet_hfut_addition.HFUTSettings;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * miningFatigueDigSpeedFix (below 26.3): the dig-speed table under mining
 * fatigue holds 0.0027 / 8.1E-4 for amplifier 2 / 3+, ten times lower than
 * the intended 0.3^(amplifier+1). Patch the two wrong entries back to
 * 0.027 / 0.0081, matching the 26.3 fix. Each constant occurs exactly
 * once in this method on every version below 26.3.
 */
@Mixin(Player.class)
public abstract class PlayerMixin {

    //#if MC < 260300
    @ModifyConstant(method = "getDestroySpeed", constant = @Constant(floatValue = 0.0027F))
    private float hfut$fatigueTierThree(float original) {
        return HFUTSettings.miningFatigueDigSpeedFix ? 0.027F : original;
    }

    @ModifyConstant(method = "getDestroySpeed", constant = @Constant(floatValue = 8.1E-4F))
    private float hfut$fatigueTierFourPlus(float original) {
        return HFUTSettings.miningFatigueDigSpeedFix ? 0.0081F : original;
    }
    //#endif
}
