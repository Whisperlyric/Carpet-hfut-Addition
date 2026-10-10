package dev.whisperlyric.carpet_hfut_addition.mixins.rule.simpleLazyChunk;

import dev.whisperlyric.carpet_hfut_addition.helpers.rule.simpleLazyChunk.SimpleLazyChunkManager;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelHeightAccessor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * simpleLazyChunk clamp: every ticket-level assignment flows through
 * {@code setTicketLevel}, so marked chunks are held at BLOCK_TICKING here
 * instead of letting players, /forceload or portals promote them to entity
 * ticking. The holder's constructor takes the level directly, but creation is
 * only reachable with the anchor ticket already applied, which pins the level
 * at BLOCK_TICKING on its own.
 */
@Mixin(ChunkHolder.class)
public abstract class ChunkHolderMixin {

    @Shadow
    @Final
    private LevelHeightAccessor levelHeightAccessor;

    @ModifyVariable(method = "setTicketLevel", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private int hfut$clampToLazy(int newLevel) {
        return SimpleLazyChunkManager.clampLevel((ServerLevel) this.levelHeightAccessor,
                SimpleLazyChunkManager.pack(((ChunkHolder) (Object) this).getPos()), newLevel);
    }
}
