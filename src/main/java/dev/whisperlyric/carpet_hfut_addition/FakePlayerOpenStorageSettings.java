package dev.whisperlyric.carpet_hfut_addition;

import carpet.api.settings.Rule;
import carpet.api.settings.RuleCategory;
import dev.whisperlyric.carpet_hfut_addition.utils.HFUTRuleCategory;

/**
 * Permission for {@code /player <player> open inventory|enderchest}, which is
 * self-contained and available with or without GCA.
 */
public class FakePlayerOpenStorageSettings {
    /** Permission for /player <player> open inventory|enderchest: true/false/ops/0-4. */
    @Rule(categories = {HFUTRuleCategory.HFUT, RuleCategory.COMMAND, RuleCategory.FEATURE},
          options = {"true", "ops", "0", "1", "2", "3", "4", "false"}, strict = true)
    public static String commandFakePlayerOpenStorage = "ops";
}
