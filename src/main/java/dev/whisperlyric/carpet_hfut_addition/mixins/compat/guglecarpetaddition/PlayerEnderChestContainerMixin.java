package dev.whisperlyric.carpet_hfut_addition.mixins.compat.guglecarpetaddition;

import carpet.patches.EntityPlayerMPFake;
import dev.whisperlyric.carpet_hfut_addition.helpers.compat.guglecarpetaddition.GcaInvertButton;
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
 */
@Restriction(require = @Condition("guglecarpetaddition"))
@Mixin(targets = "dev.dubhe.gugle.carpet.tools.player.PlayerEnderChestContainer")
public abstract class PlayerEnderChestContainerMixin {
    @Inject(method = "refreshButtons", at = @At("RETURN"))
    private void hfut$addInvertButton(CallbackInfo ci) {
        Container self = (Container) this;
        ServerPlayer interviewed = ((PlayerContainerAccessor) this).gca$player();
        if (interviewed instanceof EntityPlayerMPFake fake) {
            ((CustomMenuInvoker) this).gca$addButton(GcaInvertButton.SLOT_INDEX, GcaInvertButton.builder(fake, self));
        }
    }
}
