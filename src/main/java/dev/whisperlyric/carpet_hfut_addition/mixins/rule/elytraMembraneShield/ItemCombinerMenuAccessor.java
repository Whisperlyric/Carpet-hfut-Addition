package dev.whisperlyric.carpet_hfut_addition.mixins.rule.elytraMembraneShield;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.ResultContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * inputSlots/resultSlots are declared in ItemCombinerMenu, not AnvilMenu, and
 * @Shadow does not search superclasses - expose them so the AnvilMenu mixin can
 * read the two input slots and write the shielded result. Stable across every version.
 */
@Mixin(ItemCombinerMenu.class)
public interface ItemCombinerMenuAccessor {

    @Accessor("inputSlots")
    Container hfut$inputSlots();

    @Accessor("resultSlots")
    ResultContainer hfut$resultSlots();
}
