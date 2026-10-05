package dev.whisperlyric.carpet_hfut_addition.mixins.rule.babyMobAvoidGoldenDandelion;

//#if MC >= 260102
//$$ import dev.whisperlyric.carpet_hfut_addition.HFUTSettings;
//$$ import dev.whisperlyric.carpet_hfut_addition.helpers.goldenDandelion.AvoidGoldenDandelionGoal;
//$$ import net.minecraft.world.entity.PathfinderMob;
//$$ import net.minecraft.world.level.block.Blocks;
//#endif
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * babyMobAvoidGoldenDandelion: registers the avoidance goal only for the
 * whitelisted growable babies (AvoidGoldenDandelionGoal.GOLDEN_DAISY_BABIES,
 * matched by entity id); others pay nothing. Golden dandelion only exists on
 * 26.1.2+, so the registration is guarded. The id check stays string-based
 * because the entity type constants moved to EntityTypes in 26.2.
 */
@Mixin(AgeableMob.class)
public abstract class AgeableMobMixin {

    @Inject(method = "<init>", at = @At("TAIL"))
    private void hfut$fearGoldenDandelion(EntityType<?> type, Level level, CallbackInfo ci) {
        //#if MC >= 260102
        //$$ if (AvoidGoldenDandelionGoal.GOLDEN_DAISY_BABIES.contains(EntityType.getKey(type).getPath())) {
        //$$     ((MobGoalSelectorAccessor) this).hfut$goalSelector().addGoal(3, new AvoidGoldenDandelionGoal(
        //$$             (PathfinderMob) (Object) this,
        //$$             mob -> mob instanceof AgeableMob ageable && ageable.isBaby()
        //$$                     && (!HFUTSettings.babyMobAvoidGoldenDandelionIgnoreAgeLocked || !ageable.isAgeLocked()),
        //$$             state -> state.is(Blocks.GOLDEN_DANDELION) || state.is(Blocks.POTTED_GOLDEN_DANDELION),
        //$$             Blocks.GOLDEN_DANDELION.asItem()));
        //$$ }
        //#endif
    }
}
