package dev.whisperlyric.carpet_hfut_addition.mixins.rule.villagerPricing;

//#if MC < 260300
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.gen.Invoker;
//#endif
import net.minecraft.world.entity.npc.Villager;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Shared accessor for the two villager pricing rules (instant level-up and
 * live price sync): the three vanilla members both rules need, declared
 * once instead of in every mixin. The reset-then-update-then-resend
 * sequence itself lives in VillagerPriceHelper.reprice, which callers cast
 * to this interface - the same accessor + helper shape as
 * MobGoalSelectorAccessor. 26.3 owns all three natively, so the member set
 * is guarded and the interface is empty there.
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
