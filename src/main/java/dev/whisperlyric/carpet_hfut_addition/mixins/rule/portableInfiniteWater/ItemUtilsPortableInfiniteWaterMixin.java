package dev.whisperlyric.carpet_hfut_addition.mixins.rule.portableInfiniteWater;

import dev.whisperlyric.carpet_hfut_addition.HFUTSettings;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * portableInfiniteWater: holding a vanilla water bucket in both hands makes
 * bucket placements not consume the bucket - water source anywhere, no
 * refill trips. The interception lives in
 * {@code ItemUtils.createFilledResult}, the shared post-use helper every
 * bucket-consuming item funnels through: when the used bucket is a hand-held
 * water bucket and the other hand holds another water bucket, the original
 * (still-full) stack is returned instead of the empty replacement. Creative
 * players are exempt - vanilla already keeps the bucket for them.
 */
@Mixin(ItemUtils.class)
public abstract class ItemUtilsPortableInfiniteWaterMixin {

    @Inject(method = "createFilledResult(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/ItemStack;",
            at = @At("HEAD"), cancellable = true)
    private static void hfut$keepVanillaWaterBucket(ItemStack inputStack, Player player,
                                                    ItemStack replacementStack, CallbackInfoReturnable<ItemStack> cir) {
        if (!HFUTSettings.portableInfiniteWater || player.hasInfiniteMaterials()) {
            return;
        }
        ItemStack mainHandStack = player.getMainHandItem();
        ItemStack offHandStack = player.getOffhandItem();
        if (inputStack.getItem() == Items.WATER_BUCKET
                && replacementStack.getItem() == Items.BUCKET
                && mainHandStack.getItem() == Items.WATER_BUCKET
                && offHandStack.getItem() == Items.WATER_BUCKET
                && (inputStack == mainHandStack || inputStack == offHandStack)) {
            cir.setReturnValue(inputStack);
        }
    }
}
