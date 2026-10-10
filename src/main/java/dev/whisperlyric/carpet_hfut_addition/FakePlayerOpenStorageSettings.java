package dev.whisperlyric.carpet_hfut_addition;

import carpet.api.settings.Rule;
import carpet.api.settings.RuleCategory;
import dev.whisperlyric.carpet_hfut_addition.utils.HFUTRuleCategory;

/**
 * Parsed only when GCA is present, since the command rides its fake player
 * interface; without GCA the rule and command don't exist.
 */
public class FakePlayerOpenStorageSettings {
    /** Permission for /player <player> open inventory|enderchest: true/false/ops/0-4. */
    @Rule(categories = {HFUTRuleCategory.HFUT, RuleCategory.COMMAND, RuleCategory.FEATURE},
          options = {"true", "ops", "0", "1", "2", "3", "4", "false"}, strict = true)
    public static String commandFakePlayerOpenStorage = "ops";
}
