package dev.whisperlyric.carpet_hfut_addition.helpers.water;

import net.minecraft.world.level.material.FluidState;

/**
 * Shared water-state judgement for rules that care about flow depth, valid
 * under both fluid models this mod spans: before 26.4 a source reports
 * {@code isSource()} with amount 8; from 26.4-snapshot-4 the state carries a
 * separate {@code SOURCE} flag with amount 0. "Source or full (falling)
 * column" is the complement of "sideways flow" in both.
 */
public final class FullWaterPredicate {

    private FullWaterPredicate() {
    }

    /** True for a water source or a full (falling) column; false for sideways flow (levels 1-7). */
    public static boolean isSourceOrFull(FluidState state) {
        return state.isSource() || state.getAmount() == 8;
    }
}
