package dev.whisperlyric.carpet_hfut_addition.helpers.goldenDandelion;

//#if MC >= 260102
//$$ import dev.whisperlyric.carpet_hfut_addition.HFUTSettings;
//#endif
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.Predicate;

/**
 * babyMobAvoidGoldenDandelion: growable babies steer away from the block the
 * injected predicate names (golden dandelion, 26.1.2+; the predicate lives in
 * the guarded mixin). The baby test is injected too, since tadpoles grow up
 * without being AgeableMob. GOLDEN_DAISY_BABIES holds the affected entity ids
 * as plain strings, dodging the 26.2 EntityType -> EntityTypes move; ids
 * missing on a version (sulfur_cube on 26.1.2) never match. The rule is
 * consulted in canUse/canContinueToUse, so off = one boolean check.
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
    private BlockPos flowerPos;
    private int scanCooldown;

    public AvoidGoldenDandelionGoal(PathfinderMob mob, Predicate<PathfinderMob> babyCheck, Predicate<BlockState> scared) {
        this.mob = mob;
        this.babyCheck = babyCheck;
        this.scared = scared;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        //#if MC >= 260102
        //$$ if (!HFUTSettings.babyMobAvoidGoldenDandelion || !this.babyCheck.test(this.mob) || --this.scanCooldown > 0) {
        //$$     return false;
        //$$ }
        //$$ this.scanCooldown = SCAN_INTERVAL;
        //$$ this.flowerPos = this.findFlower();
        //$$ return this.flowerPos != null;
        //#else
        return false;
        //#endif
    }

    @Override
    public boolean canContinueToUse() {
        //#if MC >= 260102
        //$$ return HFUTSettings.babyMobAvoidGoldenDandelion && this.flowerPos != null && this.babyCheck.test(this.mob)
        //$$         && this.scared.test(this.mob.level().getBlockState(this.flowerPos));
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

    private BlockPos findFlower() {
        BlockPos base = BlockPos.containing(this.mob.position());
        for (BlockPos pos : BlockPos.betweenClosed(base.offset(-SCAN_RADIUS, -2, -SCAN_RADIUS),
                                                   base.offset(SCAN_RADIUS, 2, SCAN_RADIUS))) {
            if (this.scared.test(this.mob.level().getBlockState(pos))) {
                return pos.immutable();
            }
        }
        return null;
    }

    /** Path to a point roughly away from the flower, with a bit of jitter per attempt. */
    private void moveAway() {
        Vec3 away = this.mob.position().subtract(Vec3.atCenterOf(this.flowerPos));
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
