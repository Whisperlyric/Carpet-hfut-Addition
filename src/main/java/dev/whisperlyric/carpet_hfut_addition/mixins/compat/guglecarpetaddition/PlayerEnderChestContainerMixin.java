package dev.whisperlyric.carpet_hfut_addition.mixins.compat.guglecarpetaddition;

import carpet.patches.EntityPlayerMPFake;
import dev.whisperlyric.carpet_hfut_addition.helpers.compat.guglecarpetaddition.GcaInvertButton;
import dev.whisperlyric.carpet_hfut_addition.helpers.rule.fakePlayerOpen.FakePlayerStorageEditor;
import dev.whisperlyric.carpet_hfut_addition.utils.GcaMenuApiTester;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Adds our invert-tick-stage button into GCA's fake player menu, one slot left
 * of its "quit" button (slot 26 -> ours at 25), replacing whatever filler
 * occupies that slot.
 *
 * <p>GCA fills slots 0..26 itself in {@code refreshButtons} (a loop over 2..25
 * plus a few explicit slots) and its {@code tickButtons} calls each button's
 * {@code refresh()} in list order, so the <em>last</em> button registered for a
 * slot is the one actually shown. Our button is therefore added at {@code TAIL}
 * (after GCA's own buttons) so it wins slot 25; adding it at {@code HEAD} would
 * get overwritten by GCA's filler for that slot.
 */
@Restriction(require = {
        @Condition("gca"),
        @Condition(type = Condition.Type.TESTER, tester = GcaMenuApiTester.class)
})
@Mixin(targets = "dev.dubhe.gugle.carpet.tools.player.PlayerEnderChestContainer")
public abstract class PlayerEnderChestContainerMixin {
    @Inject(method = "refreshButtons", at = @At("HEAD"), cancellable = true)
    private void hfut$suppressForShadowPlayer(CallbackInfo ci) {
        // offline editor: no GCA control buttons (quit etc.) for a player that is not in the world
        if (FakePlayerStorageEditor.isShadowPlayer(((PlayerContainerAccessor) this).gca$player())) {
            ci.cancel();
        }
    }

    @Inject(method = "refreshButtons", at = @At("TAIL"))
    private void hfut$addInvertButton(CallbackInfo ci) {
        ServerPlayer interviewed = ((PlayerContainerAccessor) this).gca$player();
        if (interviewed instanceof EntityPlayerMPFake fake) {
            Container self = (Container) this;
            ((CustomMenuInvoker) this).gca$addButton(GcaInvertButton.SLOT_INDEX, GcaInvertButton.builder(fake, self));
        }
    }
}
