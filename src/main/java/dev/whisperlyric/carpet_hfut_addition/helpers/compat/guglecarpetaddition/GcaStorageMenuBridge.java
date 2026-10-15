package dev.whisperlyric.carpet_hfut_addition.helpers.compat.guglecarpetaddition;

import dev.dubhe.gugle.carpet.GcaSetting;
import dev.dubhe.gugle.carpet.tools.player.IGcaPlayer;
import dev.dubhe.gugle.carpet.tools.player.PlayerInventoryMenu;
import dev.dubhe.gugle.carpet.util.SettingUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;

/**
 * Reuses GCA's own fake player menus: each menu is built over GCA's cached
 * container, which GCA drives every tick, so a view opened through here stays in
 * step with the fake player. The caller probes GCA first and only then touches
 * this class, and only vanilla types appear in the return descriptors.
 */
public final class GcaStorageMenuBridge {
    private GcaStorageMenuBridge() {
    }

    /** True when GCA offers its inventory menu. */
    public static boolean allowsInventory() {
        return GcaSetting.openFakePlayerInventory;
    }

    /** True when GCA offers its ender chest menu to {@code viewer}. */
    public static boolean allowsEnderChest(Player viewer) {
        return SettingUtil.openFakePlayerEnderChest(viewer);
    }

    /** GCA's inventory menu over {@code fake}'s live container. */
    public static AbstractContainerMenu inventoryMenu(ServerPlayer fake, int id, Inventory viewerInventory) {
        return new PlayerInventoryMenu(id, viewerInventory,
                withoutReach(((IGcaPlayer) fake).getInventoryContainer().selfUpdate(), fake));
    }

    /** GCA's ender chest menu over {@code fake}'s live container. */
    public static AbstractContainerMenu enderChestMenu(ServerPlayer fake, int id, Inventory viewerInventory) {
        return ChestMenu.sixRows(id, viewerInventory,
                withoutReach(((IGcaPlayer) fake).getEnderChestContainer().selfUpdate(), fake));
    }

    /**
     * A chest menu asks its container whether the viewer is still in reach and is
     * dropped once the answer turns false; GCA's container answers that from the
     * distance between the viewer and the fake player. The returned container keeps
     * every read and write on GCA's container, so slots, buttons and the per-tick
     * refresh behave as usual, and only that reach answer is replaced by the one
     * condition the menu should still respect: the fake player being alive.
     */
    private static Container withoutReach(Container delegate, ServerPlayer fake) {
        return new DelegatingContainer(delegate) {
            @Override
            public boolean stillValid(Player viewer) {
                return fake.isAlive();
            }
        };
    }

    /**
     * Forwards the whole {@link Container} contract to another container. Only the
     * members declared abstract by the interface are forwarded, because the
     * remaining defaults are left untouched by the containers this bridge wraps.
     */
    private abstract static class DelegatingContainer implements Container {
        private final Container delegate;

        DelegatingContainer(Container delegate) {
            this.delegate = delegate;
        }

        @Override
        public int getContainerSize() {
            return delegate.getContainerSize();
        }

        @Override
        public boolean isEmpty() {
            return delegate.isEmpty();
        }

        @Override
        public ItemStack getItem(int slot) {
            return delegate.getItem(slot);
        }

        @Override
        public ItemStack removeItem(int slot, int amount) {
            return delegate.removeItem(slot, amount);
        }

        @Override
        public ItemStack removeItemNoUpdate(int slot) {
            return delegate.removeItemNoUpdate(slot);
        }

        @Override
        public void setItem(int slot, ItemStack stack) {
            delegate.setItem(slot, stack);
        }

        @Override
        public void setChanged() {
            delegate.setChanged();
        }

        @Override
        public void clearContent() {
            delegate.clearContent();
        }
    }
}
