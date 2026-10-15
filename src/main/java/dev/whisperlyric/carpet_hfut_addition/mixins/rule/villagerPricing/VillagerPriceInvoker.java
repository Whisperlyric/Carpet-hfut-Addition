package dev.whisperlyric.carpet_hfut_addition.mixins.rule.villagerPricing;

//#if MC < 260300
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.gen.Invoker;
//#endif
import net.minecraft.world.entity.npc.Villager;
import org.spongepowered.asm.mixin.Mixin;

/**
 * villagerPricing: shared accessor for the two villager pricing rules (instant
 * level-up and live price sync), declaring the three vanilla members both need
 * once. The reset-update-resend sequence lives in VillagerPriceHelper.reprice;
 * 26.3 owns all three natively, so the member set is guarded and empty there.
 */
@Mixin(Villager.class)
public interface VillagerPriceInvoker {

    //#if MC < 260300
    @Invoker("updateSpecialPrices")
    void hfut$updateSpecialPrices(Player player);

    @Invoker("resetSpecialPrices")
    void hfut$resetSpecialPrices();

    /** Vanilla's offers-packet push to the trading player (self-guarding). */
    @Invoker("resendOffersToTradingPlayer")
    void hfut$resendOffers();
    //#endif
}
