package dev.whisperlyric.carpet_hfut_addition.mixins.rule.mapWorldIdentity;

import dev.whisperlyric.carpet_hfut_addition.helpers.rule.mapWorldIdentity.MapWorldIdentity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * separateMapModWorlds: sendLevelInfo runs on join and on every dimension
 * change, so the identity is refreshed wherever world info syncs.
 */
@Mixin(PlayerList.class)
public abstract class PlayerListMixin {

    @Inject(method = "sendLevelInfo", at = @At("TAIL"))
    private void hfut$sendMapWorldIdentity(ServerPlayer player, ServerLevel level, CallbackInfo ci) {
        MapWorldIdentity.sendXaeroWorldInfo(player);
    }
}
