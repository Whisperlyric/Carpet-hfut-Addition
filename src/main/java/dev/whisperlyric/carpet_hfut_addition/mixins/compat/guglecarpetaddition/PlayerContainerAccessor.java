package dev.whisperlyric.carpet_hfut_addition.mixins.compat.guglecarpetaddition;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Reads the interviewed fake player out of GCA's per-player container
 * (the {@code player} field lives in GCA's PlayerContainer superclass).
 */
@Restriction(require = @Condition("guglecarpetaddition"))
@Mixin(targets = "dev.dubhe.gugle.carpet.tools.player.PlayerContainer")
public interface PlayerContainerAccessor {
    @Accessor("player")
    ServerPlayer gca$player();
}
