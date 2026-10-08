package dev.whisperlyric.carpet_hfut_addition.mixins.rule.durabilityRng;

import dev.whisperlyric.carpet_hfut_addition.HFUTSettings;
import dev.whisperlyric.carpet_hfut_addition.helpers.rule.durabilityRng.PlayerDurabilityRng;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * durabilityRngFollowsPlayer, roll half: the roll's only environment is the
 * ServerLevel, so redirect its getRandom to the player sequence parked by the
 * ItemStack half.
 */
@Mixin(Enchantment.class)
public abstract class EnchantmentMixin {

    private static RandomSource hfut$pickRng(ServerLevel level) {
        RandomSource playerRng = PlayerDurabilityRng.ACTIVE.get();
        if (HFUTSettings.durabilityRngFollowsPlayer && playerRng != null) {
            return playerRng;
        }
        return level.getRandom();
    }

    //#if MC >= 260100
    //$$ @Redirect(method = "lambda$modifyItemFilteredCount$0",
    //$$           at = @At(value = "INVOKE",
    //$$                    target = "Lnet/minecraft/server/level/ServerLevel;getRandom()Lnet/minecraft/util/RandomSource;"))
    //$$ private static RandomSource hfut$followPlayerRng26(ServerLevel level) {
    //$$     return hfut$pickRng(level);
    //$$ }
    //#else
    @Redirect(method = "method_60038",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/server/level/ServerLevel;getRandom()Lnet/minecraft/util/RandomSource;"))
    private static RandomSource hfut$followPlayerRng121(ServerLevel level) {
        return hfut$pickRng(level);
    }
    //#endif
}
