package dev.whisperlyric.carpet_hfut_addition;

import carpet.api.settings.Rule;
import carpet.api.settings.RuleCategory;
import dev.whisperlyric.carpet_hfut_addition.utils.HFUTRuleCategory;

/**
 * Contains only {@code ghostEnderPearlFix}, so the class can be left
 * unregistered when Carpet IGNY Addition owns the same-name rule: the field
 * stays at its default {@code false} and every fix mixin degrades to a check.
 */
public class GhostPearlFixSettings {
    //#if MC >= 12102
    //$$ @Rule(categories = {HFUTRuleCategory.HFUT, RuleCategory.BUGFIX})
    //$$ public static boolean ghostEnderPearlFix = false;
    //#endif
}
