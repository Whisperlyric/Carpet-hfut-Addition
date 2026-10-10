package dev.whisperlyric.carpet_hfut_addition.mixins.rule.fakePlayerOpen;

import dev.whisperlyric.carpet_hfut_addition.helpers.rule.fakePlayerOpen.FakePlayerStorageEditor;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Runs before vanilla (or carpet's fake-player spawn) reads the joining
 * player's data, so an open offline editor is flushed to disk first and the
 * joiner reads the edited state - no duplication window.
 */
@Mixin(PlayerList.class)
public abstract class PlayerListMixin {
    @Inject(method = "placeNewPlayer", at = @At("HEAD"))
    private void hfut$flushEditorBeforeJoin(Connection connection, ServerPlayer player,
                                            CommonListenerCookie cookie, CallbackInfo ci) {
        FakePlayerStorageEditor.flushFor(player.getUUID());
    }
}
