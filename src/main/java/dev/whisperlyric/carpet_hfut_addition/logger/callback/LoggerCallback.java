package dev.whisperlyric.carpet_hfut_addition.logger.callback;

import carpet.logging.Logger;
import net.minecraft.server.level.ServerPlayer;

/**
 * Optional subscribe/unsubscribe hook for a logger, referenced from the
 * {@code callback} element of the {@link dev.whisperlyric.carpet_hfut_addition.logger.annotation.Logger}
 * annotation.
 */
public interface LoggerCallback {
    void onSubscribe(Logger logger, ServerPlayer player, String option);

    void onUnsubscribe(Logger logger, String playerName);
}
