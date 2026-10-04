package dev.whisperlyric.carpet_hfut_addition;

import carpet.api.settings.Rule;
import carpet.api.settings.RuleCategory;
import dev.whisperlyric.carpet_hfut_addition.utils.HFUTRuleCategory;

import java.util.Locale;

/**
 * Carpet rules of Carpet HFUT Addition, parsed into Carpet's global
 * {@code /carpet} manager. Names and descriptions come from
 * {@code assets/carpet-hfut-addition/lang/}.
 */
public class HFUTSettings {
    /** Permission for {@code /player <player> tickingStage}: true/false/ops/0-4, like Carpet's command rules. */
    @Rule(categories = {HFUTRuleCategory.HFUT, RuleCategory.COMMAND, RuleCategory.FEATURE},
          options = {"true", "ops", "0", "1", "2", "3", "4", "false"}, strict = true)
    public static String commandPlayerTickingStage = "ops";

    /** Permission for the read-only {@code /pearltrace list|show}: true/false/ops/0-4; recording is gated by ghostEnderPearlTrace. */
    @Rule(categories = {HFUTRuleCategory.HFUT, RuleCategory.COMMAND, RuleCategory.FEATURE},
          options = {"true", "ops", "0", "1", "2", "3", "4", "false"}, strict = true)
    public static String commandPearlTrace = "ops";

    /** Permission for the destructive {@code /pearltrace purge}: kept separate from the read-only commands. */
    @Rule(categories = {HFUTRuleCategory.HFUT, RuleCategory.COMMAND, RuleCategory.FEATURE},
          options = {"true", "ops", "0", "1", "2", "3", "4", "false"}, strict = true)
    public static String commandPearlTracePurge = "ops";

    /** Ghost pearl forensics (MC-306936): records teleports and exposes {@code /pearltrace}. */
    //#if MC >= 12102
    //$$ @Rule(categories = {HFUTRuleCategory.HFUT, RuleCategory.BUGFIX, RuleCategory.COMMAND})
    //$$ public static boolean ghostEnderPearlTrace = false;
    //#endif

    // ------------------------------------------------------------------
    // Wandering projectile cleanup
    // ------------------------------------------------------------------

    /** Discard shulker bullets the moment they stand in a portal. 1.21.2+ only. */
    //#if MC >= 12102
    //$$ @Rule(categories = {HFUTRuleCategory.HFUT, RuleCategory.FEATURE})
    //$$ public static boolean shulkerBulletPortalCleanup = false;
    //#endif

    /** Discard shulker bullets sitting in a non-entity-ticking chunk. */
    @Rule(categories = {HFUTRuleCategory.HFUT, RuleCategory.FEATURE})
    public static boolean shulkerBulletWeakChunkCleanup = false;

    /** Discard wither skulls sitting in a non-entity-ticking chunk. */
    @Rule(categories = {HFUTRuleCategory.HFUT, RuleCategory.FEATURE})
    public static boolean witherSkullCleanup = false;

    /** Reintroduce minecart acceleration (the bot minecart-chain technique). 26.3+ only. */
    //#if MC >= 260300
    //$$ @Rule(categories = {HFUTRuleCategory.HFUT, RuleCategory.EXPERIMENTAL, RuleCategory.FEATURE})
    //$$ public static boolean minecartAcceleration = false;
    //#endif

    // ------------------------------------------------------------------
    // Upstream (remote) bug fixes
    // ------------------------------------------------------------------

    /**
     * Vanilla clears a map's player records, but map optimizations such as
     * Lithium rewrite that path and miss an edge case, so the records leak.
     */
    @Rule(categories = {HFUTRuleCategory.HFUT, HFUTRuleCategory.REMOTE_BUGFIX})
    public static boolean mapRegistryLeakFix = true;

    /**
     * Fix the relogin avatar leak on Carpet-Org-Addition's 1.21.x line.
     * Requires carpet-org-addition; below 26.1 only (ORG v1.46.0+ fixed itself).
     */
    //#if MC >= 12102
    //#if MC < 260102
    //$$ @Rule(categories = {HFUTRuleCategory.HFUT, HFUTRuleCategory.REMOTE_BUGFIX})
    //$$ public static boolean reloginAvatarLeakFix = true;
    //#endif
    //#endif

    // ------------------------------------------------------------------
    // Tripwire fixes (MC-305475); behaviour lives in mixins/rule/tripwireFix/
    // ------------------------------------------------------------------

    /**
     * A movement that only departs from the tripwire no longer powers it
     * (MC-305475). On 26.2+ it requires {@link #tripwireRestore}=true.
     */
    //#if MC >= 12109
    //$$ @Rule(categories = {HFUTRuleCategory.HFUT, RuleCategory.BUGFIX})
    //$$ public static boolean tripwireIgnoreDepartures = false;

    //$$ /** Whether the departure-veto criterion is active. */
    //$$ public static boolean departureFixActive() {
    //#if MC >= 260200
    //$$     return tripwireIgnoreDepartures && restoreMode() == MODE_LEGACY;
    //#else
    //$$     return tripwireIgnoreDepartures;
    //#endif
    //$$ }
    //#endif

    /**
     * 26.2+ only: {@code false} = vanilla of this version, {@code true} =
     * restored 26.1 base, {@code "26.3"} = raise 26.2's 0t cooldown to 1t.
     */
    //#if MC >= 260200
    //#if MC >= 260300
    //$$ @Rule(categories = {HFUTRuleCategory.HFUT, RuleCategory.BUGFIX}, options = {"false", "true"})
    //#else
    //$$ @Rule(categories = {HFUTRuleCategory.HFUT, RuleCategory.BUGFIX}, options = {"false", "true", "26.3"})
    //#endif
    //$$ public static String tripwireRestore = "false";

    //$$ public static final int MODE_VANILLA = 0; // vanilla of this version
    //$$ public static final int MODE_LEGACY = 1;  // restored 26.1 base
    //#if MC < 260300
    //$$ public static final int MODE_V263 = 2;    // forward-ported 1t cooldown (26.2 only)
    //#endif

    //$$ /** Resolves {@link #tripwireRestore}; unknown values fall back to {@link #MODE_VANILLA}. */
    //$$ public static int restoreMode() {
    //$$     String v = tripwireRestore == null ? "false" : tripwireRestore.toLowerCase(Locale.ROOT);
    //$$     if ("true".equals(v)) {
    //$$         return MODE_LEGACY;
    //$$     }
    //#if MC < 260300
    //$$     if ("26.3".equals(v)) {
    //$$         return MODE_V263;
    //$$     }
    //#endif
    //$$     return MODE_VANILLA;
    //$$ }
    //#endif
}
