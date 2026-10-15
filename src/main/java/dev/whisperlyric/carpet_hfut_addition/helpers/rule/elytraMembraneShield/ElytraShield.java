package dev.whisperlyric.carpet_hfut_addition.helpers.rule.elytraMembraneShield;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

/**
 * Reads and writes the elytra durability shield: a consumable buffer stored in the
 * stack's {@code custom_data} component under {@code hfut:elytra_shield}. Damage
 * drains the shield before the real durability and the shield never decays on its own.
 */
public final class ElytraShield {

    /** Shield granted by one phantom membrane (a quarter of the elytra's 432 durability). */
    public static final int PER_MEMBRANE = 108;

    private static final String KEY = "hfut:elytra_shield";

    private ElytraShield() {
    }

    public static int get(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) {
            return 0;
        }
        CompoundTag tag = data.copyTag();
        //#if MC >= 12105
        //$$ return tag.getIntOr(KEY, 0);
        //#else
        return tag.getInt(KEY);
        //#endif
    }

    public static void set(ItemStack stack, int value) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
            if (value <= 0) {
                tag.remove(KEY);
            } else {
                tag.putInt(KEY, value);
            }
        });
    }

    /** Adds shield, clamped to the durability data type's own limit instead of overflowing. */
    public static void add(ItemStack stack, int value) {
        long total = (long) get(stack) + value;
        set(stack, (int) Math.min(Integer.MAX_VALUE, Math.max(0L, total)));
    }
}
