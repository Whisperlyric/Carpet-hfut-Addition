package dev.whisperlyric.carpet_hfut_addition;

import carpet.api.settings.Rule;
import carpet.api.settings.RuleCategory;
import dev.whisperlyric.carpet_hfut_addition.utils.HFUTRuleCategory;

/**
 * Holds {@code fakePlayerTicksLikeRealPlayer} separately so a name conflict
 * with carpet-tis-addition cannot break registration of {@link HFUTSettings}.
 * When TIS is present, its rule owns the global behaviour.
 */
public class FakePlayerTickStageSettings {
    /** Default matches carpet-tis-addition's rule default (false). */
    @Rule(categories = {HFUTRuleCategory.HFUT, RuleCategory.EXPERIMENTAL})
    public static boolean fakePlayerTicksLikeRealPlayer = false;
}
