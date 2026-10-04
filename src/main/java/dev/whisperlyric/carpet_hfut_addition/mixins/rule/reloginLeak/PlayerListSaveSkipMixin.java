package dev.whisperlyric.carpet_hfut_addition.mixins.rule.reloginLeak;

import carpet.patches.EntityPlayerMPFake;
import dev.whisperlyric.carpet_hfut_addition.HFUTSettings;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Pair half of {@link ReLoginTaskMixin}: ORG skipped the disconnect of an
 * already-removed fake player to avoid a second save wiping the riding-entity
 * NBT. Forcing the disconnect through re-opens that path, so the re-save is
 * cancelled here. Below 26.1 only, like the rule.
 */
@Restriction(require = @Condition("carpet-org-addition"))
@Mixin(PlayerList.class)
public abstract class PlayerListSaveSkipMixin {

    //#if MC >= 12102
    //#if MC < 260102
    //$$ @Inject(method = "save", at = @At("HEAD"), cancellable = true)
    //$$ private void hfut$skipSavingRemovedFakePlayer(ServerPlayer player, CallbackInfo ci) {
    //$$     if (HFUTSettings.reloginAvatarLeakFix && player instanceof EntityPlayerMPFake && player.isRemoved()) {
    //$$         ci.cancel();
    //$$     }
    //$$ }
    //#endif
    //#endif
}
