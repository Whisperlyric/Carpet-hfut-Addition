package dev.whisperlyric.carpet_hfut_addition.mixins.rule.tripwireFix;

import dev.whisperlyric.carpet_hfut_addition.utils.interfaces.SegmentContext;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
//#if MC >= 12109
//$$ import it.unimi.dsi.fastutil.longs.LongSet;
//$$ import net.minecraft.world.entity.InsideBlockEffectApplier;
//#endif
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/**
 * Read-only observer feeding {@link SegmentContext}: captures the per-segment
 * sweep endpoints, written at sweep entry and cleared when the outer overload
 * returns. The segment API only exists on MC >= 12109, so every member is
 * //$$-guarded.
 */
@Mixin(Entity.class)
public abstract class EntityMixin
//#if MC >= 12109
        implements SegmentContext
//#endif
{
    //#if MC >= 12109
    //$$ private Vec3 hfut$segFrom;
    //$$ private Vec3 hfut$segTo;

    //$$ /** vanilla's per-position bounding box (protected), same convention as the sweep system */
    //$$ @Shadow
    //$$ protected abstract AABB makeBoundingBox(Vec3 position);

    //$$ @Inject(
    //$$         method = "checkInsideBlocks(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;"
    //$$                 + "Lnet/minecraft/world/entity/InsideBlockEffectApplier$StepBasedCollector;"
    //$$                 + "Lit/unimi/dsi/fastutil/longs/LongSet;I)I",
    //$$         at = @At("HEAD")
    //$$ )
    //$$ private void hfut$captureSegment(Vec3 from, Vec3 to,
    //$$                                  InsideBlockEffectApplier.StepBasedCollector collector,
    //$$                                  LongSet visitedBlocks, int maxIterations,
    //$$                                  CallbackInfoReturnable<Integer> cir) {
    //$$     this.hfut$segFrom = from;
    //$$     this.hfut$segTo = to;
    //$$ }

    //$$ @Inject(
    //$$         method = "checkInsideBlocks(Ljava/util/List;"
    //$$                 + "Lnet/minecraft/world/entity/InsideBlockEffectApplier$StepBasedCollector;)V",
    //$$         at = @At("RETURN")
    //$$ )
    //$$ private void hfut$clearSegment(List<?> movements,
    //$$                                InsideBlockEffectApplier.StepBasedCollector collector,
    //$$                                CallbackInfo ci) {
    //$$     this.hfut$segFrom = null;
    //$$     this.hfut$segTo = null;
    //$$ }

    //$$ public Vec3 hfut$segFrom() {
    //$$     return this.hfut$segFrom;
    //$$ }
    //$$
    //$$ public Vec3 hfut$segTo() {
    //$$     return this.hfut$segTo;
    //$$ }
    //$$
    //$$ public AABB hfut$segBox(Vec3 at) {
    //$$     return this.makeBoundingBox(at);
    //$$ }
    //#endif
}
