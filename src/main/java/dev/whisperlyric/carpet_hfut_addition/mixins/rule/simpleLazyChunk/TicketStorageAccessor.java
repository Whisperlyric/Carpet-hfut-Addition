package dev.whisperlyric.carpet_hfut_addition.mixins.rule.simpleLazyChunk;

import net.minecraft.server.level.ServerChunkCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

//#if MC >= 12105
//$$ import net.minecraft.world.level.TicketStorage;
//#endif

/**
 * 1.21.5+ only: tickets moved into the per-level TicketStorage SavedData. On
 * 1.21.4- the guarded members preprocess away and this stays as a registered
 * but empty mixin.
 */
@Mixin(ServerChunkCache.class)
public interface TicketStorageAccessor {

    //#if MC >= 12105
    //$$ @Accessor("ticketStorage")
    //$$ TicketStorage hfut$ticketStorage();
    //#endif
}
