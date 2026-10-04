package dev.whisperlyric.carpet_hfut_addition.mixins.rule.enderDragonFlight;

import dev.whisperlyric.carpet_hfut_addition.HFUTSettings;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * enderDragonVerticalVelocityFix (MC-272431): since 19w08b the vertical
 * acceleration toward the fly target is scaled by 0.01 instead of 0.1
 * (1.12 used 0.1), so the dragon can barely climb or dive and circles its
 * target nodes. The 0.01 constant occurs exactly once in aiStep on every
 * supported version; restore it to 0.1.
 */
@Mixin(EnderDragon.class)
public abstract class EnderDragonMixin {

    @ModifyConstant(method = "aiStep", constant = @Constant(doubleValue = 0.01))
    private double hfut$verticalAcceleration(double original) {
        return HFUTSettings.enderDragonVerticalVelocityFix ? 0.1 : original;
    }
}
