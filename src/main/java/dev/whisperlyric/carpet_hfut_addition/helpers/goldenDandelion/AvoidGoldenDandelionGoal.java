package dev.whisperlyric.carpet_hfut_addition.helpers.goldenDandelion;

//#if MC >= 260102
//$$ import dev.whisperlyric.carpet_hfut_addition.HFUTSettings;
//#endif
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.Predicate;

/**
 * babyMobAvoidGoldenDandelion: growable babies steer away from the golden
 * dandelion whether it is placed (the injected block predicate) or held by a
 * player (the injected item). Entity ids are kept as plain strings, dodging the
 * 26.2 EntityType -> EntityTypes move; ids missing on a version (sulfur_cube on
 * 26.1.2) never match. The rule is checked in canUse/canContinueToUse, so off =
 * one boolean check.
 */
public class AvoidGoldenDandelionGoal extends Goal {

    /** Entity ids of the growable babies golden dandelion repels. */
    public static final Set<String> GOLDEN_DAISY_BABIES = Set.of(
            "armadillo", "axolotl", "bee", "camel", "cat", "chicken", "cow", "dolphin", "donkey",
            "fox", "glow_squid", "goat", "happy_ghast", "hoglin", "horse", "llama", "mooshroom",
            "mule", "nautilus", "ocelot", "panda", "pig", "polar_bear", "rabbit", "sheep",
            "sniffer", "squid", "strider", "sulfur_cube", "tadpole", "trader_llama", "turtle", "wolf");

    private static final int SCAN_INTERVAL = 20;
    private static final int SCAN_RADIUS = 6;
    private static final double FLEE_SPEED = 1.15;

    private final PathfinderMob mob;
    private final Predicate<PathfinderMob> babyCheck;
    private final Predicate<BlockState> scared;
    private final Item fearItem;
    private BlockPos flowerPos;
    private Player holder;
    private int scanCooldown;

    public AvoidGoldenDandelionGoal(PathfinderMob mob, Predicate<PathfinderMob> babyCheck,
                                    Predicate<BlockState> scared, Item fearItem) {
        this.mob = mob;
        this.babyCheck = babyCheck;
        this.scared = scared;
        this.fearItem = fearItem;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        //#if MC >= 260102
        //$$ if (!HFUTSettings.babyMobAvoidGoldenDandelion || !this.babyCheck.test(this.mob) || --this.scanCooldown > 0) {
        //$$     return false;
        //$$ }
        //$$ this.scanCooldown = SCAN_INTERVAL;
        //$$ this.flowerPos = null;
        //$$ this.holder = null;
        //$$ this.findSource();
        //$$ return this.flowerPos != null || this.holder != null;
        //#else
        return false;
        //#endif
    }

    @Override
    public boolean canContinueToUse() {
        //#if MC >= 260102
        //$$ if (!HFUTSettings.babyMobAvoidGoldenDandelion || !this.babyCheck.test(this.mob)) {
        //$$     return false;
        //$$ }
        //$$ if (this.holder != null) {
        //$$     return this.holder.level() == this.mob.level() && this.holder.isAlive()
        //$$             && !this.holder.isRemoved() && this.holdsFearItem(this.holder);
        //$$ }
        //$$ return this.flowerPos != null && this.scared.test(this.mob.level().getBlockState(this.flowerPos));
        //#else
        return false;
        //#endif
    }

    @Override
    public void start() {
        this.moveAway();
    }

    @Override
    public void tick() {
        if (this.mob.getNavigation().isDone()) {
            this.moveAway();
        }
    }

    private void findSource() {
        BlockPos base = BlockPos.containing(this.mob.position());
        for (BlockPos pos : BlockPos.betweenClosed(base.offset(-SCAN_RADIUS, -2, -SCAN_RADIUS),
                                                   base.offset(SCAN_RADIUS, 2, SCAN_RADIUS))) {
            if (this.scared.test(this.mob.level().getBlockState(pos))) {
                this.flowerPos = pos.immutable();
                return;
            }
        }
        for (Player player : this.mob.level().getEntitiesOfClass(Player.class,
                this.mob.getBoundingBox().inflate(SCAN_RADIUS, 2.0, SCAN_RADIUS), this::holdsFearItem)) {
            this.holder = player;
            return;
        }
    }

    private boolean holdsFearItem(Player player) {
        return player.getMainHandItem().getItem() == this.fearItem
                || player.getOffhandItem().getItem() == this.fearItem;
    }

    private Vec3 sourcePos() {
        return this.flowerPos != null ? Vec3.atCenterOf(this.flowerPos) : this.holder.position();
    }

    /** Path to a point roughly away from the source, with a bit of jitter per attempt. */
    private void moveAway() {
        Vec3 away = this.mob.position().subtract(this.sourcePos());
        if (away.horizontalDistanceSqr() < 1.0E-4) {
            away = new Vec3(this.mob.getRandom().nextDouble() - 0.5, 0.0, this.mob.getRandom().nextDouble() - 0.5);
        }
        double jitter = Math.toRadians((this.mob.getRandom().nextDouble() - 0.5) * 90.0);
        double cos = Math.cos(jitter);
        double sin = Math.sin(jitter);
        Vec3 dir = new Vec3(away.x * cos - away.z * sin, 0.0, away.x * sin + away.z * cos).normalize();
        Vec3 target = this.mob.position().add(dir.scale(8.0 + this.mob.getRandom().nextDouble() * 4.0));
        this.mob.getNavigation().moveTo(target.x, Mth.clamp(target.y, this.mob.level().getMinBuildHeight(),
                this.mob.level().getMaxBuildHeight() - 1), target.z, FLEE_SPEED);
    }
}
