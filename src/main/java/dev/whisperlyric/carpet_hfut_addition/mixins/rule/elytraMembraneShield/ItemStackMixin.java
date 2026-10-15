package dev.whisperlyric.carpet_hfut_addition.mixins.rule.elytraMembraneShield;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.whisperlyric.carpet_hfut_addition.HFUTSettings;
import dev.whisperlyric.carpet_hfut_addition.helpers.rule.elytraMembraneShield.ElytraShield;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

/**
 * elytraMembraneShield, state half: a shielded elytra drains its shield from the
 * damage that survives the Unbreaking roll and only the leftover reaches vanilla,
 * so creative mode (which returns before the roll) stays exempt. The tooltip gains
 * a line with the remaining shield. The roll lives in hurtAndBreak up to 1.21.2
 * and in the private processDurabilityChange from 1.21.3 on.
 */
@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    //#if MC >= 12103
    //$$ @WrapOperation(
    //$$         method = "processDurabilityChange(ILnet/minecraft/server/level/ServerLevel;Lnet/minecraft/server/level/ServerPlayer;)I",
    //$$         at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;processDurabilityChange(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;I)I"))
    //#else
    @WrapOperation(
            method = "hurtAndBreak(ILnet/minecraft/server/level/ServerLevel;Lnet/minecraft/server/level/ServerPlayer;Ljava/util/function/Consumer;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;processDurabilityChange(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;I)I"))
    //#endif
    private int hfut$drainShield(ServerLevel level, ItemStack stack, int amount, Operation<Integer> original) {
        int remaining = original.call(level, stack, amount);
        if (!HFUTSettings.elytraMembraneShield || remaining <= 0 || stack.getItem() != Items.ELYTRA) {
            return remaining;
        }
        int shield = ElytraShield.get(stack);
        if (shield <= 0) {
            return remaining;
        }
        if (shield >= remaining) {
            ElytraShield.set(stack, shield - remaining);
            return 0;
        }
        ElytraShield.set(stack, 0);
        return remaining - shield;
    }

    @Inject(
            method = "getTooltipLines(Lnet/minecraft/world/item/Item$TooltipContext;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/TooltipFlag;)Ljava/util/List;",
            at = @At("RETURN"),
            cancellable = true)
    private void hfut$shieldTooltip(Item.TooltipContext context, Player player, TooltipFlag flag, CallbackInfoReturnable<List<Component>> cir) {
        int shield = ElytraShield.get((ItemStack) (Object) this);
        if (shield <= 0) {
            return;
        }
        List<Component> lines = new ArrayList<>(cir.getReturnValue());
        lines.add(Component.translatable("carpet.rule.elytraMembraneShield.tooltip", shield)
                .withStyle(ChatFormatting.AQUA, ChatFormatting.ITALIC));
        cir.setReturnValue(lines);
    }
}
