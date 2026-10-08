package dev.whisperlyric.carpet_hfut_addition.helpers.rule.villagerPricing;

import dev.whisperlyric.carpet_hfut_addition.mixins.rule.villagerPricing.VillagerPriceInvoker;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;

/**
 * Shared by both villager rules: reset first (updateSpecialPrices is
 * additive), re-apply for the trading player, then resend so the new prices
 * show up without a reopen. 26.3 owns the pricing natively, so the body is
 * empty there.
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
