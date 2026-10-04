package dev.whisperlyric.carpet_hfut_addition.mixins.rule.reloginLeak;

import carpet.patches.EntityPlayerMPFake;
import dev.whisperlyric.carpet_hfut_addition.HFUTSettings;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Relogin avatar leak on Carpet-Org-Addition's 1.21.x line: the scheduled logout
 * returns early while the avatar is already marked removed, so the real
 * disconnect never runs and the avatar lingers. The redirect forces it through,
 * and must ship with {@link PlayerListSaveSkipMixin} (see its note).
 */
@Restriction(require = @Condition("carpet-org-addition"))
@Mixin(targets = "org.carpetorgaddition.periodic.task.schedule.ReLoginTask")
public abstract class ReLoginTaskMixin {

    //#if MC >= 12102
    //#if MC < 260102
    //$$ @Redirect(
    //$$         method = "lambda$logoutPlayer$*",
    //$$         at = @At(
    //$$                 value = "INVOKE",
    //$$                 target = "Lcarpet/patches/EntityPlayerMPFake;isRemoved()Z"
    //$$         ),
    //$$         require = 0
    //$$ )
    //$$ private static boolean hfut$forceDisconnect(EntityPlayerMPFake fakePlayer) {
    //$$     if (HFUTSettings.reloginAvatarLeakFix) {
    //$$         return false; // the avatar is not "already gone" until it has been properly disconnected
    //$$     }
    //$$     return fakePlayer.isRemoved();
    //$$ }
    //#endif
    //#endif
}
