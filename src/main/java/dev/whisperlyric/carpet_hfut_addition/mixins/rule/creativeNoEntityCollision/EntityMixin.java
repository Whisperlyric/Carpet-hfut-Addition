package dev.whisperlyric.carpet_hfut_addition.mixins.rule.creativeNoEntityCollision;

import dev.whisperlyric.carpet_hfut_addition.HFUTSettings;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * creativeNoEntityCollision, push half.
 *
 * <p>Entity.push(Entity) is the generic shove used by mobs and boats via VehicleEntity.push.
 * Cancelling at its entry when either side is a rule creative player disables pushes in both directions.
 *
 * <p>Movement (passing through boats, shulkers and crowds) is handled in EntityGetterMixin,
 * which empties EntityGetter.getEntityCollisions. This avoids conflicts on Entity.collide
 * call-site instructions also injected by other mods such as Lithium.
 *
 * <p>Entities bypassing Entity.push(Entity), e.g. minecarts calling Entity.push(double,double,double)
 * for themselves and the player, are handled by pushEntities cancellation in LivingEntityMixin.
 */
@Mixin(Entity.class)
public abstract class EntityMixin {

    @Inject(method = "push(Lnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"), cancellable = true)
    private void hfut$noEntityPush(Entity other, CallbackInfo ci) {
        if (HFUTSettings.creativeNoEntityCollision
                && (isRuleCreative((Entity) (Object) this) || isRuleCreative(other))) {
            ci.cancel();
        }
    }

    @Unique
    private static boolean isRuleCreative(Entity entity) {
        return entity instanceof Player player && player.isCreative() && !player.isSpectator();
    }
}
