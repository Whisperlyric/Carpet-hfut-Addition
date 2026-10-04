package dev.whisperlyric.carpet_hfut_addition.mixins.rule.fakePlayerTickStage;

import carpet.helpers.EntityPlayerActionPack;
import dev.whisperlyric.carpet_hfut_addition.helpers.rule.fakePlayerTickStage.PlayerActionPackCanceller;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Restriction(conflict = @Condition("carpet-tis-addition"))
@Mixin(EntityPlayerActionPack.class)
public abstract class EntityPlayerActionPackMixin {
    @Inject(method = "onUpdate", at = @At("HEAD"), cancellable = true, remap = false)
    private void hfut$dontTickActionPackAtEntityPhase(CallbackInfo ci) {
        if (PlayerActionPackCanceller.cancelled.get()) {
            ci.cancel();
        }
    }
}
