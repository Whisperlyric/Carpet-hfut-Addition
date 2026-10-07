package dev.whisperlyric.carpet_hfut_addition.mixins.rule.babyMobAvoidGoldenDandelion;

//#if MC >= 260100
//$$ import dev.whisperlyric.carpet_hfut_addition.HFUTSettings;
//$$ import dev.whisperlyric.carpet_hfut_addition.helpers.goldenDandelion.AvoidGoldenDandelionGoal;
//$$ import net.minecraft.world.entity.PathfinderMob;
//$$ import net.minecraft.world.level.block.Blocks;
//#endif
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.frog.Tadpole;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * babyMobAvoidGoldenDandelion, tadpole anchor: tadpoles grow up without
 * being AgeableMob (their growth is AbstractFish's own), so the AgeableMob
 * registration misses them. Every tadpole counts as a baby - the injected
 * baby check is simply true. Guarded like the AgeableMob half; the Tadpole
 * class keeps its package on every 26.x version.
 */
@Mixin(Tadpole.class)
public abstract class TadpoleMixin {

    @Inject(method = "<init>", at = @At("TAIL"))
    private void hfut$fearGoldenDandelion(EntityType<?> type, Level level, CallbackInfo ci) {
        //#if MC >= 260100
        //$$ if (AvoidGoldenDandelionGoal.GOLDEN_DAISY_BABIES.contains(EntityType.getKey(type).getPath())) {
        //$$     ((MobGoalSelectorAccessor) this).hfut$goalSelector().addGoal(3, new AvoidGoldenDandelionGoal(
        //$$             (PathfinderMob) (Object) this,
        //$$             mob -> !HFUTSettings.babyMobAvoidGoldenDandelionIgnoreAgeLocked
        //$$                     || !((Tadpole) (Object) mob).isAgeLocked(),
        //$$             state -> state.is(Blocks.GOLDEN_DANDELION) || state.is(Blocks.POTTED_GOLDEN_DANDELION),
        //$$             Blocks.GOLDEN_DANDELION.asItem()));
        //$$ }
        //#endif
    }
}
