package dev.whisperlyric.carpet_hfut_addition.mixins.compat.guglecarpetaddition;

import dev.dubhe.gugle.carpet.api.menu.control.Button;
import dev.dubhe.gugle.carpet.api.menu.control.ButtonBuilder;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * Exposes GCA's public button registration to our string-target mixin on
 * {@code PlayerEnderChestContainer} (which cannot reference the superclass
 * method directly).
 */
@Restriction(require = @Condition("guglecarpetaddition"))
@Mixin(targets = "dev.dubhe.gugle.carpet.api.menu.CustomMenu")
public interface CustomMenuInvoker {
    @Invoker("addButton")
    Button gca$addButton(int slot, ButtonBuilder builder);
}
