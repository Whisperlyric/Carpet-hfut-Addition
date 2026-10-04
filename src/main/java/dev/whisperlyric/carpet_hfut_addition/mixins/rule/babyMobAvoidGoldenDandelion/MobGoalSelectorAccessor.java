package dev.whisperlyric.carpet_hfut_addition.mixins.rule.babyMobAvoidGoldenDandelion;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * goalSelector is declared in Mob, not AgeableMob, and @Shadow does not
 * search superclasses - expose it through an accessor so the AgeableMob
 * mixin can register the avoidance goal. Stable across every version.
 */
@Mixin(Mob.class)
public interface MobGoalSelectorAccessor {

    @Accessor("goalSelector")
    GoalSelector hfut$goalSelector();
}
