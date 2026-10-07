package dev.whisperlyric.carpet_hfut_addition.mixins.rule.windChargeNoRedstoneActivation;

//#if MC < 12102
import dev.whisperlyric.carpet_hfut_addition.HFUTSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.windcharge.AbstractWindCharge;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import java.util.function.BiConsumer;
//#endif
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import org.spongepowered.asm.mixin.Mixin;

/**
 * windChargeNoRedstoneActivation, 1.21.1 shape: filters the five redstone
 * components in Explosion.finalizeExplosion's dispatch. On 1.21.2+ that
 * dispatch moved to ServerExplosion (sibling mixin); below 12102 this member
 * set is stripped and the mixin is restricted to <1.21.2 with a string target,
 * Explosion having become an interface.
 */
@Restriction(require = @Condition(value = "minecraft", versionPredicates = "<1.21.2"))
@Mixin(targets = "net.minecraft.world.level.Explosion")
public abstract class ExplosionMixin {

    //#if MC < 12102
    @Shadow
    @Final
    private Entity source;

    private boolean hfut$isRedstoneComponent(BlockState state) {
        return state.getBlock() instanceof ButtonBlock
                || state.getBlock() instanceof LeverBlock
                || state.getBlock() instanceof DoorBlock
                || state.getBlock() instanceof TrapDoorBlock
                || state.getBlock() instanceof FenceGateBlock;
    }

    @WrapOperation(method = "finalizeExplosion",
                   at = @At(value = "INVOKE",
                            target = "Lnet/minecraft/world/level/block/state/BlockState;onExplosionHit(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/Explosion;Ljava/util/function/BiConsumer;)V"))
    private void hfut$noRedstoneActivation(BlockState state, Level level, BlockPos pos, Explosion explosion,
                                           BiConsumer<ItemStack, BlockPos> drops, Operation<Void> original) {
        if (HFUTSettings.windChargeNoRedstoneActivation
                && this.source instanceof AbstractWindCharge
                && this.hfut$isRedstoneComponent(state)) {
            return;
        }
        original.call(state, level, pos, explosion, drops);
    }
    //#endif
}
