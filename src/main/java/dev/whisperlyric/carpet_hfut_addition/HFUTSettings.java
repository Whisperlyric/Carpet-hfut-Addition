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

    /**
     * Reintroduce MC-311022: swapping equipment quickly leaves attribute
     * modifiers uncleared for one tick (soul speed's movement_speed is the
     * most visible case). 26.3+ only - 26.3 hands the captured broken stack
     * to the removal path instead of re-reading the already-cleared slot.
     */
    //#if MC >= 260300
    //$$ @Rule(categories = {HFUTRuleCategory.HFUT, RuleCategory.EXPERIMENTAL, RuleCategory.FEATURE})
    //$$ public static boolean attributeModifierRemovalDelay = false;
    //#endif

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
    //#if MC < 260100
    //$$ @Rule(categories = {HFUTRuleCategory.HFUT, HFUTRuleCategory.REMOTE_BUGFIX})
    //$$ public static boolean reloginAvatarLeakFix = true;
    //#endif
    //#endif

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

    /**
     * Fix the mining fatigue dig-speed table: 0.0027 and 8.1E-4 should be
     * 0.027 and 0.0081, so fatigue III+ digs 10x too slow. 26.3 replaced
     * the table with 0.3^(amplifier+1).
     */
    //#if MC < 260300
    @Rule(categories = {HFUTRuleCategory.HFUT, RuleCategory.BUGFIX})
    public static boolean miningFatigueDigSpeedFix = false;
    //#endif

    /**
     * Fix MC-272431: since 19w08b the dragon's vertical acceleration toward
     * its target node is scaled by 0.01 instead of 0.1 (1.12 had 0.1), so
     * it can barely climb or dive. Unfixed in every supported version.
     */
    @Rule(categories = {HFUTRuleCategory.HFUT, RuleCategory.BUGFIX})
    public static boolean enderDragonVerticalVelocityFix = false;

    /**
     * Port of the 26.3 villager trade change: a qualifying trade levels the
     * villager up and unlocks the next tier instantly, without closing and
     * reopening the screen.
     */
    //#if MC < 260300
    @Rule(categories = {HFUTRuleCategory.HFUT, RuleCategory.FEATURE})
    public static boolean villagerInstantLevelUp = false;
    //#endif

    /**
     * Port of the 26.3 live price sync: restocks, demand catch-up, gossip
     * and player reputation events reprice the offers shown to a player
     * who is mid-trade, instead of waiting for the next screen open.
     */
    //#if MC < 260300
    @Rule(categories = {HFUTRuleCategory.HFUT, RuleCategory.FEATURE})
    public static boolean villagerLivePriceSync = false;
    //#endif

    /**
     * Baby mobs keep away from golden dandelions (placed, potted, or held by a
     * player), the way piglins fear soul fire. Only exists where the block does
     * (26.1+).
     */
    //#if MC >= 260100
    //$$ @Rule(categories = {HFUTRuleCategory.HFUT, RuleCategory.FEATURE})
    //$$ public static boolean babyMobAvoidGoldenDandelion = false;
    //#endif

    /**
     * Babies whose growth a golden dandelion already locked (was fed) stop
     * avoiding it. Inert unless babyMobAvoidGoldenDandelion is on.
     */
    //#if MC >= 260100
    //$$ @Rule(categories = {HFUTRuleCategory.HFUT, RuleCategory.FEATURE})
    //$$ public static boolean babyMobAvoidGoldenDandelionIgnoreAgeLocked = false;
    //#endif

    /** Bone meal on a mature (age 7) melon/pumpkin stem grows the fruit block. */
    @Rule(categories = {HFUTRuleCategory.HFUT, RuleCategory.FEATURE})
    public static boolean bonemealGrowMelons = false;

    /**
     * Port of the 26.3 map change: off-map player markers rotate with the
     * player's facing instead of pointing north. In-map markers always did.
     */
    //#if MC < 260300
    @Rule(categories = {HFUTRuleCategory.HFUT, RuleCategory.FEATURE})
    public static boolean mapPlayerIconRotation = false;
    //#endif

    /**
     * Creative players are neither stopped by entity hitboxes nor pushed
     * around by them - standing or flying, so landing never re-enables
     * collision. Block collision is untouched.
     */
    @Rule(categories = {HFUTRuleCategory.HFUT, RuleCategory.FEATURE})
    public static boolean creativeNoEntityCollision = false;

    /**
     * Wind charge explosions no longer trigger buttons, levers, doors,
     * trapdoors or fence gates. No other explosion source is affected.
     */
    @Rule(categories = {HFUTRuleCategory.HFUT, RuleCategory.FEATURE})
    public static boolean windChargeNoRedstoneActivation = false;

    /** Wind charge explosions no longer anger bees through hives. */
    @Rule(categories = {HFUTRuleCategory.HFUT, RuleCategory.FEATURE})
    public static boolean windChargeNoBeehiveAnger = false;

    /**
     * Wind charge explosions no longer knock back non-living entities; living
     * entities, players and the wind-charge jump are unaffected.
     */
    @Rule(categories = {HFUTRuleCategory.HFUT, RuleCategory.FEATURE})
    public static boolean windChargeNoNonLivingKnockback = false;
}
