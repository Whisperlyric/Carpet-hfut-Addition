package dev.whisperlyric.carpet_hfut_addition.mixins.rule.fakePlayerTickStage;

import carpet.patches.EntityPlayerMPFake;
import dev.whisperlyric.carpet_hfut_addition.helpers.rule.fakePlayerTickStage.FakePlayerTickStage;
import dev.whisperlyric.carpet_hfut_addition.helpers.rule.fakePlayerTickStage.TisBridge;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * TIS-delegation mode: for fake players with a per-player override, write the
 * desired stage into TIS's rule field for the duration of their tick, so TIS's
 * own mixins give them the overridden behaviour.
 */
@Restriction(require = @Condition("carpet-tis-addition"))
@Mixin(EntityPlayerMPFake.class)
public abstract class TisBridgeMixin {
    @Inject(method = "tick", at = @At("HEAD"))
    private void hfut$tisBridge_enter(CallbackInfo ci) {
        EntityPlayerMPFake fake = (EntityPlayerMPFake) (Object) this;
        if (FakePlayerTickStage.modeOf(fake) != FakePlayerTickStage.StageMode.GLOBAL) {
            TisBridge.enterTick(fake, FakePlayerTickStage.effectiveTicksLikeRealPlayer(fake));
        }
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void hfut$tisBridge_exit(CallbackInfo ci) {
        EntityPlayerMPFake fake = (EntityPlayerMPFake) (Object) this;
        if (FakePlayerTickStage.modeOf(fake) != FakePlayerTickStage.StageMode.GLOBAL) {
            TisBridge.exitTick();
        }
    }
}
