package dev.whisperlyric.carpet_hfut_addition.mixins.rule.ghostPearl;

import dev.whisperlyric.carpet_hfut_addition.helpers.rule.ghostPearl.PearlTraceHandler;
import net.minecraft.world.entity.Entity;
//#if MC >= 12111
//$$ import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownEnderpearl;
//#else
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
//#endif
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Trace layer: a thrown pearl being discarded is the teleport moment. Recorded
 * here with origin and per-UUID count, so a 2+ count proves ghost replay.
 */
@Mixin(targets = {
        //#if MC >= 12111
        //$$ "net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownEnderpearl"
        //#else
        "net.minecraft.world.entity.projectile.ThrownEnderpearl"
        //#endif
})
public abstract class ThrownEnderpearlMixin {
    //#if MC >= 12102
    //$$ @Inject(method = "onRemoval", at = @At("HEAD"))
    //$$ private void hfut$onPearlRemoved(Entity.RemovalReason reason, CallbackInfo ci) {
    //$$     if (reason == Entity.RemovalReason.DISCARDED) {
    //$$         PearlTraceHandler.onPearlDiscard((ThrownEnderpearl) (Object) this);
    //$$     }
    //$$ }
    //#endif
}
