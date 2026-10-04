package dev.whisperlyric.carpet_hfut_addition.mixins.rule.villagerLivePriceSync;

//#if MC < 260300
import dev.whisperlyric.carpet_hfut_addition.HFUTSettings;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.village.ReputationEventType;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#endif
import net.minecraft.world.entity.npc.Villager;
import org.spongepowered.asm.mixin.Mixin;

/**
 * villagerLivePriceSync (below 26.3): up to 26.2 a mid-trade price event
 * (restock, demand catch-up, gossip, reputation) stays invisible until the
 * screen is reopened; 26.3 reprices on each event, and the four RETURN
 * injections here do the same. updateSpecialPrices is additive, so each
 * reprice resets first (the idempotency trick 26.3 moved into the method
 * head). Plain class/gossip renames are left to the preprocessor.
 */
@Mixin(Villager.class)
public abstract class VillagerMixin {

    //#if MC < 260300
    @Invoker("updateSpecialPrices")
    abstract void hfut$updateSpecialPrices(Player player);

    @Invoker("resetSpecialPrices")
    abstract void hfut$resetSpecialPrices();

    /**
     * Vanilla's own "push current offers to the trading player" helper; needed
     * because vanilla's resend inside restock/catchUpDemand runs before our
     * RETURN reprice, and gossip/reputation never send at all.
     */
    @Invoker("resendOffersToTradingPlayer")
    abstract void hfut$resendOffers();

    @Inject(method = "restock", at = @At("RETURN"))
    private void hfut$repriceAfterRestock(CallbackInfo ci) {
        this.hfut$reprice();
    }

    @Inject(method = "catchUpDemand", at = @At("RETURN"))
    private void hfut$repriceAfterDemand(CallbackInfo ci) {
        this.hfut$reprice();
    }

    @Inject(method = "gossip", at = @At("RETURN"))
    private void hfut$repriceAfterGossip(ServerLevel level, Villager other, long gossipDelay, CallbackInfo ci) {
        this.hfut$reprice();
    }

    @Inject(method = "onReputationEventFrom", at = @At("RETURN"))
    private void hfut$repriceAfterReputation(ReputationEventType type, Entity entity, CallbackInfo ci) {
        if (!HFUTSettings.villagerLivePriceSync) {
            return;
        }
        Player player = ((Villager) (Object) this).getTradingPlayer();
        if (player != null && player.getUUID().equals(entity.getUUID())) {
            this.hfut$repriceFor(player);
        }
    }

    @Unique
    private void hfut$reprice() {
        if (!HFUTSettings.villagerLivePriceSync) {
            return;
        }
        Player player = ((Villager) (Object) this).getTradingPlayer();
        this.hfut$repriceFor(player);
    }

    @Unique
    private void hfut$repriceFor(Player player) {
        if (player == null) {
            return;
        }
        this.hfut$resetSpecialPrices();
        this.hfut$updateSpecialPrices(player);
        this.hfut$resendOffers();
    }
    //#endif
}
