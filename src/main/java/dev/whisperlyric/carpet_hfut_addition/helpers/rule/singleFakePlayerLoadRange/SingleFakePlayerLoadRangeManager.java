package dev.whisperlyric.carpet_hfut_addition.helpers.rule.singleFakePlayerLoadRange;

import dev.whisperlyric.carpet_hfut_addition.mixins.rule.simpleLazyChunk.TicketStorageAccessor;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ChunkLevel;
import net.minecraft.server.level.FullChunkStatus;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.level.ChunkPos;

//#if MC < 12105
import dev.whisperlyric.carpet_hfut_addition.mixins.rule.simpleLazyChunk.DistanceManagerAccessor;
import net.minecraft.server.level.DistanceManager;
//#else
//$$ import net.minecraft.server.level.Ticket;
//$$ import net.minecraft.world.level.TicketStorage;
//#endif

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Backing store for {@code /player <player> loadRange <n>}: replaces one fake
 * player's chunk loading with a single ticket of our own.
 * <p>
 * Chunk level {@code 33} is the outermost fully-loaded level, and a ticket at
 * level {@code L} gives the chunk {@code d} chunks away level {@code L + d}, so
 * one ticket at {@code 33 - n} drives all three rings at once, each one chunk
 * thick: fully loaded out to {@code n}, block ticking out to {@code n - 1} and
 * entity ticking out to {@code n - 2}. With {@code n >= 2} the fake's own chunk
 * stays entity-ticking, which is why the range is capped there.
 * <p>
 * The vanilla ticket is dropped by answering {@code true} from
 * {@code ChunkMap#skipPlayer} while a range is set; vanilla adds its own ticket
 * back by itself on the next {@code ChunkMap#move} after a reset. A range lasts
 * only as long as its fake player does: a fake player removed for any reason
 * takes its setting and ticket with it.
 */
public final class SingleFakePlayerLoadRangeManager {
    public static final int MIN_RADIUS = 2;
    public static final int MAX_RADIUS = 33;

    /** The level at which a chunk counts as fully loaded. */
    private static final int FULL_LEVEL = ChunkLevel.byStatus(FullChunkStatus.FULL);

    //#if MC < 12105
    private static final TicketType<UUID> HFUT$TICKET_TYPE = TicketType.create("hfut_single_fake_player_load_range", UUID::compareTo);
    //#elseif MC < 12110
    //$$ private static final TicketType HFUT$TICKET_TYPE = new TicketType(TicketType.NO_TIMEOUT, false, TicketType.TicketUse.LOADING_AND_SIMULATION);
    //#else
    //$$ private static final TicketType HFUT$TICKET_TYPE = new TicketType(TicketType.NO_TIMEOUT, TicketType.FLAG_LOADING | TicketType.FLAG_SIMULATION);
    //#endif

    /** Requested radius per fake player. */
    private static final Map<UUID, Integer> RANGES = new HashMap<>();
    /** The ticket currently held in the world per fake player. */
    private static final Map<UUID, Applied> APPLIED = new HashMap<>();
    private static MinecraftServer server;

    private record Applied(ServerLevel level, ChunkPos pos, int ticketLevel) {
    }

    private SingleFakePlayerLoadRangeManager() {
    }

    public static void attach(MinecraftServer minecraftServer) {
        server = minecraftServer;
    }

    public static void clear() {
        server = null;
        RANGES.clear();
        APPLIED.clear();
    }

    public static void setRange(ServerPlayer player, int radius) {
        RANGES.put(player.getUUID(), radius);
        maintain(player);
    }

    /** Drops the range; vanilla's own ticket comes back on the fake's next move. */
    public static void reset(ServerPlayer player) {
        drop(player.getUUID());
    }

    /** A fake player removed for any reason takes its setting and ticket with it. */
    public static void onPlayerRemoved(ServerPlayer player) {
        drop(player.getUUID());
    }

    /**
     * Keeps this player's ticket in sync with the requested range and reports
     * whether the player should be skipped by vanilla chunk loading. Called from
     * {@code ChunkMap#skipPlayer} on every move, so it both applies changes and
     * repairs tickets lost to chunk unloads; re-adding an existing ticket is a
     * no-op.
     */
    public static boolean maintain(ServerPlayer player) {
        UUID id = player.getUUID();
        Integer radius = RANGES.get(id);
        if (radius == null || server == null || !(player.level() instanceof ServerLevel level)
                || level.getServer() != server) {
            return false;
        }
        int ticketLevel = FULL_LEVEL - radius;
        ChunkPos pos = new ChunkPos(player.blockPosition().getX() >> 4, player.blockPosition().getZ() >> 4);
        Applied applied = APPLIED.get(id);
        if (applied != null && applied.level() == level && applied.ticketLevel() == ticketLevel
                && pack(applied.pos()) == pack(pos)) {
            return true;
        }
        if (applied != null) {
            removeTicket(applied.level(), applied.pos(), applied.ticketLevel(), id);
        }
        addTicket(level, pos, ticketLevel, id);
        APPLIED.put(id, new Applied(level, pos, ticketLevel));
        return true;
    }

    private static void drop(UUID id) {
        RANGES.remove(id);
        Applied applied = APPLIED.remove(id);
        if (applied != null) {
            removeTicket(applied.level(), applied.pos(), applied.ticketLevel(), id);
        }
    }

    /**
     * One pack point for every ChunkPos we handle: the accessor renamed
     * {@code toLong} to {@code pack} in 26.1.
     */
    private static long pack(ChunkPos pos) {
        //#if MC >= 260100
        //$$ return pos.pack();
        //#else
        return pos.toLong();
        //#endif
    }

    private static void addTicket(ServerLevel level, ChunkPos pos, int ticketLevel, UUID value) {
        //#if MC < 12105
        DistanceManager distanceManager = ((DistanceManagerAccessor) level.getChunkSource()).hfut$distanceManager();
        distanceManager.addTicket(HFUT$TICKET_TYPE, pos, ticketLevel, value);
        //#else
        //$$ TicketStorage storage = ((TicketStorageAccessor) level.getChunkSource()).hfut$ticketStorage();
        //$$ storage.addTicket(new Ticket(HFUT$TICKET_TYPE, ticketLevel), pos);
        //#endif
    }

    private static void removeTicket(ServerLevel level, ChunkPos pos, int ticketLevel, UUID value) {
        //#if MC < 12105
        DistanceManager distanceManager = ((DistanceManagerAccessor) level.getChunkSource()).hfut$distanceManager();
        distanceManager.removeTicket(HFUT$TICKET_TYPE, pos, ticketLevel, value);
        //#else
        //$$ TicketStorage storage = ((TicketStorageAccessor) level.getChunkSource()).hfut$ticketStorage();
        //$$ storage.removeTicket(new Ticket(HFUT$TICKET_TYPE, ticketLevel), pos);
        //#endif
    }
}
