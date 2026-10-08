package dev.whisperlyric.carpet_hfut_addition.mixins.rule.durabilityRng;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.whisperlyric.carpet_hfut_addition.HFUTSettings;
import dev.whisperlyric.carpet_hfut_addition.helpers.rule.durabilityRng.PlayerDurabilityRng;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

import java.util.function.Consumer;

/**
 * durabilityRngFollowsPlayer, state half: the only durability entry points carrying
 * a player (hurtWithoutBreaking is 1.21.3+ and bypasses hurtAndBreak). push/pop in
 * try/finally so the roll falls back to the world sequence even when the body throws.
 */
@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @WrapMethod(method = "hurtAndBreak(ILnet/minecraft/server/level/ServerLevel;Lnet/minecraft/server/level/ServerPlayer;Ljava/util/function/Consumer;)V")
    private void hfut$wrapHurtAndBreak(int amount, ServerLevel level, ServerPlayer player, Consumer<Item> onBreak, Operation<Void> original) {
        if (HFUTSettings.durabilityRngFollowsPlayer && player != null) {
            PlayerDurabilityRng.push(player.getRandom());
            try {
                original.call(amount, level, player, onBreak);
            } finally {
                PlayerDurabilityRng.pop();
            }
            return;
        }
        original.call(amount, level, player, onBreak);
    }

    //#if MC >= 12103
    //$$ @WrapMethod(method = "hurtWithoutBreaking(ILnet/minecraft/world/entity/player/Player;)V")
    //$$ private void hfut$wrapHurtWithoutBreaking(int amount, Player player, Operation<Void> original) {
    //$$     if (HFUTSettings.durabilityRngFollowsPlayer && player != null) {
    //$$         PlayerDurabilityRng.push(player.getRandom());
    //$$         try {
    //$$             original.call(amount, player);
    //$$         } finally {
    //$$             PlayerDurabilityRng.pop();
    //$$         }
    //$$         return;
    //$$     }
    //$$     original.call(amount, player);
    //$$ }
    //#endif
}
