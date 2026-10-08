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
 * of a wandering trader whose custom name exactly matches the rule. Only that tick
 * is cancelled — the trader llama syncs its timer to despawnDelay-1 every tick, so
 * freezing earlier would extend the llama's life too.
 *
 * <p>Injection point and critical-tick approach based on
 * Carpet-Ice-Addition (LGPL-3.0, Ice2974); the string matching and
 * three-state rule are modeled after Carpet-Igny-Addition (LGPL-3.0).
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
