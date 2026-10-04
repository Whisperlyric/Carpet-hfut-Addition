package dev.whisperlyric.carpet_hfut_addition.utils;

/**
 * Custom rule categories used by Carpet HFUT Addition, besides Carpet's own
 * {@link carpet.api.settings.RuleCategory}. Each becomes a tab in the
 * {@code /carpet} browser, translated with {@code carpet.category.<name>}.
 */
public class HFUTRuleCategory {
    /** The main category; every HFUT rule carries it. */
    public static final String HFUT = "HFUT";

    /** Fixes for upstream bugs confirmed outside the Mojang tracker */
    public static final String REMOTE_BUGFIX = "remotebugfix";
}
