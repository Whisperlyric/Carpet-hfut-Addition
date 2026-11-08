package dev.whisperlyric.carpet_hfut_addition.mixins.rule.blockPlacementIgnoreAttachment;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.whisperlyric.carpet_hfut_addition.HFUTSettings;
import dev.whisperlyric.carpet_hfut_addition.helpers.rule.blockPlacementIgnoreAttachment.PlacementScope;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import org.spongepowered.asm.mixin.Mixin;

/**
 * blockPlacementIgnoreAttachment: opens the placement scope around the whole
 * placement, so both the block's own getStateForPlacement lookups and the
 * canSurvive check in canPlace are covered.
 */
@Mixin(BlockItem.class)
public abstract class BlockItemMixin {

    @WrapMethod(method = "place")
    private InteractionResult hfut$openPlacementScope(BlockPlaceContext context, Operation<InteractionResult> original) {
        if (!HFUTSettings.blockPlacementIgnoreAttachment) {
            return original.call(context);
        }
        PlacementScope.enter();
        try {
            return original.call(context);
        } finally {
            PlacementScope.exit();
        }
    }
}
