package dev.whisperlyric.carpet_hfut_addition.mixins.rule.windChargeNoNonLivingKnockback;

//#if MC >= 12102
//$$ import dev.whisperlyric.carpet_hfut_addition.HFUTSettings;
//$$ import net.minecraft.world.entity.Entity;
//$$ import net.minecraft.world.entity.LivingEntity;
//$$ import net.minecraft.world.entity.projectile.windcharge.AbstractWindCharge;
//$$ import net.minecraft.world.phys.Vec3;
//$$ import org.spongepowered.asm.mixin.Final;
//$$ import org.spongepowered.asm.mixin.Shadow;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
//$$ import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
//#if MC >= 12109
//$$ import net.minecraft.world.entity.projectile.Projectile;
//#endif
//#endif
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import org.spongepowered.asm.mixin.Mixin;

/**
 * windChargeNoNonLivingKnockback, 1.21.2+ shape: skips hurtEntities' velocity
 * write for non-living entities - setDeltaMovement on 1.21.2-1.21.3, push on
 * 1.21.4-26.2, pushFromExplosion on 26.3 - plus the 1.21.9+ projectile-owner
 * claim, which only ever hits projectiles.
 */
@Restriction(require = @Condition(value = "minecraft", versionPredicates = ">=1.21.2"))
@Mixin(targets = "net.minecraft.world.level.ServerExplosion")
public abstract class ServerExplosionMixin {

    //#if MC >= 12102
    //$$ @Shadow
    //$$ @Final
    //$$ private Entity source;

    //$$ private boolean hfut$nonLivingWindChargeTarget(Entity entity) {
    //$$     return HFUTSettings.windChargeNoNonLivingKnockback
    //$$             && this.source instanceof AbstractWindCharge
    //$$             && !(entity instanceof LivingEntity);
    //$$ }

    //$$ private boolean hfut$isWindChargeSource() {
    //$$     return HFUTSettings.windChargeNoNonLivingKnockback && this.source instanceof AbstractWindCharge;
    //$$ }

    //#if MC < 12104
    //$$ @WrapOperation(method = "hurtEntities",
    //$$                at = @At(value = "INVOKE",
    //$$                         target = "Lnet/minecraft/world/entity/Entity;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V"))
    //$$ private void hfut$noNonLivingKnockback(Entity entity, Vec3 velocity, Operation<Void> original) {
    //$$     if (!this.hfut$nonLivingWindChargeTarget(entity)) {
    //$$         original.call(entity, velocity);
    //$$     }
    //$$ }
    //#else
    //#if MC < 260300
    //$$ @WrapOperation(method = "hurtEntities",
    //$$                at = @At(value = "INVOKE",
    //$$                         target = "Lnet/minecraft/world/entity/Entity;push(Lnet/minecraft/world/phys/Vec3;)V"))
    //$$ private void hfut$noNonLivingKnockback(Entity entity, Vec3 velocity, Operation<Void> original) {
    //$$     if (!this.hfut$nonLivingWindChargeTarget(entity)) {
    //$$         original.call(entity, velocity);
    //$$     }
    //$$ }
    //#else
    //$$ @WrapOperation(method = "hurtEntities",
    //$$                at = @At(value = "INVOKE",
    //$$                         target = "Lnet/minecraft/world/entity/Entity;pushFromExplosion(Lnet/minecraft/world/phys/Vec3;)V"))
    //$$ private void hfut$noNonLivingKnockback(Entity entity, Vec3 velocity, Operation<Void> original) {
    //$$     if (!this.hfut$nonLivingWindChargeTarget(entity)) {
    //$$         original.call(entity, velocity);
    //$$     }
    //$$ }
    //#endif
    //#if MC >= 12109
    //$$ @WrapOperation(method = "hurtEntities",
    //$$                at = @At(value = "INVOKE",
    //$$                         target = "Lnet/minecraft/world/entity/projectile/Projectile;setOwner(Lnet/minecraft/world/entity/Entity;)V"))
    //$$ private void hfut$noProjectileOwnershipClaim(Projectile projectile, Entity owner, Operation<Void> original) {
    //$$     if (!this.hfut$isWindChargeSource()) {
    //$$         original.call(projectile, owner);
    //$$     }
    //$$ }
    //#endif
    //#endif
    //#endif
}
