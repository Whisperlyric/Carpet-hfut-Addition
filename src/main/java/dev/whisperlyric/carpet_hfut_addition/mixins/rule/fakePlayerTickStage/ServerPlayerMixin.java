package dev.whisperlyric.carpet_hfut_addition.mixins.rule.fakePlayerTickStage;

import carpet.fakes.ServerPlayerInterface;
import carpet.patches.EntityPlayerMPFake;
import dev.whisperlyric.carpet_hfut_addition.helpers.rule.fakePlayerTickStage.FakePlayerTicker;
import dev.whisperlyric.carpet_hfut_addition.helpers.rule.fakePlayerTickStage.FakePlayerTickStage;
import dev.whisperlyric.carpet_hfut_addition.helpers.rule.fakePlayerTickStage.PlayerActionPackCanceller;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Cancels carpet's action-pack tick for fake players deferred to the network
 * phase and hands their action pack to the async-phase ticker. The Before/After
 * pair (priorities 100/10000) wraps carpet's own injection (priority 1000).
 */
public abstract class ServerPlayerMixin {
    @Restriction(conflict = @Condition("carpet-tis-addition"))
    @Mixin(value = ServerPlayer.class, priority = 100)
    public static abstract class Before {
        @Inject(method = "tick", at = @At("HEAD"))
        private void hfut$cancelCarpetActionPackTicking_before(CallbackInfo ci) {
            ServerPlayer self = (ServerPlayer) (Object) this;
            if (self instanceof EntityPlayerMPFake fake
                    && FakePlayerTickStage.effectiveTicksLikeRealPlayer(fake)) {
                PlayerActionPackCanceller.cancelled.set(true);
                FakePlayerTicker.getInstance().addActionPackTick(fake, ((ServerPlayerInterface) fake).getActionPack());
            }
        }
    }

    @Restriction(conflict = @Condition("carpet-tis-addition"))
    @Mixin(value = ServerPlayer.class, priority = 10000)
    public static abstract class After {
        @Inject(method = "tick", at = @At("HEAD"))
        private void hfut$cancelCarpetActionPackTicking_after(CallbackInfo ci) {
            PlayerActionPackCanceller.cancelled.set(false);
        }
    }
}
