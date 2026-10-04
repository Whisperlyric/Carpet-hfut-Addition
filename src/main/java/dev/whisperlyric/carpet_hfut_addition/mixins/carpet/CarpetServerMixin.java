package dev.whisperlyric.carpet_hfut_addition.mixins.carpet;

import carpet.CarpetExtension;
import carpet.CarpetServer;
import dev.whisperlyric.carpet_hfut_addition.HFUTServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Diagnostic mixin: logs each Carpet extension as it gets registered. */
@Mixin(CarpetServer.class)
public abstract class CarpetServerMixin {
    @Inject(method = "manageExtension", at = @At("HEAD"))
    private static void hfut$onManageExtension(CarpetExtension extension, CallbackInfo ci) {
        HFUTServer.LOGGER.debug("[HFUT] Carpet extension registered: {}", extension.version());
    }
}
