package dev.whisperlyric.carpet_hfut_addition.mixins.rule.minecartAcceleration;

import net.minecraft.world.entity.vehicle.Minecart;
import dev.whisperlyric.carpet_hfut_addition.HFUTSettings;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * minecartAcceleration (26.3+): when an interaction actually mounted this cart,
 * return PASS like 26.2, so the action is not consumed and the 4t use cooldown
 * never arms — bots can chain-mount carts again.
 */
@Mixin(Minecart.class)
public abstract class MinecartMixin {

    //#if MC >= 260300
    //$$ @Inject(method = "interact", at = @At("RETURN"), cancellable = true)
    //$$ private void hfut$restore262ReturnShape(Player player, InteractionHand hand, Vec3 location,
    //$$                                           CallbackInfoReturnable<InteractionResult> cir) {
    //$$     if (!HFUTSettings.minecartAcceleration) {
    //$$         return;
    //$$     }
    //$$     InteractionResult result = cir.getReturnValue();
    //$$     if (result != null && result.consumesAction() && player.getVehicle() == (Object) this) {
    //$$         cir.setReturnValue(InteractionResult.PASS);
    //$$     }
    //$$ }
    //#endif
}
