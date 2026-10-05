package dev.whisperlyric.carpet_hfut_addition.mixins.rule.creativeNoEntityCollision;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.whisperlyric.carpet_hfut_addition.HFUTSettings;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

/**
 * creativeNoEntityCollision, movement half: for a creative player with the rule
 * on, Entity.collide's single fetch of every entity collision shape returns an
 * empty list, so boats, shulkers and crowds are walked through - flying or
 * landed alike. Block collision is a separate path and untouched.
 */
@Mixin(Entity.class)
public abstract class EntityMixin {

    @WrapOperation(method = "collide(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;",
                   at = @At(value = "INVOKE",
                            target = "Lnet/minecraft/world/level/Level;getEntityCollisions(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"))
    private List<VoxelShape> hfut$noEntityCollision(Level level, Entity mover, AABB box,
                                                    Operation<List<VoxelShape>> original) {
        if (HFUTSettings.creativeNoEntityCollision
                && mover instanceof Player player && player.isCreative() && !player.isSpectator()) {
            return List.of();
        }
        return original.call(level, mover, box);
    }
}
