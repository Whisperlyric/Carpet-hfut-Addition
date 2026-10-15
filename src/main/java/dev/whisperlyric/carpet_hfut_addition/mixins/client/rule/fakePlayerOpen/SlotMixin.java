package dev.whisperlyric.carpet_hfut_addition.mixins.client.rule.fakePlayerOpen;

import dev.whisperlyric.carpet_hfut_addition.helpers.rule.fakePlayerOpen.OfflinePlayerInventoryContainer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//#if MC < 12104
import com.mojang.datafixers.util.Pair;
//#endif

/**
 * fakePlayerOpen, client half. The armour (cells 1-4) and off-hand (cell 6) of our
 * 5x9 storage view hold no item when empty, so vanilla would draw nothing there.
 * This hands back the same empty-slot icons vanilla's {@code InventoryMenu} uses
 * (helmet / chestplate / leggings / boots / shield) so those cells read as real
 * equipment slots. The view is recognised through the placeholder marker on cell 0,
 * because the client only ever sees a plain {@code SimpleContainer}. Before 1.21.4
 * the icon is an atlas/texture pair; from 1.21.4 (and through the Identifier rename)
 * it is a single sprite id.
 */
@Mixin(Slot.class)
public abstract class SlotMixin {

    @Shadow
    @Final
    public Container container;

    @Shadow
    public abstract int getContainerSlot();

    //#if MC < 12104
    @Inject(method = "getNoItemIcon", at = @At("HEAD"), cancellable = true)
    private void hfut$storageSlotIcon(CallbackInfoReturnable<Pair<ResourceLocation, ResourceLocation>> cir) {
        ResourceLocation icon = hfut$emptyIcon();
        if (icon != null) {
            cir.setReturnValue(Pair.of(InventoryMenu.BLOCK_ATLAS, icon));
        }
    }
    //#else
    //$$ @Inject(method = "getNoItemIcon", at = @At("HEAD"), cancellable = true)
    //$$ private void hfut$storageSlotIcon(CallbackInfoReturnable<ResourceLocation> cir) {
    //$$     ResourceLocation icon = hfut$emptyIcon();
    //$$     if (icon != null) {
    //$$         cir.setReturnValue(icon);
    //$$     }
    //$$ }
    //#endif

    @Unique
    private ResourceLocation hfut$emptyIcon() {
        ResourceLocation icon = switch (this.getContainerSlot()) {
            case 1 -> InventoryMenu.EMPTY_ARMOR_SLOT_HELMET;
            case 2 -> InventoryMenu.EMPTY_ARMOR_SLOT_CHESTPLATE;
            case 3 -> InventoryMenu.EMPTY_ARMOR_SLOT_LEGGINGS;
            case 4 -> InventoryMenu.EMPTY_ARMOR_SLOT_BOOTS;
            case 6 -> InventoryMenu.EMPTY_ARMOR_SLOT_SHIELD;
            default -> null;
        };
        if (icon == null || !OfflinePlayerInventoryContainer.isStorageView(this.container)) {
            return null;
        }
        return icon;
    }
}
