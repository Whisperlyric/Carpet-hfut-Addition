package dev.whisperlyric.carpet_hfut_addition.mixins.rule.fakePlayerTickStage;

import carpet.patches.EntityPlayerMPFake;
import dev.whisperlyric.carpet_hfut_addition.helpers.rule.fakePlayerTickStage.FakePlayerTicker;
import dev.whisperlyric.carpet_hfut_addition.helpers.rule.fakePlayerTickStage.FakePlayerTickStage;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Entity stage: {@code EntityPlayerMPFake#tick} runs inside the ServerLevel entity
 * tick loop. When this fake player's effective tick-stage is "like a real player",
 * the doTick call here is deferred to the network phase instead.
 */
@Restriction(conflict = @Condition("carpet-tis-addition"))
@Mixin(EntityPlayerMPFake.class)
public abstract class EntityPlayerMPFakeMixin {
    @WrapOperation(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lcarpet/patches/EntityPlayerMPFake;doTick()V"
            )
    )
    private void hfut$deferDoTickToPlayerPhase(EntityPlayerMPFake player, Operation<Void> original) {
        if (FakePlayerTickStage.effectiveTicksLikeRealPlayer(player)) {
            FakePlayerTicker.getInstance().addPlayerEntityTick(player);
            return;
        }
        original.call(player);
    }
}
