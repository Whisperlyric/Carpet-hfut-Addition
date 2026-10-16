package dev.whisperlyric.carpet_hfut_addition.mixins.rule.creativeNoEntityCollision;

import dev.whisperlyric.carpet_hfut_addition.HFUTSettings;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * standableEntitiesCreativeNoCollision, boat half.
 *
 * <p>Boats stay solid through their own canBeCollidedWith override, so only a hook here
 * reaches them. Answering false for rule creative players only leaves everyone else blocked.
 *
 * <p>Bounded to 1.21.6+, where canBeCollidedWith is first told who is moving; the boat
 * class also moved to vehicle.boat at 1.21.11.
 */
@Restriction(require = @Condition(value = "minecraft", versionPredicates = ">=1.21.6"))
@Mixin(targets = {
        //#if MC >= 12111
        //$$ "net.minecraft.world.entity.vehicle.boat.AbstractBoat"
        //#else
        "net.minecraft.world.entity.vehicle.AbstractBoat"
        //#endif
})
public abstract class AbstractBoatMixin {

    //#if MC >= 12106
    //$$ @Inject(method = "canBeCollidedWith", at = @At("HEAD"), cancellable = true)
    //$$ private void hfut$standableNoCollision(Entity other, CallbackInfoReturnable<Boolean> cir) {
    //$$     if (HFUTSettings.creativeNoEntityCollision
    //$$             && HFUTSettings.standableEntitiesCreativeNoCollision
    //$$             && other instanceof Player player && player.isCreative() && !player.isSpectator()) {
    //$$         cir.setReturnValue(false);
    //$$     }
    //$$ }
    //#endif
}
