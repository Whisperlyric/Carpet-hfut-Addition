package dev.whisperlyric.carpet_hfut_addition.mixins.rule.brittleIce;

//#if MC >= 260400
//$$ import dev.whisperlyric.carpet_hfut_addition.HFUTSettings;
//$$ import net.minecraft.core.BlockPos;
//$$ import net.minecraft.world.level.Level;
//$$ import net.minecraft.world.level.block.LiquidBlock;
//$$ import net.minecraft.world.level.block.state.BlockState;
//$$ import net.minecraft.world.level.material.FluidState;
//$$ import net.minecraft.world.level.material.FlowingFluid;
//$$ import net.minecraft.world.level.material.Fluids;
//$$ import org.spongepowered.asm.mixin.Final;
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.Shadow;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//$$
//$$ /**
//$$  * lateralFlowingWaterNoBrittleIce: sideways-flowing water (levels 1-7) ignores
//$$  * blue ice instead of turning into brittle ice; sources and falling water
//$$  * still convert like vanilla. 26.4+ only.
//$$  */
//$$ @Mixin(LiquidBlock.class)
//$$ public abstract class LiquidBlockMixin {
//$$
//$$     @Shadow
//$$     @Final
//$$     private FlowingFluid fluid;
//$$
//$$     @Inject(method = "shouldSpreadLiquid", at = @At("HEAD"), cancellable = true)
//$$     private void hfut$flowingWaterIgnoresBlueIce(Level level, BlockPos pos, BlockState state,
//$$                                                  CallbackInfoReturnable<Boolean> cir) {
//$$         // levels 1-7 are sideways flow (0 = source, 8-15 = falling)
//$$         FluidState fluidState = state.getFluidState();
//$$         if (HFUTSettings.lateralFlowingWaterNoBrittleIce
//$$                 && this.fluid == Fluids.WATER
//$$                 && !fluidState.isSource()
//$$                 && fluidState.getAmount() < 8) {
//$$             cir.setReturnValue(true);
//$$         }
//$$     }
//$$ }
//#endif
