package dev.whisperlyric.carpet_hfut_addition.mixins.rule.beaconDownwardsInfiniteRange;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.whisperlyric.carpet_hfut_addition.HFUTSettings;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

/**
 * beaconDownwardsInfiniteRange: 效果判定框向下无限延伸，水平范围不变.
 * getEntitiesOfClass(Class, AABB) 的 descriptor 各版本一致，故全版本共用同一锚点，无需版本分流.
 */
@Mixin(BeaconBlockEntity.class)
public abstract class BeaconBlockEntityMixin {

    @WrapOperation(
            method = "applyEffects",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;getEntitiesOfClass(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"
            )
    )
    private static List<?> hfut$extendBoxDownwards(Level level, Class<?> clazz, AABB box, Operation<List<?>> original) {
        if (!HFUTSettings.beaconDownwardsInfiniteRange) {
            return original.call(level, clazz, box);
        }
        return original.call(level, clazz, box.expandTowards(0.0D, -1.0E7D, 0.0D));
    }
}
