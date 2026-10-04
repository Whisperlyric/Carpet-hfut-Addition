package dev.whisperlyric.carpet_hfut_addition.helpers.rule.ghostPearl;

import dev.whisperlyric.carpet_hfut_addition.HFUTServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;

import java.util.List;
import java.util.UUID;

/**
 * Bridge between the ghost pearl mixins and the rules: the load point serves
 * both the fix (recycling rejected pearls) and the trace (origin marking); the
 * discard and save points serve trace recording and the save predicate.
 */
public final class PearlTraceHandler {
    private PearlTraceHandler() {
    }

    /**
     * A pearl passed through {@code ServerLevel#addWithUUID} during NBT load.
     * When rejected, the fix layer deregisters and discards it; the trace marks
     * the origin either way.
     */
    //#if MC >= 12102
    //$$ public static void onLoadAttempt(ThrownEnderpearl pearl, boolean added, ServerLevel level) {
    //$$     if (GhostPearlGuard.traceActive()) {
    //$$         PearlTraceStore.get().markOrigin(pearl);
    //$$     }
    //$$     if (!added && GhostPearlGuard.fixActive()) {
    //$$         UUID pearlId = pearl.getUUID();
    //$$         PearlTraceStore.get().markRecycled(pearlId);
    //$$         ServerPlayer owner = ownerOf(pearl);
    //$$         if (owner != null) {
    //$$             owner.deregisterEnderPearl(pearl);
    //$$         }
    //$$         pearl.discard();
    //$$         HFUTServer.LOGGER.debug("[HFUT] Recycled ghost ender pearl {} rejected by the world", pearlId);
    //$$     }
    //$$ }
    //#endif

    /**
     * A pearl was discarded: for a thrown pearl this is the teleport moment.
     * Recycled pearls are suppressed and must not count as teleport events.
     */
    public static void onPearlDiscard(ThrownEnderpearl pearl) {
        if (!GhostPearlGuard.traceActive()) {
            return;
        }
        if (!PearlTraceStore.get().consumeRecycled(pearl.getUUID())) {
            ServerPlayer owner = ownerOf(pearl);
            PearlTraceStore.get().recordTeleport(pearl, owner);
        }
    }

    /**
     * Save predicate: a pearl that is not the live entity of its UUID is
     * dropped, so duplicate collections collapse onto the live instance.
     */
    //#if MC >= 12102
    //$$ public static void filterPearlsBeforeSave(ServerPlayer player) {
    //$$     if (!GhostPearlGuard.fixActive()) {
    //$$         return;
    //$$     }
    //$$     player.getEnderPearls().removeIf(pearl -> !isLive(pearl));
    //$$ }
    //#endif

    /**
     * Server stop: players left in the list by a lost disconnect race have
     * unmarked pearls that the coming chunk save would duplicate; marking them
     * UNLOADED_WITH_PLAYER makes the save skip them.
     */
    //#if MC >= 12102
    //$$ public static void markStragglersOnStop(PlayerList playerList) {
    //$$     if (!GhostPearlGuard.fixActive()) {
    //$$         return;
    //$$     }
    //$$     for (ServerPlayer player : List.copyOf(playerList.getPlayers())) {
    //$$         for (ThrownEnderpearl pearl : List.copyOf(player.getEnderPearls())) {
    //$$             pearl.setRemoved(Entity.RemovalReason.UNLOADED_WITH_PLAYER);
    //$$         }
    //$$     }
    //$$ }
    //#endif

    private static ServerPlayer ownerOf(ThrownEnderpearl pearl) {
        return pearl.getOwner() instanceof ServerPlayer player ? player : null;
    }

    private static boolean isLive(ThrownEnderpearl pearl) {
        ServerLevel level = (ServerLevel) pearl.level();
        // not a plain rename: on 1.21.10+ getEntity compiles via the EntityGetter
        // default but only searches the current dimension, while the deliberate
        // call is the cross-dimension lookup
        //#if MC >= 12110
        //$$ return level.getEntityInAnyDimension(pearl.getUUID()) == pearl;
        //#else
        return level.getEntity(pearl.getUUID()) == pearl;
        //#endif
    }
}
