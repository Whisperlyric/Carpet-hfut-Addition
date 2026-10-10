package dev.whisperlyric.carpet_hfut_addition.utils;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Tags every line of one paged command result (header, rows, page arrows) with
 * a batch id, so a client with our mod reads the tag back and drops the
 * previous page - pages replace instead of stack. The tag rides a zero-width
 * sibling (empty text, insertion-only style) appended to the line, NOT the
 * line's own style: vanilla shift-click pastes the clicked glyphs' insertion
 * into the chat input, and a root insertion would surface the raw tag there.
 * The marker has no glyphs, so it can never be clicked; the client mixin scans
 * the line's siblings for it. Plain text and the log are untouched either way.
 */
public final class HFUTChatPage {

    public static final String LAZYCHUNK = "lazychunk";
    public static final String PEARLTRACE = "pearltrace";

    private static final String PREFIX = "hfut:page:";
    private static final AtomicLong NEXT_BATCH = new AtomicLong();

    private HFUTChatPage() {
    }

    /** A fresh batch id: every line of one page shares it, the next page gets a different one. */
    public static long nextBatch() {
        return NEXT_BATCH.incrementAndGet();
    }

    /** Appends the invisible page-tag carrier to one line (see class doc for why a sibling). */
    public static MutableComponent mark(MutableComponent line, String channel, long batch) {
        return line.append(Component.empty()
                .withStyle(style -> style.withInsertion(PREFIX + channel + ":" + batch)));
    }

    /** Decodes a style insertion into a page tag, or {@code null} when it is not ours. */
    public static PageTag parse(String insertion) {
        if (insertion == null || !insertion.startsWith(PREFIX)) {
            return null;
        }
        String body = insertion.substring(PREFIX.length());
        int separator = body.lastIndexOf(':');
        if (separator <= 0) {
            return null;
        }
        try {
            return new PageTag(body.substring(0, separator), Long.parseLong(body.substring(separator + 1)));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** Which paged command a line belongs to, and which rendering of it. */
    public record PageTag(String channel, long batch) {
    }
}
