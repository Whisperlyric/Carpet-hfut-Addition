package dev.whisperlyric.carpet_hfut_addition.mixins.client.rule.hidePauseSocial;

//#if MC >= 260200
//$$ import dev.whisperlyric.carpet_hfut_addition.client.ClientToggles;
//$$ import net.minecraft.client.gui.components.SpriteIconButton;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#endif
import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;

/**
 * hidePauseSocialRow, client half: 26.2+ puts a right-hand icon row next to
 * Multiplayer (Friends / language / accessibility). When the toggle is on the whole
 * row is removed by hiding every sprite-icon button vanilla placed there; that leaves
 * no gap because those buttons live on their own manually positioned row, separate
 * from the Options / Quit buttons. ModMenu's own buttons are left untouched.
 * Registered on every version for the same reason as {@link PauseScreenMixin}.
 */
@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin {

    //#if MC >= 260200
    //$$ @Inject(method = "init", at = @At("TAIL"))
    //$$ private void hfut$removeTitleIconRow(CallbackInfo ci) {
    //$$     if (!ClientToggles.hidePauseSocialRow) {
    //$$         return;
    //$$     }
    //$$     for (var child : ((TitleScreen) (Object) this).children()) {
    //$$         if (child instanceof SpriteIconButton icon
    //$$                 && !child.getClass().getName().startsWith("com.terraformersmc.modmenu.")) {
    //$$             icon.visible = false;
    //$$         }
    //$$     }
    //$$ }
    //#endif
}
