package dev.whisperlyric.carpet_hfut_addition.mixins.rule.windChargeNoNonLivingKnockback;

//#if MC < 12102
import dev.whisperlyric.carpet_hfut_addition.HFUTSettings;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.windcharge.AbstractWindCharge;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
//#endif
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import org.spongepowered.asm.mixin.Mixin;

/**
 * windChargeNoNonLivingKnockback, 1.21.1 shape: skips Explosion.explode's
 * velocity write for non-living entities. On 1.21.2+ the entity loop lives in
 * ServerExplosion (sibling mixin); below 12102 this member set is stripped and
 * the mixin is restricted to <1.21.2 with a string target.
 */
@Restriction(require = @Condition(value = "minecraft", versionPredicates = "<1.21.2"))
@Mixin(targets = "net.minecraft.world.level.Explosion")
public abstract class ExplosionMixin {

    //#if MC < 12102
    @Shadow
    @Final
    private Entity source;

    @WrapOperation(method = "explode",
                   at = @At(value = "INVOKE",
                            target = "Lnet/minecraft/world/entity/Entity;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V"))
    private void hfut$noNonLivingKnockback(Entity entity, Vec3 velocity, Operation<Void> original) {
        if (HFUTSettings.windChargeNoNonLivingKnockback
                && this.source instanceof AbstractWindCharge
                && !(entity instanceof LivingEntity)) {
            return;
        }
        original.call(entity, velocity);
    }
    //#endif
}
