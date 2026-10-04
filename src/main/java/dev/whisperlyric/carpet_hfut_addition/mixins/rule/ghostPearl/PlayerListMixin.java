package dev.whisperlyric.carpet_hfut_addition.mixins.rule.ghostPearl;

import dev.whisperlyric.carpet_hfut_addition.helpers.rule.ghostPearl.PearlTraceHandler;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * {@code PlayerList#removeAll} RETURN (server stop): players whose disconnect
 * lost the race still have unmarked pearls that the chunk save would duplicate;
 * the fix layer marks them UNLOADED_WITH_PLAYER.
 */
@Mixin(PlayerList.class)
public abstract class PlayerListMixin {
    //#if MC >= 12102
    //$$ @Inject(method = "removeAll", at = @At("RETURN"))
    //$$ private void hfut$markStragglerPearls(CallbackInfo ci) {
    //$$     PearlTraceHandler.markStragglersOnStop((PlayerList) (Object) this);
    //$$ }
    //#endif
}
