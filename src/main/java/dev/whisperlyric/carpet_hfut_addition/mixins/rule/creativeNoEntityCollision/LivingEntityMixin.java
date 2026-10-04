package dev.whisperlyric.carpet_hfut_addition.mixins.rule.creativeNoEntityCollision;

import dev.whisperlyric.carpet_hfut_addition.HFUTSettings;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * creativeNoEntityCollision, push half: isPushable is the gate vanilla
 * consults before entities shove each other. Reporting false for creative
 * players makes pushing inert both ways, matching "no entity collision".
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(method = "isPushable", at = @At("HEAD"), cancellable = true)
    private void hfut$creativeNotPushable(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (HFUTSettings.creativeNoEntityCollision
                && self instanceof Player player && player.isCreative() && !player.isSpectator()) {
            cir.setReturnValue(false);
        }
    }
}
