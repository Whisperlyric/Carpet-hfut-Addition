package dev.whisperlyric.carpet_hfut_addition.helpers.rule.mapWorldIdentity;

import dev.whisperlyric.carpet_hfut_addition.HFUTServerMod;
import dev.whisperlyric.carpet_hfut_addition.HFUTSettings;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.LevelResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.zip.CRC32;

/**
 * separateMapModWorlds: pushes a stable world id to Xaero's maps
 * ({@code xaerominimap:main} / {@code xaeroworldmap:main}, marker 0 + CRC32 of
 * the id) and answers VoxelMap's {@code worldinfo:world_id} query
 * ({@code 0x00, 0x2A, length, UTF-8 id}). The id comes from the rule value;
 * {@code true} and {@code auto} use a UUID stored in the world root.
 */
public final class MapWorldIdentity {

    public static final ResourceLocation VOXELMAP_CHANNEL =
            ResourceLocation.fromNamespaceAndPath("worldinfo", "world_id");
    public static final ResourceLocation XAERO_MINIMAP_CHANNEL =
            ResourceLocation.fromNamespaceAndPath("xaerominimap", "main");
    public static final ResourceLocation XAERO_WORLDMAP_CHANNEL =
            ResourceLocation.fromNamespaceAndPath("xaeroworldmap", "main");

    private static final String AUTO = "auto";
    private static final String ID_FILE = HFUTServerMod.MOD_ID + "-map-world-id.txt";
    private static final byte VOXELMAP_MAGIC = 42;

    private static final CustomPacketPayload.Type<MapWorldPayload> VOXELMAP_TYPE = new CustomPacketPayload.Type<>(VOXELMAP_CHANNEL);
    private static final CustomPacketPayload.Type<MapWorldPayload> XAERO_MINIMAP_TYPE = new CustomPacketPayload.Type<>(XAERO_MINIMAP_CHANNEL);
    private static final CustomPacketPayload.Type<MapWorldPayload> XAERO_WORLDMAP_TYPE = new CustomPacketPayload.Type<>(XAERO_WORLDMAP_CHANNEL);

    /** The auto id for this server session; nulled on server close. */
    private static volatile String autoIdCache;

    private MapWorldIdentity() {
    }

    /** Raw passthrough payload; carries its pre-built type to avoid per-send allocation. */
    public record MapWorldPayload(ResourceLocation channel, CustomPacketPayload.Type<MapWorldPayload> payloadType, byte[] data) implements CustomPacketPayload {
        @Override
        public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
            return this.payloadType;
        }
    }

    private static CustomPacketPayload.Type<MapWorldPayload> type(ResourceLocation channel) {
        if (VOXELMAP_CHANNEL.equals(channel)) {
            return VOXELMAP_TYPE;
        }
        if (XAERO_MINIMAP_CHANNEL.equals(channel)) {
            return XAERO_MINIMAP_TYPE;
        }
        return XAERO_WORLDMAP_TYPE;
    }

    private static StreamCodec<RegistryFriendlyByteBuf, MapWorldPayload> rawCodec(ResourceLocation channel) {
        return StreamCodec.of(
                (buf, payload) -> buf.writeBytes(payload.data()),
                buf -> {
                    byte[] bytes = new byte[buf.readableBytes()];
                    buf.readBytes(bytes);
                    return new MapWorldPayload(channel, type(channel), bytes);
                });
    }

    /** Registers the payload types and the VoxelMap query receiver. */
    public static void registerNetworking() {
        //#if MC >= 260100
        //$$ PayloadTypeRegistry.clientboundPlay().register(VOXELMAP_TYPE, rawCodec(VOXELMAP_CHANNEL));
        //$$ PayloadTypeRegistry.clientboundPlay().register(XAERO_MINIMAP_TYPE, rawCodec(XAERO_MINIMAP_CHANNEL));
        //$$ PayloadTypeRegistry.clientboundPlay().register(XAERO_WORLDMAP_TYPE, rawCodec(XAERO_WORLDMAP_CHANNEL));
        //$$ PayloadTypeRegistry.serverboundPlay().register(VOXELMAP_TYPE, rawCodec(VOXELMAP_CHANNEL));
        //#else
        PayloadTypeRegistry.playS2C().register(VOXELMAP_TYPE, rawCodec(VOXELMAP_CHANNEL));
        PayloadTypeRegistry.playS2C().register(XAERO_MINIMAP_TYPE, rawCodec(XAERO_MINIMAP_CHANNEL));
        PayloadTypeRegistry.playS2C().register(XAERO_WORLDMAP_TYPE, rawCodec(XAERO_WORLDMAP_CHANNEL));
        PayloadTypeRegistry.playC2S().register(VOXELMAP_TYPE, rawCodec(VOXELMAP_CHANNEL));
        //#endif
        ServerPlayNetworking.registerGlobalReceiver(VOXELMAP_TYPE, (payload, context) ->
                sendVoxelMapResponse(context.player()));
    }

    /** Pushes the world id to Xaero's on both channels; called from sendLevelInfo. */
    public static void sendXaeroWorldInfo(ServerPlayer player) {
        String worldId = resolveWorldId(player.level().getServer());
        if (worldId == null) {
            return;
        }
        byte[] payload = formatXaeroPayload(worldId);
        sendIfAccepted(player, XAERO_MINIMAP_TYPE, payload);
        sendIfAccepted(player, XAERO_WORLDMAP_TYPE, payload);
    }

    private static void sendVoxelMapResponse(ServerPlayer player) {
        String worldId = resolveWorldId(player.level().getServer());
        if (worldId == null) {
            return;
        }
        // the query implies the client listens on the channel; answer unconditionally
        ServerPlayNetworking.send(player, new MapWorldPayload(VOXELMAP_CHANNEL, VOXELMAP_TYPE, formatVoxelMapResponse(worldId)));
    }

    /**
     * Skips clients that did not register the channel (no map mod installed) -
     * vanilla drops such packets silently but fabric logs the rejection.
     */
    private static void sendIfAccepted(ServerPlayer player, CustomPacketPayload.Type<MapWorldPayload> type, byte[] payload) {
        if (ServerPlayNetworking.canSend(player, type)) {
            ServerPlayNetworking.send(player, new MapWorldPayload(type.id(), type, payload));
        }
    }

    /** Drops the cached auto id; called on server close so a new session re-reads the file. */
    public static void clear() {
        autoIdCache = null;
    }

    /** Marker 0, magic 42, UTF-8 length, id. */
    private static byte[] formatVoxelMapResponse(String worldId) {
        byte[] idBytes = worldId.getBytes(StandardCharsets.UTF_8);
        byte[] response = new byte[3 + idBytes.length];
        response[0] = 0;
        response[1] = VOXELMAP_MAGIC;
        response[2] = (byte) idBytes.length;
        System.arraycopy(idBytes, 0, response, 3, idBytes.length);
        return response;
    }

    /** Marker 0, then the CRC32 of the id, big-endian. */
    private static byte[] formatXaeroPayload(String worldId) {
        CRC32 crc32 = new CRC32();
        byte[] idBytes = worldId.getBytes(StandardCharsets.UTF_8);
        crc32.update(idBytes, 0, idBytes.length);
        int crc = (int) crc32.getValue();
        return new byte[]{
                0,
                (byte) (crc >>> 24),
                (byte) (crc >>> 16),
                (byte) (crc >>> 8),
                (byte) crc
        };
    }

    /** Rule value to id: disabled/null, {@code true}/{@code auto} file, or the custom string. */
    private static String resolveWorldId(MinecraftServer server) {
        String rule = HFUTSettings.separateMapModWorlds;
        if (rule == null || rule.isBlank() || "false".equalsIgnoreCase(rule.trim())) {
            return null;
        }
        String value = rule.trim();
        String worldId = AUTO.equalsIgnoreCase(value) || "true".equalsIgnoreCase(value)
                ? getOrCreateAutoWorldId(server)
                : value;
        return isValidWorldId(worldId) ? worldId : null;
    }

    private static boolean isValidWorldId(String worldId) {
        return worldId != null && !worldId.isEmpty() && worldId.getBytes(StandardCharsets.UTF_8).length <= 255;
    }

    /** Reads the id file once per server session, generating and storing a UUID on first use. */
    private static String getOrCreateAutoWorldId(MinecraftServer server) {
        String cached = autoIdCache;
        if (cached != null) {
            return cached;
        }
        Path path = server.getWorldPath(LevelResource.ROOT).resolve(ID_FILE);
        if (Files.isRegularFile(path)) {
            try {
                String id = Files.readString(path, StandardCharsets.UTF_8).trim();
                if (!id.isEmpty()) {
                    autoIdCache = id;
                    return id;
                }
            } catch (IOException ignored) {
                // fall through to generation
            }
        }
        String generated = UUID.randomUUID().toString();
        try {
            Files.writeString(path, generated + System.lineSeparator(), StandardCharsets.UTF_8);
            autoIdCache = generated;
            return generated;
        } catch (IOException e) {
            return null;
        }
    }
}
