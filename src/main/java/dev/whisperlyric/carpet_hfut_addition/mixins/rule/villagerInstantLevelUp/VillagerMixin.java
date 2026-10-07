package dev.whisperlyric.carpet_hfut_addition.mixins.rule.villagerInstantLevelUp;

//#if MC < 260300
import dev.whisperlyric.carpet_hfut_addition.HFUTSettings;
import dev.whisperlyric.carpet_hfut_addition.helpers.rule.villagerPricing.VillagerPriceHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#endif
import net.minecraft.world.entity.npc.Villager;
import org.spongepowered.asm.mixin.Mixin;

/**
 * villagerInstantLevelUp (below 26.3): up to 26.2 a qualifying trade only
 * arms a deferred level-up that runs while NOT trading, hence the "close and
 * reopen" dance; 26.3 levels up right in rewardTradeXp with a 10s regen I and
 * reprices a trading player. Reproduced here; off = passthrough, and 26.3 has
 * it natively so the whole member set is guarded. The pricing triple
 * (update/reset/resend) lives in VillagerPriceInvoker + VillagerPriceHelper,
 * shared with villagerLivePriceSync.
 */
@Mixin(Villager.class)
public abstract class VillagerMixin {

    //#if MC < 260300
    @Shadow
    protected abstract boolean shouldIncreaseLevel();

    //#if MC >= 12111
    //$$ @Invoker("increaseMerchantCareer")
    //$$ abstract void hfut$increaseMerchantCareer(ServerLevel level);
    //#else
    @Invoker("increaseMerchantCareer")
    abstract void hfut$increaseMerchantCareer();
    //#endif

    /**
     * At the decision point of rewardTradeXp: with the rule on, do the
     * level-up right away and report false so the deferred branch never
     * arms; with it off, keep the vanilla answer.
     */
    //#if MC >= 12111
    //$$ @Redirect(method = "rewardTradeXp",
    //$$         at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/npc/villager/Villager;shouldIncreaseLevel()Z"))
    //$$ private boolean hfut$instantLevelUp(Villager self) {
    //$$     return this.hfut$decide();
    //$$ }
    //#else
    @Redirect(method = "rewardTradeXp",
              at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/npc/Villager;shouldIncreaseLevel()Z"))
    private boolean hfut$instantLevelUp(Villager self) {
        return this.hfut$decide();
    }
    //#endif

    @Unique
    private boolean hfut$decide() {
        if (!HFUTSettings.villagerInstantLevelUp) {
            return this.shouldIncreaseLevel();
        }
        if (this.shouldIncreaseLevel()) {
            this.hfut$levelUpNow();
        }
        return false;
    }

    @Unique
    private void hfut$levelUpNow() {
        Villager self = (Villager) (Object) this;
        //#if MC >= 12111
        //$$ if (self.level() instanceof ServerLevel serverLevel) {
        //$$     this.hfut$increaseMerchantCareer(serverLevel);
        //$$     self.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 0));
        //$$ }
        //#else
        this.hfut$increaseMerchantCareer();
        self.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 0));
        //#endif
    }

    /**
     * 26.3 updateTrades tail: while a player is mid-trade, re-apply special
     * prices so the freshly unlocked tier appears discounted immediately.
     */
    //#if MC >= 12111
    //$$ @Inject(method = "updateTrades", at = @At("RETURN"))
    //$$ private void hfut$liveSpecialPrices(ServerLevel level, CallbackInfo ci) {
    //$$     this.hfut$applyLivePrices();
    //$$ }
    //#else
    @Inject(method = "updateTrades", at = @At("RETURN"))
    private void hfut$liveSpecialPrices(CallbackInfo ci) {
        this.hfut$applyLivePrices();
    }
    //#endif

    @Unique
    private void hfut$applyLivePrices() {
        if (!HFUTSettings.villagerInstantLevelUp) {
            return;
        }
        Villager self = (Villager) (Object) this;
        VillagerPriceHelper.reprice(self, self.getTradingPlayer());
    }
    //#endif
}
