package dev.whisperlyric.carpet_hfut_addition.mixins.rule.projectileCleanup;

import dev.whisperlyric.carpet_hfut_addition.HFUTSettings;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.entity.projectile.WitherSkull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * projectileCleanup: clears wandering projectiles (shulker bullets, wither
 * skulls incl. charged) once they leave the entity-ticking list. Anchored at
 * {@code ServerLevel$EntityCallbacks.onTickingEnd}, the only hook that also sees
 * demoted chunks, and once per transition, so the cost with the rules off is one
 * boolean check.
 */
@Mixin(targets = "net.minecraft.server.level.ServerLevel$EntityCallbacks")
public abstract class WeakChunkCleanupMixin {

    @Inject(method = "onTickingEnd(Lnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"))
    private void hfut$clearOnTickingEnd(Entity entity, CallbackInfo ci) {
        if (entity.isRemoved()) {
            return;
        }
        if (HFUTSettings.shulkerBulletWeakChunkCleanup && entity instanceof ShulkerBullet) {
            entity.discard();
            return;
        }
        if (HFUTSettings.witherSkullCleanup && entity instanceof WitherSkull) {
            entity.discard();
        }
    }
}
