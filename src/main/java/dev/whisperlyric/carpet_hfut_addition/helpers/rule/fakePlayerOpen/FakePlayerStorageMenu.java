package dev.whisperlyric.carpet_hfut_addition.helpers.rule.fakePlayerOpen;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
//#if MC >= 260400
//$$ import net.minecraft.world.inventory.Slot.Section;
//#endif
import net.minecraft.world.item.ItemStack;

import java.util.function.IntPredicate;

/**
 * Vanilla {@link AbstractContainerMenu} mirroring {@code ChestMenu}'s slot geometry
 * (so the client renders a normal chest) but supporting inert placeholder cells:
 * placeholder slots reject both placing and picking up, keeping them out of
 * shift-click transfers. {@link #stillValid(Player)} is unconditional.
 * <p>
 * From 26.4-snapshot-10 {@code Slot} also takes a {@code Section} tag; the
 * 26.4 node passes the matching viewer-inventory section, older nodes use the
 * four-argument constructor.
 */
public final class FakePlayerStorageMenu extends AbstractContainerMenu {
    private static final int COLUMNS = 9;

    private final FakePlayerStorageEditor.Kind kind;
    private final int storageSlots;

    public FakePlayerStorageMenu(MenuType<?> type, int id, Inventory viewerInventory,
                                 FakePlayerStorageEditor.Kind kind, Container storage,
                                 int rows, IntPredicate placeholder) {
        super(type, id);
        checkContainerSize(storage, rows * COLUMNS);
        this.kind = kind;
        this.storageSlots = rows * COLUMNS;

        int offset = (rows - 4) * 18;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                int index = col + row * COLUMNS;
                addSlot(new StorageSlot(storage, index, 8 + col * 18, 18 + row * 18,
                        placeholder.test(index)));
            }
        }
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                //#if MC >= 260400
                //$$ addSlot(new Slot(viewerInventory, col + row * COLUMNS + 9,
                //$$         8 + col * 18, 103 + row * 18 + offset,
                //$$         viewerSection(col + row * COLUMNS + 9)));
                //#else
                addSlot(new Slot(viewerInventory, col + row * COLUMNS + 9,
                        8 + col * 18, 103 + row * 18 + offset));
                //#endif
            }
        }
        for (int col = 0; col < COLUMNS; col++) {
            //#if MC >= 260400
            //$$ addSlot(new Slot(viewerInventory, col, 8 + col * 18, 161 + offset,
            //$$         Section.HOTBAR));
            //#else
            addSlot(new Slot(viewerInventory, col, 8 + col * 18, 161 + offset));
            //#endif
        }
    }

    //#if MC >= 260400
    //$$ /** Maps a viewer-inventory index (9-35 main, 0-8 hotbar) to its slot section. */
    //$$ private static Section viewerSection(int index) {
    //$$     return index >= Inventory.getSelectionSize()
    //$$             ? Section.INVENTORY
    //$$             : Section.HOTBAR;
    //$$ }
    //#endif

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (player instanceof ServerPlayer serverPlayer) {
            FakePlayerStorageEditor.onViewerClose(kind, this, serverPlayer);
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = index >= 0 && index < slots.size() ? slots.get(index) : null;
        if (slot == null || !slot.hasItem() || !slot.mayPickup(player)) {
            return ItemStack.EMPTY;
        }
        ItemStack inSlot = slot.getItem();
        ItemStack original = inSlot.copy();
        if (index < storageSlots) {
            if (!moveItemStackTo(inSlot, storageSlots, slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else if (!moveItemStackTo(inSlot, 0, storageSlots, false)) {
            return ItemStack.EMPTY;
        }
        if (inSlot.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        return original;
    }

    private static final class StorageSlot extends Slot {
        private final boolean placeholder;

        StorageSlot(Container container, int slot, int x, int y, boolean placeholder) {
            //#if MC >= 260400
            //$$ super(container, slot, x, y, Section.CONTAINER);
            //#else
            super(container, slot, x, y);
            //#endif
            this.placeholder = placeholder;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return !placeholder;
        }

        @Override
        public boolean mayPickup(Player player) {
            return !placeholder;
        }
    }
}
