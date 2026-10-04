package dev.whisperlyric.carpet_hfut_addition.mixins.rule.projectileCleanup;

import dev.whisperlyric.carpet_hfut_addition.HFUTSettings;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Shulker bullet portal cleanup: the {@code handlePortal()} call inside
 * {@code tick} is reached only on the tick the bullet stands inside a portal,
 * so this is an event trigger with zero steady-state cost. The weak-chunk
 * cleanup lives in {@link WeakChunkCleanupMixin}.
 */
@Mixin(ShulkerBullet.class)
public abstract class ShulkerBulletMixin {

    //#if MC >= 12102
    //$$ @Inject(
    //$$         method = "tick()V",
    //$$         at = @At(
    //$$                 value = "INVOKE",
    //$$                 target = "Lnet/minecraft/world/entity/projectile/ShulkerBullet;handlePortal()V"
    //$$         ),
    //$$         cancellable = true
    //$$ )
    //$$ private void hfut$clearOnPortal(CallbackInfo ci) {
    //$$     if (!HFUTSettings.shulkerBulletPortalCleanup) {
    //$$         return;
    //$$     }
    //$$     ((ShulkerBullet) (Object) this).discard();
    //$$     ci.cancel(); // cleared; skip the rest of this tick
    //$$ }
    //#endif
}
