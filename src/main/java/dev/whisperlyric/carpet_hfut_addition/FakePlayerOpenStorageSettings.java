package dev.whisperlyric.carpet_hfut_addition;

import carpet.api.settings.Rule;
import carpet.api.settings.RuleCategory;
import dev.whisperlyric.carpet_hfut_addition.utils.HFUTRuleCategory;

/**
 * Permissions for {@code /player <player> open inventory|enderchest}.
 */
public class FakePlayerOpenStorageSettings {
    /** Permission while the target is online: true/false/ops/0-4. */
    @Rule(categories = {HFUTRuleCategory.HFUT, RuleCategory.COMMAND, RuleCategory.FEATURE},
          options = {"true", "ops", "0", "1", "2", "3", "4", "false"}, strict = true)
    public static String commandFakePlayerRemoteOpenStorage = "ops";

    /** Permission while the target is offline: true/false/ops/0-4. */
    @Rule(categories = {HFUTRuleCategory.HFUT, RuleCategory.COMMAND, RuleCategory.FEATURE},
          options = {"true", "ops", "0", "1", "2", "3", "4", "false"}, strict = true)
    public static String commandOfflineFakePlayerOpenStorage = "ops";
}
