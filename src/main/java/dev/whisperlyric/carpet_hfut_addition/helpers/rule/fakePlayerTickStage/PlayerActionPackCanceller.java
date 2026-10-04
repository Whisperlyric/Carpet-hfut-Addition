package dev.whisperlyric.carpet_hfut_addition.helpers.rule.fakePlayerTickStage;

/**
 * Set around the entity-stage {@code ServerPlayer#tick} of fake players whose
 * action pack has been deferred to the async phase, so carpet's own action
 * pack tick (injected into ServerPlayer#tick) is skipped for them.
 */
public class PlayerActionPackCanceller {
    public static final ThreadLocal<Boolean> cancelled = ThreadLocal.withInitial(() -> false);
}
