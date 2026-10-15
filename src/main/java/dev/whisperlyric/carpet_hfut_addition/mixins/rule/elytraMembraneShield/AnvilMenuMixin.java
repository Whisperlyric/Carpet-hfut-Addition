package dev.whisperlyric.carpet_hfut_addition.mixins.rule.elytraMembraneShield;

import dev.whisperlyric.carpet_hfut_addition.HFUTSettings;
import dev.whisperlyric.carpet_hfut_addition.helpers.rule.elytraMembraneShield.ElytraShield;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * elytraMembraneShield, anvil half: a full-durability elytra in the left slot plus
 * phantom membranes in the right slot produce an elytra carrying a consumable
 * durability shield, where vanilla would repair nothing. The cost is one level per
 * membrane, and taking the result consumes the whole right slot (repairItemCountCost 0).
 */
@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin {

    @Shadow
    private int repairItemCountCost;

    @Shadow
    private DataSlot cost;

    @Inject(method = "createResult", at = @At("RETURN"))
    private void hfut$membraneShield(CallbackInfo ci) {
        if (!HFUTSettings.elytraMembraneShield) {
            return;
        }
        ItemCombinerMenuAccessor accessor = (ItemCombinerMenuAccessor) this;
        ItemStack left = accessor.hfut$inputSlots().getItem(0);
        if (left.getItem() != Items.ELYTRA || left.getDamageValue() != 0) {
            return;
        }
        ItemStack right = accessor.hfut$inputSlots().getItem(1);
        if (right.isEmpty() || right.getItem() != Items.PHANTOM_MEMBRANE) {
            return;
        }
        int count = right.getCount();
        ItemStack base = accessor.hfut$resultSlots().getItem(0);
        ItemStack result = base.getItem() == Items.ELYTRA ? base : left.copy();
        result.setCount(1);
        ElytraShield.add(result, count * ElytraShield.PER_MEMBRANE);
        accessor.hfut$resultSlots().setItem(0, result);
        this.cost.set(count);
        this.repairItemCountCost = 0;
    }
}
