package dev.whisperlyric.carpet_hfut_addition.helpers.rule.fakePlayerOpen;

import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;

/**
 * Read/write {@link Container} over an offline fake player's inventory, shown as a
 * 5x9 chest: armor and off-hand in row 1, then the 36 real inventory slots.
 *
 * <p>Cells {@code 0/5/7/8} are inert placeholders carrying a plain-NBT
 * {@code GcaClear}/{@code GcaButton} marker (via the vanilla {@code custom_data}
 * component, no GCA class) so GCA-aware sorters skip them. {@link #stillValid(Player)}
 * always returns {@code true} so the view never closes on distance.
 */
public final class OfflinePlayerInventoryContainer implements Container {
    private static final int SIZE = 45;
    private static final int[] PLACEHOLDER_SLOTS = {0, 5, 7, 8};

    private final ServerPlayer player;
    private final NonNullList<ItemStack> placeholders;

    public OfflinePlayerInventoryContainer(ServerPlayer player) {
        this.player = player;
        this.placeholders = NonNullList.withSize(SIZE, ItemStack.EMPTY);
        for (int slot : PLACEHOLDER_SLOTS) {
            placeholders.set(slot, placeholderMarker(slot));
        }
    }

    private static ItemStack placeholderMarker(int slot) {
        //#if MC >= 260200
        //$$ // 26.2 起染色玻璃系合并为 ColorCollection,逐色常量不复存在
        //$$ ItemStack stack = new ItemStack(Items.STAINED_GLASS_PANE.red());
        //#else
        ItemStack stack = new ItemStack(Items.RED_STAINED_GLASS_PANE);
        //#endif
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("GcaClear", true);
        tag.putInt("GcaButton", slot);
        CustomData.set(DataComponents.CUSTOM_DATA, stack, tag);
        return stack;
    }

    public static boolean isPlaceholder(int slot) {
        for (int placeholder : PLACEHOLDER_SLOTS) {
            if (placeholder == slot) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getContainerSize() {
        return SIZE;
    }

    @Override
    public boolean isEmpty() {
        for (int slot = 0; slot < SIZE; slot++) {
            if (!isPlaceholder(slot) && !getItem(slot).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        if (isPlaceholder(slot)) {
            return placeholders.get(slot).copy();
        }
        if (slot >= 1 && slot <= 4) {
            return player.getItemBySlot(armorSlot(slot));
        }
        if (slot == 6) {
            return player.getItemBySlot(EquipmentSlot.OFFHAND);
        }
        if (slot >= 9 && slot < SIZE) {
            return player.getInventory().getItem(inventoryIndex(slot));
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        if (isPlaceholder(slot)) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = getItem(slot);
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack removed = stack.split(amount);
        if (!removed.isEmpty()) {
            setChanged();
        }
        return removed;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        if (isPlaceholder(slot)) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = getItem(slot);
        setItem(slot, ItemStack.EMPTY);
        return stack;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (isPlaceholder(slot)) {
            return;
        }
        if (slot >= 1 && slot <= 4) {
            player.setItemSlot(armorSlot(slot), stack);
            return;
        }
        if (slot == 6) {
            player.setItemSlot(EquipmentSlot.OFFHAND, stack);
            return;
        }
        if (slot >= 9 && slot < SIZE) {
            player.getInventory().setItem(inventoryIndex(slot), stack);
        }
    }

    @Override
    public void setChanged() {
        player.getInventory().setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        // Keep the placeholder markers; clear only the real storage.
        for (int slot = 0; slot < SIZE; slot++) {
            if (!isPlaceholder(slot)) {
                setItem(slot, ItemStack.EMPTY);
            }
        }
    }

    private static EquipmentSlot armorSlot(int slot) {
        return switch (slot) {
            case 1 -> EquipmentSlot.HEAD;
            case 2 -> EquipmentSlot.CHEST;
            case 3 -> EquipmentSlot.LEGS;
            default -> EquipmentSlot.FEET;
        };
    }

    private static int inventoryIndex(int slot) {
        return slot >= 36 ? slot - 36 : slot;
    }
}
