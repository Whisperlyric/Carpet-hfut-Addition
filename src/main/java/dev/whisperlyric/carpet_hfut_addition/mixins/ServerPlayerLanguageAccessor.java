package dev.whisperlyric.carpet_hfut_addition.mixins;

import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * The client language of a player is private in every supported version; needed
 * to resolve feedback text server-side.
 */
@Mixin(ServerPlayer.class)
public interface ServerPlayerLanguageAccessor {
    @Accessor("language")
    String hfut$language();
}
