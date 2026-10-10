package dev.whisperlyric.carpet_hfut_addition.helpers.rule.fakePlayerOpen;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Read/write {@link Container} over an offline fake player's ender chest (3x9).
 * All calls delegate to the player's ender chest inventory; {@link #stillValid(Player)}
 * always returns {@code true} so remote viewers are never disconnected.
 */
public final class OfflinePlayerEnderChestContainer implements Container {
    private final Container enderChest;

    public OfflinePlayerEnderChestContainer(ServerPlayer player) {
        this.enderChest = player.getEnderChestInventory();
    }

    @Override
    public int getContainerSize() {
        return enderChest.getContainerSize();
    }

    @Override
    public boolean isEmpty() {
        return enderChest.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return enderChest.getItem(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return enderChest.removeItem(slot, amount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return enderChest.removeItemNoUpdate(slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        enderChest.setItem(slot, stack);
    }

    @Override
    public void setChanged() {
        enderChest.setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        enderChest.clearContent();
    }
}
