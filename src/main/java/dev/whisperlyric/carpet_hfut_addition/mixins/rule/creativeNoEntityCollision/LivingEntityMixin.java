package dev.whisperlyric.carpet_hfut_addition.mixins.rule.creativeNoEntityCollision;

import dev.whisperlyric.carpet_hfut_addition.HFUTSettings;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Creative player-side entity collision disable.
 *
 * <p>isPushable: Creative player returns false, excluded from EntitySelector.pushableBy push lists.
 * Mobs, boats and minecarts won't push the player, and cramming ignores them.
 *
 * <p>pushEntities: The player still runs its own push loop calling doPush on pushable entities.
 * Some entities like minecarts push back via overloads.
 * Blocking this entry point stops all mutual pushing and cramming damage.
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(method = "isPushable", at = @At("HEAD"), cancellable = true)
    private void hfut$creativeNotPushable(CallbackInfoReturnable<Boolean> cir) {
        if (HFUTSettings.creativeNoEntityCollision && isRuleCreative((LivingEntity) (Object) this)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "pushEntities", at = @At("HEAD"), cancellable = true)
    private void hfut$creativeSkipsPushEntities(CallbackInfo ci) {
        if (HFUTSettings.creativeNoEntityCollision && isRuleCreative((LivingEntity) (Object) this)) {
            ci.cancel();
        }
    }

    @Unique
    private static boolean isRuleCreative(LivingEntity entity) {
        return entity instanceof Player player && player.isCreative() && !player.isSpectator();
    }
}
