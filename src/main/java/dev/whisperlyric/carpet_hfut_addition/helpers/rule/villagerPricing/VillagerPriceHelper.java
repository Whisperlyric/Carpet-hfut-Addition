package dev.whisperlyric.carpet_hfut_addition.helpers.rule.villagerPricing;

import dev.whisperlyric.carpet_hfut_addition.mixins.rule.villagerPricing.VillagerPriceInvoker;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;

/**
 * The shared pricing action of both villager rules: reset the additive
 * special-price diff first (vanilla only resets when the screen closes),
 * re-apply it for the trading player, then push the offers packet so the
 * new prices show up without a reopen. Idempotent, so the two rules stay
 * independent and can stack freely. Guarded like the invoker: 26.3 owns the
 * pricing natively, so the body is empty there, and the same guard on the
 * callers keeps the class compilable on every node.
 */
public final class VillagerPriceHelper {

    private VillagerPriceHelper() {
    }

    public static void reprice(Villager villager, Player player) {
        //#if MC < 260300
        if (player == null) {
            return;
        }
        VillagerPriceInvoker inv = (VillagerPriceInvoker) villager;
        inv.hfut$resetSpecialPrices();
        inv.hfut$updateSpecialPrices(player);
        inv.hfut$resendOffers();
        //#endif
    }
}
