package dev.whisperlyric.carpet_hfut_addition.mixins.rule.namedTrader;

import dev.whisperlyric.carpet_hfut_addition.HFUTSettings;
import net.minecraft.network.chat.Component;
//#if MC >= 12111
//$$ import net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;
//#else
import net.minecraft.world.entity.npc.WanderingTrader;
//#endif
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * namedWanderingTraderPersistence: cancels the critical despawn tick (delay 1→0)
 * of a wandering trader whose custom name matches the rule. Freezing any earlier
 * tick would also extend the trader llama's life, which syncs to despawnDelay-1.
 * Injection after Carpet-Ice-Addition; string matching and the three-state rule
 * after Carpet-Igny-Addition (both LGPL-3.0).
 */
@Mixin(WanderingTrader.class)
public abstract class WanderingTraderMixin {

    @Inject(method = "maybeDespawn", at = @At("HEAD"), cancellable = true)
    private void hfut$namedTraderPersistence(CallbackInfo ci) {
        String rule = HFUTSettings.namedWanderingTraderPersistence;
        if (rule.equals("false") || rule.isEmpty()) {
            return;
        }
        WanderingTrader trader = (WanderingTrader) (Object) this;
        if (trader.getDespawnDelay() != 1 || trader.isTrading()) {
            return;
        }
        Component name = trader.getCustomName();
        if (name == null) {
            return;
        }
        if (rule.equals("true") || name.getString().equals(rule)) {
            ci.cancel();
        }
    }
}
