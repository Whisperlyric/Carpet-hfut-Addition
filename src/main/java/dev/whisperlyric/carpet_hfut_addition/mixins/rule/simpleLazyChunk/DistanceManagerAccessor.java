package dev.whisperlyric.carpet_hfut_addition.mixins.rule.simpleLazyChunk;

import net.minecraft.server.level.ServerChunkCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

//#if MC < 12105
import net.minecraft.server.level.DistanceManager;
//#endif

/**
 * 1.21.4- only: tickets lived in the per-level DistanceManager before
 * TicketStorage took over. On 1.21.5+ the guarded members preprocess away and
 * this stays as a registered but empty mixin.
 */
@Mixin(ServerChunkCache.class)
public interface DistanceManagerAccessor {

    //#if MC < 12105
    @Accessor("distanceManager")
    DistanceManager hfut$distanceManager();
    //#endif
}
