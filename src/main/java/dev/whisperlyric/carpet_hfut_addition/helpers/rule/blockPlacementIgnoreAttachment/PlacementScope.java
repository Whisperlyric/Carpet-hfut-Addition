package dev.whisperlyric.carpet_hfut_addition.helpers.rule.blockPlacementIgnoreAttachment;

/**
 * Marks "a block is being placed right now", so attachment checks made along
 * the placement path can be skipped without touching the ones that run on
 * neighbour updates afterwards.
 */
public final class PlacementScope {
    private static final ThreadLocal<int[]> DEPTH = ThreadLocal.withInitial(() -> new int[1]);

    private PlacementScope() {
    }

    public static void enter() {
        DEPTH.get()[0]++;
    }

    public static void exit() {
        int[] depth = DEPTH.get();
        if (depth[0] > 0) {
            depth[0]--;
        }
    }

    public static boolean active() {
        return DEPTH.get()[0] > 0;
    }
}
