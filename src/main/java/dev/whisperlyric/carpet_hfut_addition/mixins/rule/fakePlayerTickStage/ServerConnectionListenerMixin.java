package dev.whisperlyric.carpet_hfut_addition.mixins.rule.fakePlayerTickStage;

import dev.whisperlyric.carpet_hfut_addition.helpers.rule.fakePlayerTickStage.FakePlayerTicker;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.server.network.ServerConnectionListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Network phase: runs the deferred fake player doTick right where real players'
 * {@code ServerGamePacketListenerImpl#tick} → {@code ServerPlayer#doTick} happens.
 */
@Restriction(conflict = @Condition("carpet-tis-addition"))
@Mixin(ServerConnectionListener.class)
public abstract class ServerConnectionListenerMixin {
    @Inject(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/List;iterator()Ljava/util/Iterator;",
                    ordinal = 0
            )
    )
    private void hfut$playerPhaseTick(CallbackInfo ci) {
        FakePlayerTicker.getInstance().networkPhaseTick();
    }
}
