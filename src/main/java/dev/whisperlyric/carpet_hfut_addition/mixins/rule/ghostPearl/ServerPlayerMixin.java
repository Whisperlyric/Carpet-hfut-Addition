package dev.whisperlyric.carpet_hfut_addition.mixins.rule.ghostPearl;

import dev.whisperlyric.carpet_hfut_addition.helpers.rule.ghostPearl.PearlTraceHandler;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
//#if MC >= 12111
//$$ import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownEnderpearl;
//#else
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
//#endif
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Ghost ender pearl fixes (MC-306936) on {@code ServerPlayer}: intercept the
 * {@code addWithUUID} call in the per-pearl NBT load lambda (recycle on
 * rejection, mark trace origin), and drop non-live pearls at
 * {@code saveEnderPearls} HEAD. Lambda targets are namespace-stable.
 */
@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {
    //#if MC >= 12102
    //$$ @WrapOperation(
    //#if MC >= 260102
    //$$         method = "lambda$loadAndSpawnEnderPearl$0",
    //#elseif MC >= 12105
    //$$         method = {"method_68174", "method_64132"},
    //#else
    //$$         method = {"method_64129", "method_64132"},
    //#endif
    //$$         at = @At(
    //$$                 value = "INVOKE",
    //$$                 target = "Lnet/minecraft/server/level/ServerLevel;addWithUUID(Lnet/minecraft/world/entity/Entity;)Z"
    //$$         )
    //$$ )
    //$$ private static boolean hfut$onPearlLoadAttempt(ServerLevel level, Entity entity, Operation<Boolean> original) {
    //$$     boolean added = original.call(level, entity);
    //$$     if (entity instanceof ThrownEnderpearl pearl) {
    //$$         PearlTraceHandler.onLoadAttempt(pearl, added, level);
    //$$     }
    //$$     return added;
    //$$ }
    //$$
    //$$ @Inject(method = "saveEnderPearls", at = @At("HEAD"))
    //$$ private void hfut$filterPearlsBeforeSave(CallbackInfo ci) {
    //$$     PearlTraceHandler.filterPearlsBeforeSave((ServerPlayer) (Object) this);
    //$$ }
    //#endif
}
