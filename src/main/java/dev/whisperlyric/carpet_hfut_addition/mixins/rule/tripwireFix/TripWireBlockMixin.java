package dev.whisperlyric.carpet_hfut_addition.mixins.rule.tripwireFix;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.whisperlyric.carpet_hfut_addition.HFUTSettings;
import dev.whisperlyric.carpet_hfut_addition.utils.interfaces.SegmentContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TripWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if MC >= 12109
//$$ import net.minecraft.world.entity.InsideBlockEffectApplier;
//#endif

import java.util.List;

/**
 * Tripwire fixes (MC-305475), ruled by {@link HFUTSettings}. The class exists
 * on every version; members needing APIs absent below 1.21.9 are //$$-guarded.
 */
@Mixin(TripWireBlock.class)
public abstract class TripWireBlockMixin {

    //#if MC >= 12109
    //$$ @Shadow
    //$$ private void checkPressed(Level level, BlockPos pos, List<? extends Entity> entities) {
    //$$     throw new AssertionError();
    //$$ }
    //#endif

    //#if MC >= 12109
    //$$ @Inject(
    //$$         method = "entityInside(Lnet/minecraft/world/level/block/state/BlockState;"
    //$$                 + "Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;"
    //$$                 + "Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/InsideBlockEffectApplier;Z)V",
    //$$         at = @At("HEAD"), cancellable = true
    //$$ )
    //$$ private void hfut$entityInside(BlockState state, Level level, BlockPos pos, Entity entity,
    //$$                                InsideBlockEffectApplier effectApplier, boolean isPrecise,
    //$$                                CallbackInfo ci) {
    //$$     if (level.isClientSide()) return;
    //$$     if (state.getValue(TripWireBlock.POWERED)) return; // powered wires are a vanilla no-op
    //$$     boolean direction = HFUTSettings.departureFixActive();
    //#if MC >= 260200
    //$$     boolean legacyBase = HFUTSettings.restoreMode() == HFUTSettings.MODE_LEGACY;
    //$$     if (!legacyBase && !direction) return; // VANILLA / V263 base: keep the vanilla guard
    //#else
    //$$     if (!direction) return;                // no Mojang fix here, nothing to strip
    //#endif
    //$$     ci.cancel(); // re-implement the 26.1 trigger logic
    //$$
    //$$     if (direction && entity instanceof SegmentContext ctx
    //$$             && ctx.hfut$segFrom() != null && ctx.hfut$segTo() != null) {
    //$$         AABB band = state.getShape(level, pos).bounds().move(pos).deflate(1.0E-5);
    //$$         AABB boxFrom = ctx.hfut$segBox(ctx.hfut$segFrom()).deflate(1.0E-5);
    //$$         AABB boxTo = ctx.hfut$segBox(ctx.hfut$segTo()).deflate(1.0E-5);
    //$$         if (boxFrom.intersects(band) && !boxTo.intersects(band)) {
    //$$             return; // pure departure sweep: no right to raise the edge (MC-305475)
    //$$         }
    //$$     }
    //$$     this.checkPressed(level, pos, List.of(entity));
    //$$ }
    //#endif

    //#if MC >= 260200
    //$$ @WrapOperation(
    //$$         method = "checkPressed(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Ljava/util/List;)V",
    //$$         at = @At(value = "INVOKE",
    //$$                  target = "Lnet/minecraft/world/level/Level;scheduleTick"
    //$$                           + "(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;I)V")
    //$$ )
    //$$ private void hfut$restoreScheduleTick(Level instance, BlockPos pos, Block block, int delay,
    //$$                                       Operation<Void> original) {
    //$$     int mode = HFUTSettings.restoreMode();
    //$$     if (mode == HFUTSettings.MODE_LEGACY && delay != 10) {
    //$$         return; // drop the cooldown tick (covers 0 and 1)
    //$$     }
    //#if MC < 260300
    //$$     if (mode == HFUTSettings.MODE_V263 && delay == 0) {
    //$$         delay = 1;
    //$$     }
    //#endif
    //$$     original.call(instance, pos, block, delay);
    //$$ }
    //#endif
}
