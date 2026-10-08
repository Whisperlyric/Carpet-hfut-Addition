package dev.whisperlyric.carpet_hfut_addition.helpers.rule.durabilityRng;

import net.minecraft.util.RandomSource;

/**
 * durabilityRngFollowsPlayer bridge: the player is only reachable in ItemStack.hurtAndBreak,
 * the roll only in Enchantment's lambda, so a ThreadLocal (both run on the owning player's
 * thread) carries the sequence; push/pop restore the previous value after a nested call.
 */
public final class PlayerDurabilityRng {

    public static final ThreadLocal<RandomSource> ACTIVE = new ThreadLocal<>();
    private static final ThreadLocal<RandomSource> PREVIOUS = new ThreadLocal<>();

    private PlayerDurabilityRng() {
    }

    public static void push(RandomSource rng) {
        PREVIOUS.set(ACTIVE.get());
        ACTIVE.set(rng);
    }

    public static void pop() {
        RandomSource prev = PREVIOUS.get();
        if (prev != null) {
            ACTIVE.set(prev);
        } else {
            ACTIVE.remove();
        }
        PREVIOUS.remove();
    }
}
