package dev.whisperlyric.carpet_hfut_addition;

import carpet.api.settings.Rule;
import carpet.api.settings.RuleCategory;
import dev.whisperlyric.carpet_hfut_addition.utils.HFUTRuleCategory;

/**
 * Permissions for {@code /player <player> open inventory|enderchest}, split by
 * target identity: offline (v3) uuids use {@code commandFakePlayerOpenStorage},
 * Mojang-account (v4) uuids - real players and 26.x premium-named bots - use
 * {@code commandRealPlayerOpenStorage}. Both all-versions; no GCA needed.
 */
public class FakePlayerOpenStorageSettings {
    /** Permission for opening offline-identity (v3 uuid) storage: true/false/ops/0-4. */
    @Rule(categories = {HFUTRuleCategory.HFUT, RuleCategory.COMMAND, RuleCategory.FEATURE},
          options = {"true", "ops", "0", "1", "2", "3", "4", "false"}, strict = true)
    public static String commandFakePlayerOpenStorage = "ops";

    /** Permission for opening Mojang-account (v4 uuid) storage. Default false: this touches real players' saved data. */
    @Rule(categories = {HFUTRuleCategory.HFUT, RuleCategory.COMMAND, RuleCategory.FEATURE},
          options = {"true", "ops", "0", "1", "2", "3", "4", "false"}, strict = true)
    public static String commandRealPlayerOpenStorage = "false";
}
