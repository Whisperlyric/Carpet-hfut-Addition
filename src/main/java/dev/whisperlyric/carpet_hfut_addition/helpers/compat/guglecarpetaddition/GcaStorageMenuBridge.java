package dev.whisperlyric.carpet_hfut_addition.helpers.compat.guglecarpetaddition;

import dev.dubhe.gugle.carpet.GcaSetting;
import dev.dubhe.gugle.carpet.tools.player.IGcaPlayer;
import dev.dubhe.gugle.carpet.tools.player.PlayerInventoryMenu;
import dev.dubhe.gugle.carpet.util.SettingUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;

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
                ((IGcaPlayer) fake).getInventoryContainer().selfUpdate());
    }

    /** GCA's ender chest menu over {@code fake}'s live container. */
    public static AbstractContainerMenu enderChestMenu(ServerPlayer fake, int id, Inventory viewerInventory) {
        return ChestMenu.sixRows(id, viewerInventory,
                ((IGcaPlayer) fake).getEnderChestContainer().selfUpdate());
    }
}
