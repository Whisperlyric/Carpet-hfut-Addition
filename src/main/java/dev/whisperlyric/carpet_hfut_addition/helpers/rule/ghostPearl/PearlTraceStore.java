package dev.whisperlyric.carpet_hfut_addition.helpers.rule.ghostPearl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dev.whisperlyric.carpet_hfut_addition.HFUTServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Forensics store for {@code ghostEnderPearlTrace}: a ring buffer of recent
 * teleport events plus per-UUID counts, persisted as a JSON sidecar in the
 * world folder. A pearl can only teleport once, so 2+ counts prove ghost replay.
 */
public final class PearlTraceStore {
    public enum Origin {
        NBT_LOAD, OTHER
    }

    public record TeleportEvent(UUID pearl, String ownerName, UUID ownerUuid, long time,
                                String dimension, double x, double y, double z,
                                Origin origin, String originSnapshot, int count) {
    }

    public record OriginSnapshot(String dimension, double x, double y, double z) {
    }

    private record CountEntry(int count, long lastSeen) {
    }

    private static final int MAX_EVENTS = 100;
    private static final long EVENT_TTL_MILLIS = 30L * 60 * 1000;
    private static final long COUNT_TTL_MILLIS = 7L * 24 * 60 * 60 * 1000;
    private static final Gson GSON = new Gson();

    private static final PearlTraceStore INSTANCE = new PearlTraceStore();

    public static PearlTraceStore get() {
        return INSTANCE;
    }

    private final ArrayDeque<TeleportEvent> events = new ArrayDeque<>();
    private final Map<UUID, OriginSnapshot> origins = new ConcurrentHashMap<>();
    private final Set<UUID> recycled = ConcurrentHashMap.newKeySet();
    private final Map<UUID, CountEntry> counts = new ConcurrentHashMap<>();
    private volatile Path filePath;

    // ------------------------------------------------------------------
    // lifecycle
    // ------------------------------------------------------------------
    public void attach(MinecraftServer server) {
        this.filePath = server.getWorldPath(LevelResource.ROOT).resolve("data").resolve("ghostpearl_trace.json");
        this.events.clear();
        this.origins.clear();
        this.recycled.clear();
        loadCounts();
        pruneCounts();
        saveCounts();
    }

    public void close() {
        saveCounts();
        this.events.clear();
        this.origins.clear();
        this.recycled.clear();
    }

    // ------------------------------------------------------------------
    // inputs (called from mixins)
    // ------------------------------------------------------------------
    public void markOrigin(Entity pearl) {
        if (this.origins.size() > 1024) {
            this.origins.clear(); // session cap; origins only matter briefly after load
        }
        this.origins.put(pearl.getUUID(), new OriginSnapshot(
                pearl.level().dimension().location().toString(),
                pearl.getX(), pearl.getY(), pearl.getZ()
        ));
    }

    public void markRecycled(UUID pearl) {
        this.recycled.add(pearl);
    }

    /** True (and clears the mark) when this pearl was recycled by our fix layer, so its discard is not a teleport. */
    public boolean consumeRecycled(UUID pearl) {
        return this.recycled.remove(pearl);
    }

    public synchronized void recordTeleport(Entity pearl, ServerPlayer owner) {
        UUID uuid = pearl.getUUID();
        OriginSnapshot snapshot = this.origins.remove(uuid);
        Origin origin = snapshot != null ? Origin.NBT_LOAD : Origin.OTHER;
        int count = increment(uuid);
        this.events.addLast(new TeleportEvent(
                uuid,
                owner != null ? ownerName(owner) : null,
                owner != null ? owner.getUUID() : null,
                System.currentTimeMillis(),
                pearl.level().dimension().location().toString(),
                pearl.getX(), pearl.getY(), pearl.getZ(),
                origin,
                snapshot != null ? String.format("%.1f %.1f %.1f @ %s", snapshot.x(), snapshot.y(), snapshot.z(), snapshot.dimension()) : null,
                count
        ));
        while (this.events.size() > MAX_EVENTS) {
            this.events.removeFirst();
        }
        pruneEvents();
        saveCounts();
    }

    // ------------------------------------------------------------------
    // queries (called from /pearltrace)
    // ------------------------------------------------------------------
    public synchronized List<TeleportEvent> events(String ownerNameFilter, UUID ownerUuidFilter) {
        List<TeleportEvent> result = new ArrayList<>();
        for (TeleportEvent event : this.events) {
            if (ownerNameFilter != null && !ownerNameFilter.equalsIgnoreCase(event.ownerName())) {
                continue;
            }
            if (ownerUuidFilter != null && !ownerUuidFilter.equals(event.ownerUuid())) {
                continue;
            }
            result.add(event);
        }
        return result;
    }

    public synchronized TeleportEvent latestFor(UUID pearl) {
        TeleportEvent found = null;
        for (TeleportEvent event : this.events) {
            if (event.pearl().equals(pearl)) {
                found = event;
            }
        }
        return found;
    }

    public int countOf(UUID pearl) {
        CountEntry entry = this.counts.get(pearl);
        return entry != null ? entry.count : 0;
    }

    /** Every UUID the store knows anything about (events + persisted counts), for prefix resolution. */
    public synchronized List<UUID> knownUuids() {
        List<UUID> known = new ArrayList<>(this.counts.keySet());
        for (TeleportEvent event : this.events) {
            if (!known.contains(event.pearl())) {
                known.add(event.pearl());
            }
        }
        return known;
    }

    /** Pearls with this UUID still sitting in online players' collections. Only exists on MC 1.21.2+ (pearl persistence). */
    //#if MC >= 12102
    //$$ public List<String> remainingCopies(MinecraftServer server, UUID pearl) {
    //$$     List<String> holders = new ArrayList<>();
    //$$     for (ServerPlayer player : server.getPlayerList().getPlayers()) {
    //$$         for (ThrownEnderpearl p : player.getEnderPearls()) {
    //$$             if (p.getUUID().equals(pearl)) {
    //$$                 holders.add(playerName(player));
    //$$             }
    //$$         }
    //$$     }
    //$$     return holders;
    //$$ }

    //$$ /** Removes matching pearls from the online player's collection; returns how many were removed. */
    //$$ public static int purge(ServerPlayer player, UUID pearl) {
    //$$     List<ThrownEnderpearl> matches = new ArrayList<>();
    //$$     for (ThrownEnderpearl p : player.getEnderPearls()) {
    //$$         if (p.getUUID().equals(pearl)) {
    //$$             matches.add(p);
    //$$         }
    //$$     }
    //$$     for (ThrownEnderpearl p : matches) {
    //$$         player.deregisterEnderPearl(p);
    //$$     }
    //$$     return matches.size();
    //$$ }
    //#endif

    private synchronized int increment(UUID pearl) {
        CountEntry entry = this.counts.get(pearl);
        int next = (entry != null ? entry.count : 0) + 1;
        this.counts.put(pearl, new CountEntry(next, System.currentTimeMillis()));
        return next;
    }

    // ------------------------------------------------------------------
    // persistence (JSON sidecar in the world dir)
    // ------------------------------------------------------------------
    private void loadCounts() {
        if (this.filePath == null || !Files.isRegularFile(this.filePath)) {
            return;
        }
        try {
            String json = Files.readString(this.filePath, StandardCharsets.UTF_8);
            Type type = new TypeToken<LinkedHashMap<String, CountEntry>>() {
            }.getType();
            Map<String, CountEntry> loaded = GSON.fromJson(json, type);
            this.counts.clear();
            if (loaded != null) {
                loaded.forEach((k, v) -> this.counts.put(UUID.fromString(k), v));
            }
        } catch (Exception e) {
            HFUTServer.LOGGER.warn("[HFUT] Failed to load ghostpearl_trace.json", e);
        }
    }

    private void saveCounts() {
        if (this.filePath == null) {
            return;
        }
        try {
            Files.createDirectories(this.filePath.getParent());
            Map<String, CountEntry> out = new LinkedHashMap<>();
            for (Map.Entry<UUID, CountEntry> e : this.counts.entrySet()) {
                out.put(e.getKey().toString(), e.getValue());
            }
            Files.writeString(this.filePath, GSON.toJson(out), StandardCharsets.UTF_8);
        } catch (Exception e) {
            HFUTServer.LOGGER.warn("[HFUT] Failed to save ghostpearl_trace.json", e);
        }
    }

    private void pruneCounts() {
        long cutoff = System.currentTimeMillis() - COUNT_TTL_MILLIS;
        this.counts.entrySet().removeIf(e -> e.getValue().lastSeen < cutoff);
    }

    private void pruneEvents() {
        long cutoff = System.currentTimeMillis() - EVENT_TTL_MILLIS;
        while (!this.events.isEmpty() && this.events.peekFirst().time() < cutoff) {
            this.events.removeFirst();
        }
    }

    private static String ownerName(ServerPlayer player) {
        //#if MC >= 12110
        //$$ return player.getGameProfile().name();
        //#else
        return player.getGameProfile().getName();
        //#endif
    }

    private static String playerName(ServerPlayer player) {
        return ownerName(player);
    }
}
