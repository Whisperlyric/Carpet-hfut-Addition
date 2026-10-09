package dev.whisperlyric.carpet_hfut_addition.mixins.client.rule.hidePauseSocial;

//#if MC >= 260200
//$$ import dev.whisperlyric.carpet_hfut_addition.client.ClientToggles;
//$$ import net.minecraft.client.gui.layouts.LayoutElement;
//$$ import net.minecraft.client.gui.layouts.LayoutSettings;
//$$ import net.minecraft.client.gui.layouts.LinearLayout;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.ModifyArg;
//#endif
import net.minecraft.client.gui.screens.PauseScreen;
import org.spongepowered.asm.mixin.Mixin;

/**
 * hidePauseSocialRow, client half. 26.2+ adds a social row (bug / feedback / Friends /
 * reporting) to the pause menu grid; when on, this empties the row and zeroes its cell
 * padding just before vanilla inserts it, so it collapses with no leftover gap.
 */
@Mixin(PauseScreen.class)
public abstract class PauseScreenMixin {

    //#if MC >= 260200
    //$$ @ModifyArg(
    //$$         method = "createPauseMenu",
    //$$         at = @At(
    //$$                 value = "INVOKE",
    //$$                 target = "Lnet/minecraft/client/gui/layouts/GridLayout$RowHelper;addChild(Lnet/minecraft/client/gui/layouts/LayoutElement;ILnet/minecraft/client/gui/layouts/LayoutSettings;)Lnet/minecraft/client/gui/layouts/LayoutElement;",
    //$$                 ordinal = 1),
    //$$         index = 0)
    //$$ private LayoutElement hfut$emptySocialRow(LayoutElement row) {
    //$$     if (ClientToggles.hidePauseSocialRow && row instanceof LinearLayout socialRow) {
    //$$         socialRow.removeChildren();
    //$$     }
    //$$     return row;
    //$$ }
    //$$
    //$$ @ModifyArg(
    //$$         method = "createPauseMenu",
    //$$         at = @At(
    //$$                 value = "INVOKE",
    //$$                 target = "Lnet/minecraft/client/gui/layouts/GridLayout$RowHelper;addChild(Lnet/minecraft/client/gui/layouts/LayoutElement;ILnet/minecraft/client/gui/layouts/LayoutSettings;)Lnet/minecraft/client/gui/layouts/LayoutElement;",
    //$$                 ordinal = 1),
    //$$         index = 2)
    //$$ private LayoutSettings hfut$zeroSocialRowPadding(LayoutSettings settings) {
    //$$     return ClientToggles.hidePauseSocialRow ? settings.padding(0) : settings;
    //$$ }
    //#endif
}
