package dev.whisperlyric.carpet_hfut_addition.mixins.rule.silkTouchFrostedIce;

import dev.whisperlyric.carpet_hfut_addition.HFUTSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * silkTouchFrostedIce: frosted ice has no drops at all, so a silk-touch tool
 * cannot harvest it. With the rule on, breaking it with silk touch drops one
 * regular ice and keeps the after-break hook; creative players stay exempt.
 */
@Mixin(Block.class)
public abstract class SilkTouchFrostedIceMixin {

    @Inject(method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)V",
            at = @At("HEAD"), cancellable = true)
    private static void hfut$dropFrostedIceWithSilkTouch(BlockState state, Level level, BlockPos pos,
                                                         @Nullable BlockEntity blockEntity, @Nullable Entity entity,
                                                         ItemStack tool, CallbackInfo ci) {
        if (!HFUTSettings.silkTouchFrostedIce) {
            return;
        }
        if (!level.isClientSide() && state.is(Blocks.FROSTED_ICE)
                && !(entity instanceof Player player && player.getAbilities().instabuild)
                && EnchantmentHelper.getEnchantmentsForCrafting(tool).entrySet().stream().anyMatch(entry ->
                        entry.getKey().is(Enchantments.SILK_TOUCH) && entry.getIntValue() > 0)) {
            Block.popResource(level, pos, new ItemStack(Blocks.ICE));
            //#if MC >= 260400
            //$$ state.spawnAfterBreak((ServerLevel) level, pos, tool, true, entity);
            //#else
            state.spawnAfterBreak((ServerLevel) level, pos, tool, true);
            //#endif
            ci.cancel();
        }
    }
}
