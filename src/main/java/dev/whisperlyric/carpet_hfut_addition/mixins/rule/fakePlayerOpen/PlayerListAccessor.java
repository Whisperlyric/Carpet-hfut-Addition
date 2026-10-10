package dev.whisperlyric.carpet_hfut_addition.mixins.rule.fakePlayerOpen;

import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.storage.PlayerDataStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Exposes the per-server playerdata storage (same private field name on every
 * supported version) so the offline editor can save shadow players.
 */
@Mixin(PlayerList.class)
public interface PlayerListAccessor {
    @Accessor("playerIo")
    PlayerDataStorage hfut$playerIo();
}
