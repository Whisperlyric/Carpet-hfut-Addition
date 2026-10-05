package dev.whisperlyric.carpet_hfut_addition.mixins.rule.attributeModifierRemovalDelay;

import net.minecraft.world.entity.LivingEntity;
import dev.whisperlyric.carpet_hfut_addition.HFUTSettings;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

/**
 * attributeModifierRemovalDelay (26.3+): reverts MC-311022's fix. 26.2 re-read
 * the equipment slot, which applyDamage had already emptied, so the removal ran
 * on an empty stack and its modifiers were cleared a tick late - visible with
 * soul speed, but any enchantment on the same removal path is affected.
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    //#if MC >= 260300
    //$$ @ModifyArgs(
    //$$     method = "onEquippedItemBroken",
    //$$     at = @At(
    //$$         value = "INVOKE",
    //$$         target = "Lnet/minecraft/world/entity/LivingEntity;stopLocationBasedEffects(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/EquipmentSlot;Lnet/minecraft/world/entity/ai/attributes/AttributeMap;)V"
    //$$     )
    //$$ )
    //$$ private void hfut$rereadClearedSlot(Args args) {
    //$$     if (HFUTSettings.attributeModifierRemovalDelay) {
    //$$         // 26.2 behaviour: re-read the slot, which is empty by now
    //$$         EquipmentSlot inSlot = (EquipmentSlot) args.get(1);
    //$$         args.set(0, ((LivingEntity) (Object) this).getItemBySlot(inSlot));
    //$$     }
    //$$ }
    //#endif
}
