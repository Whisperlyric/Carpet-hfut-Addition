package dev.whisperlyric.carpet_hfut_addition.mixins.rule.dolphinBoatRidingRestore;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.whisperlyric.carpet_hfut_addition.HFUTSettings;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Dolphin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * dolphinBoatRidingRestore: since 1.21.9 boat boarding rejects entities in
 * CANNOT_BE_PUSHED_ONTO_BOATS (dolphin included); flips that check for dolphins.
 * Drift: boat class moved to vehicle.boat at 1.21.11, tag receiver became Entity.is at 26.x.
 * Restricted to >=1.21.9 (earlier versions strip the members below).
 */
@Restriction(require = @Condition(value = "minecraft", versionPredicates = ">=1.21.9"))
@Mixin(targets = {
        //#if MC >= 12111
        //$$ "net.minecraft.world.entity.vehicle.boat.AbstractBoat"
        //#else
        "net.minecraft.world.entity.vehicle.AbstractBoat"
        //#endif
})
public abstract class AbstractBoatMixin {

    //#if MC >= 260100
    //$$ @ModifyExpressionValue(method = "tick", at = @At(value = "INVOKE",
    //$$         target = "Lnet/minecraft/world/entity/Entity;is(Lnet/minecraft/tags/TagKey;)Z"))
    //$$ private boolean hfut$allowDolphinBoarding26(boolean blocked, @Local Entity entity) {
    //$$     return blocked && !(HFUTSettings.dolphinBoatRidingRestore && entity instanceof Dolphin);
    //$$ }
    //$$
    //#elseif MC >= 12109
    //$$ @ModifyExpressionValue(method = "tick", at = @At(value = "INVOKE",
    //$$         target = "Lnet/minecraft/world/entity/EntityType;is(Lnet/minecraft/tags/TagKey;)Z"))
    //$$ private boolean hfut$allowDolphinBoarding(boolean blocked, @Local Entity entity) {
    //$$     return blocked && !(HFUTSettings.dolphinBoatRidingRestore && entity instanceof Dolphin);
    //$$ }
    //#endif
}
