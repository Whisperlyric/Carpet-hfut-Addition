package dev.whisperlyric.carpet_hfut_addition.mixins.rule.bonemealMelon;

import dev.whisperlyric.carpet_hfut_addition.HFUTSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * bonemealGrowMelons: vanilla rejects bone meal on mature stems outright
 * (age == 7), so fruit only comes from random ticks. With the rule on a
 * mature stem becomes a valid target and performBonemeal replays vanilla's
 * randomTick fruit placement - every placement detail stays vanilla because
 * the vanilla method does the work.
 */
@Mixin(StemBlock.class)
public abstract class StemBlockMixin {

    @Shadow
    protected abstract void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random);

    private static boolean hfut$matureStem(BlockState state) {
        return state.getValue(StemBlock.AGE) >= StemBlock.MAX_AGE;
    }

    //#if MC >= 260300
    //$$ @Inject(method = "isValidBonemealTarget", at = @At("HEAD"), cancellable = true)
    //$$ private void hfut$allowMatureStem(LevelReader level, BlockPos pos, BlockState state,
    //$$                                   net.minecraft.world.level.block.BonemealSource source,
    //$$                                   CallbackInfoReturnable<Boolean> cir) {
    //$$     if (HFUTSettings.bonemealGrowMelons && hfut$matureStem(state)) {
    //$$         cir.setReturnValue(true);
    //$$     }
    //$$ }
    //#else
    @Inject(method = "isValidBonemealTarget", at = @At("HEAD"), cancellable = true)
    private void hfut$allowMatureStem(LevelReader level, BlockPos pos, BlockState state,
                                      CallbackInfoReturnable<Boolean> cir) {
        if (HFUTSettings.bonemealGrowMelons && hfut$matureStem(state)) {
            cir.setReturnValue(true);
        }
    }
    //#endif

    //#if MC >= 260300
    //$$ @Inject(method = "performBonemeal", at = @At("HEAD"), cancellable = true)
    //$$ private void hfut$growFruit(ServerLevel level, RandomSource random, BlockPos pos, BlockState state,
    //$$                            net.minecraft.world.level.block.BonemealSource source, CallbackInfo ci) {
    //$$     if (HFUTSettings.bonemealGrowMelons && hfut$matureStem(state)) {
    //$$         hfut$placeFruit(level, random, pos, state);
    //$$         ci.cancel();
    //$$     }
    //$$ }
    //#else
    @Inject(method = "performBonemeal", at = @At("HEAD"), cancellable = true)
    private void hfut$growFruit(ServerLevel level, RandomSource random, BlockPos pos, BlockState state, CallbackInfo ci) {
        if (HFUTSettings.bonemealGrowMelons && hfut$matureStem(state)) {
            hfut$placeFruit(level, random, pos, state);
            ci.cancel();
        }
    }
    //#endif

    /**
     * Each randomTick picks one random side; a miss (blocked side) leaves
     * the stem in place, any success converts it to an attached stem. Retry
     * until attached so one free side is near-certainly enough.
     */
    private void hfut$placeFruit(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        for (int i = 0; i < 24 && level.getBlockState(pos).getBlock() instanceof StemBlock; i++) {
            this.randomTick(state, level, pos, random);
        }
    }
}
