package dev.whisperlyric.carpet_hfut_addition.mixins.rule.fakePlayerOpen;

import dev.whisperlyric.carpet_hfut_addition.helpers.rule.fakePlayerOpen.FakePlayerStorageEditor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * When the editor viewer closes our menu (explicitly, by opening another
 * container, or implicitly), the shadow player is written back to playerdata.
 */
@Mixin(Player.class)
public abstract class PlayerCloseMixin {
    @Shadow
    public AbstractContainerMenu containerMenu;

    @Inject(method = "doCloseContainer", at = @At("HEAD"))
    private void hfut$onStorageMenuClose(CallbackInfo ci) {
        if ((Object) this instanceof ServerPlayer serverPlayer) {
            FakePlayerStorageEditor.onViewerClose(this.containerMenu, serverPlayer);
        }
    }
}
