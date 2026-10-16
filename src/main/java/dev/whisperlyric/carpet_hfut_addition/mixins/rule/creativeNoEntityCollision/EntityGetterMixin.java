package dev.whisperlyric.carpet_hfut_addition.mixins.rule.creativeNoEntityCollision;

import dev.whisperlyric.carpet_hfut_addition.HFUTSettings;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.EntityGetter;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/**
 * creativeNoEntityCollision, movement half.
 *
 * <p>Entity.collide collects all entity collision shapes via EntityGetter.getEntityCollisions.
 * This is an interface default method with no overrides in Level or its subclasses.
 * Intercepting at its entry point clears entity collisions for the mover without modifying call sites;
 * redirecting here avoids injection conflicts with other mods such as Lithium.
 *
 * <p>Returning an empty list makes boats, shulkers and other entities passable for rule creative players.
 * Block collision is gathered separately inside Entity.collide and remains unaffected.
 */

@Mixin(EntityGetter.class)
public interface EntityGetterMixin {

    @Inject(method = "getEntityCollisions", at = @At("HEAD"), cancellable = true)
    private void hfut$creativeNoEntityCollision(Entity entity, AABB box, CallbackInfoReturnable<List<VoxelShape>> cir) {
        if (HFUTSettings.creativeNoEntityCollision
                && entity instanceof Player player && player.isCreative() && !player.isSpectator()) {
            cir.setReturnValue(List.of());
        }
    }
}
