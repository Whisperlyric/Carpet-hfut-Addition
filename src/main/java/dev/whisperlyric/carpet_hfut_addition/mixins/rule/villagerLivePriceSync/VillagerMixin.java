package dev.whisperlyric.carpet_hfut_addition.mixins.rule.villagerLivePriceSync;

//#if MC < 260300
import dev.whisperlyric.carpet_hfut_addition.HFUTSettings;
import dev.whisperlyric.carpet_hfut_addition.helpers.rule.villagerPricing.VillagerPriceHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.village.ReputationEventType;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Unique;
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
 * injections here do the same. The reset-update-resend triple lives in
 * VillagerPriceInvoker + VillagerPriceHelper, shared with
 * villagerInstantLevelUp. Plain class/gossip renames are left to the
 * preprocessor.
 */
@Mixin(Villager.class)
public abstract class VillagerMixin {

    //#if MC < 260300
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
        if (player != null && entity != null && player.getUUID().equals(entity.getUUID())) {
            VillagerPriceHelper.reprice((Villager) (Object) this, player);
        }
    }

    @Unique
    private void hfut$reprice() {
        if (!HFUTSettings.villagerLivePriceSync) {
            return;
        }
        Villager self = (Villager) (Object) this;
        VillagerPriceHelper.reprice(self, self.getTradingPlayer());
    }
    //#endif
}
