package dev.whisperlyric.carpet_hfut_addition.mixins.rule.fakePlayerTickStage;

import dev.whisperlyric.carpet_hfut_addition.helpers.rule.fakePlayerTickStage.FakePlayerTicker;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Async task phase: ticks the deferred fake player action packs, just like real
 * clients apply their inputs outside the world tick.
 */
@Restriction(conflict = @Condition("carpet-tis-addition"))
@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    @Inject(
            method = "waitUntilNextTick",
            at = @At("HEAD")
    )
    private void hfut$asyncTaskPhaseTick(CallbackInfo ci) {
        FakePlayerTicker.getInstance().asyncTaskPhaseTick();
    }
}
