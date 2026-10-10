package dev.whisperlyric.carpet_hfut_addition.helpers.rule.simpleLazyChunk;

import dev.whisperlyric.carpet_hfut_addition.mixins.rule.simpleLazyChunk.TicketStorageAccessor;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ChunkLevel;
import net.minecraft.server.level.FullChunkStatus;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

//#if MC < 12105
import dev.whisperlyric.carpet_hfut_addition.mixins.rule.simpleLazyChunk.DistanceManagerAccessor;
import net.minecraft.server.level.DistanceManager;
import net.minecraft.util.Unit;
//#else
//$$ import net.minecraft.server.level.Ticket;
//$$ import net.minecraft.world.level.TicketStorage;
//#endif

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Backing store for {@code /hfut lazychunk}: marks chunks "simple lazy"
 * ({@link FullChunkStatus#BLOCK_TICKING}, blocks tick but entities do not).
 * An anchor ticket keeps unattended chunks loaded and {@link #clampLevel}
 * (from the ChunkHolder mixin) holds back promotion past that level, pinning
 * a mark there until it is unmarked. Marks are in-memory per dimension, lost
 * on restart; the ticket type differs by version (1.21.5 changed its model),
 * but all variants load and simulate without persisting.
 */
public final class SimpleLazyChunkManager {
    /** Range commands, like vanilla /forceload, refuse to grow beyond this many chunks. */
    public static final int MAX_RANGE_CHUNKS = 256;

    /**
     * Chunk-coordinate equivalent of the world border (vanilla caps block
     * positions at +/-29,999,984, which is exactly +/-1,874,999 chunks);
     * anything beyond can never be visited, let alone loaded.
     */
    public static final int MAX_CHUNK_COORD = 1_874_999;

    public static boolean inWorldBound(int chunkX, int chunkZ) {
        return chunkX >= -MAX_CHUNK_COORD && chunkX <= MAX_CHUNK_COORD
                && chunkZ >= -MAX_CHUNK_COORD && chunkZ <= MAX_CHUNK_COORD;
    }

    /** The level our anchor ticket carries and marked chunks are clamped to. */
    public static final int LAZY_LEVEL = ChunkLevel.byStatus(FullChunkStatus.BLOCK_TICKING);

    //#if MC < 12105
    private static final TicketType<Unit> HFUT$TICKET_TYPE = TicketType.create("hfut_simple_lazy_chunk", (a, b) -> 0);
    //#elseif MC < 12110
    //$$ private static final TicketType HFUT$TICKET_TYPE = new TicketType(TicketType.NO_TIMEOUT, false, TicketType.TicketUse.LOADING_AND_SIMULATION);
    //#else
    //$$ private static final TicketType HFUT$TICKET_TYPE = new TicketType(TicketType.NO_TIMEOUT, TicketType.FLAG_LOADING | TicketType.FLAG_SIMULATION);
    //#endif

    private static final Map<ResourceKey<Level>, LongSet> MARKS = new HashMap<>();
    private static final LongSet NO_MARKS = new LongOpenHashSet();
    private static MinecraftServer server;

    private SimpleLazyChunkManager() {
    }

    public static void attach(MinecraftServer minecraftServer) {
        server = minecraftServer;
    }

    public static void clear() {
        server = null;
        MARKS.clear();
    }

    /** Level assignment hook from the ChunkHolder mixin; runs on every ticket-level change. */
    public static int clampLevel(ServerLevel level, long packedPos, int newLevel) {
        if (newLevel < LAZY_LEVEL && level.getServer() == server
                && MARKS.getOrDefault(level.dimension(), NO_MARKS).contains(packedPos)
                // a player standing in the chunk needs it entity-ticking - clamping
                // here would stop their entity tick and freeze them
                && !isPlayerInChunk(level, packedPos)) {
            return LAZY_LEVEL;
        }
        return newLevel;
    }

    /** Online players whose chunk falls inside the rectangle (inclusive). */
    public static List<ServerPlayer> playersIn(ServerLevel level, int minX, int minZ, int maxX, int maxZ) {
        List<ServerPlayer> found = new ArrayList<>();
        for (ServerPlayer player : level.players()) {
            int chunkX = player.blockPosition().getX() >> 4;
            int chunkZ = player.blockPosition().getZ() >> 4;
            if (chunkX >= minX && chunkX <= maxX && chunkZ >= minZ && chunkZ <= maxZ) {
                found.add(player);
            }
        }
        return found;
    }

    private static boolean isPlayerInChunk(ServerLevel level, long packedPos) {
        int chunkX = x(packedPos);
        int chunkZ = z(packedPos);
        for (ServerPlayer player : level.players()) {
            if ((player.blockPosition().getX() >> 4) == chunkX
                    && (player.blockPosition().getZ() >> 4) == chunkZ) {
                return true;
            }
        }
        return false;
    }

    /** Unmarks every chunk of this dimension; returns how many marks were removed. */
    public static int clearDimension(ServerLevel level) {
        if (server != level.getServer()) {
            return 0;
        }
        LongSet marks = MARKS.remove(level.dimension());
        if (marks == null) {
            return 0;
        }
        for (long packed : marks) {
            removeTicket(level, new ChunkPos(x(packed), z(packed)));
        }
        return marks.size();
    }

    /** Marks one chunk; false if it was already marked. */
    public static boolean add(ServerLevel level, ChunkPos pos) {
        LongSet marks = editableMarks(level);
        if (marks == null || !marks.add(pack(pos))) {
            return false;
        }
        addTicket(level, pos);
        return true;
    }

    /** Unmarks one chunk; false if it was not marked. */
    public static boolean remove(ServerLevel level, ChunkPos pos) {
        if (server != level.getServer()) {
            return false;
        }
        LongSet marks = MARKS.get(level.dimension());
        if (marks == null || !marks.remove(pack(pos))) {
            return false;
        }
        removeTicket(level, pos);
        return true;
    }

    /** Returns newly marked count, or -1 if the rectangle exceeds {@link #MAX_RANGE_CHUNKS}. */
    public static int addRange(ServerLevel level, ChunkPos from, ChunkPos to) {
        return range(level, from, to, true);
    }

    /** Returns newly unmarked count, or -1 if the rectangle exceeds {@link #MAX_RANGE_CHUNKS}. */
    public static int removeRange(ServerLevel level, ChunkPos from, ChunkPos to) {
        return range(level, from, to, false);
    }

    private static int range(ServerLevel level, ChunkPos from, ChunkPos to, boolean adding) {
        // ChunkPos.x/z are fields up to 1.21.11 and private accessors on 26.1+; go through packed longs
        long packedFrom = pack(from);
        long packedTo = pack(to);
        // width/height in long: the int subtraction would wrap for extreme inputs
        // and a negative "area" used to slip past the cap
        long width = (long) Math.max(x(packedFrom), x(packedTo)) - Math.min(x(packedFrom), x(packedTo)) + 1;
        long height = (long) Math.max(z(packedFrom), z(packedTo)) - Math.min(z(packedFrom), z(packedTo)) + 1;
        if (width * height > MAX_RANGE_CHUNKS) {
            return -1;
        }
        int minX = Math.min(x(packedFrom), x(packedTo));
        int maxX = Math.max(x(packedFrom), x(packedTo));
        int minZ = Math.min(z(packedFrom), z(packedTo));
        int maxZ = Math.max(z(packedFrom), z(packedTo));
        int changed = 0;
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                if (adding ? add(level, new ChunkPos(x, z)) : remove(level, new ChunkPos(x, z))) {
                    changed++;
                }
            }
        }
        return changed;
    }

    /** Sorted snapshot of marked chunks in this dimension, packed as {@code (z << 32) | x}. */
    public static List<Long> marksIn(ServerLevel level) {
        List<Long> marks = new ArrayList<>(MARKS.getOrDefault(level.dimension(), NO_MARKS));
        marks.sort(Long::compare);
        return marks;
    }

    public static int count(ServerLevel level) {
        return MARKS.getOrDefault(level.dimension(), NO_MARKS).size();
    }

    public static int x(long packed) {
        return (int) packed;
    }

    public static int z(long packed) {
        return (int) (packed >> 32);
    }

    /**
     * One pack point for every ChunkPos we handle: the accessor renamed
     * {@code toLong} to {@code pack} in 26.1.
     */
    public static long pack(ChunkPos pos) {
        //#if MC >= 260100
        //$$ return pos.pack();
        //#else
        return pos.toLong();
        //#endif
    }

    private static LongSet editableMarks(ServerLevel level) {
        if (server != level.getServer()) {
            return null;
        }
        return MARKS.computeIfAbsent(level.dimension(), key -> new LongOpenHashSet());
    }

    private static void addTicket(ServerLevel level, ChunkPos pos) {
        //#if MC < 12105
        DistanceManager distanceManager = ((DistanceManagerAccessor) level.getChunkSource()).hfut$distanceManager();
        distanceManager.addTicket(HFUT$TICKET_TYPE, pos, LAZY_LEVEL, Unit.INSTANCE);
        //#else
        //$$ TicketStorage storage = ((TicketStorageAccessor) level.getChunkSource()).hfut$ticketStorage();
        //$$ storage.addTicket(new Ticket(HFUT$TICKET_TYPE, LAZY_LEVEL), pos);
        //#endif
    }

    private static void removeTicket(ServerLevel level, ChunkPos pos) {
        //#if MC < 12105
        DistanceManager distanceManager = ((DistanceManagerAccessor) level.getChunkSource()).hfut$distanceManager();
        distanceManager.removeTicket(HFUT$TICKET_TYPE, pos, LAZY_LEVEL, Unit.INSTANCE);
        //#else
        //$$ TicketStorage storage = ((TicketStorageAccessor) level.getChunkSource()).hfut$ticketStorage();
        //$$ storage.removeTicket(new Ticket(HFUT$TICKET_TYPE, LAZY_LEVEL), pos);
        //#endif
    }
}
