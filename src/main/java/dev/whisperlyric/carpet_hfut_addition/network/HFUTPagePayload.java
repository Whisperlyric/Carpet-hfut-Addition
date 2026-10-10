package dev.whisperlyric.carpet_hfut_addition.network;

import dev.whisperlyric.carpet_hfut_addition.HFUTServerMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * Client -> server request for one page of a paged listing, sent when a page
 * arrow is clicked. The page number is useless as a typed command argument, so
 * the arrow asks over this channel instead; the server re-checks the command
 * permission before answering, and only a client with our mod can send it.
 *
 * <p>{@code channel} is one of the {@link dev.whisperlyric.carpet_hfut_addition.utils.HFUTChatPage}
 * channels; {@code filter} is the {@code /pearltrace list} owner filter, or empty.
 */
public record HFUTPagePayload(String channel, int page, String filter) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<HFUTPagePayload> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(HFUTServerMod.MOD_ID, "page"));

    public static final StreamCodec<RegistryFriendlyByteBuf, HFUTPagePayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, HFUTPagePayload::channel,
            ByteBufCodecs.VAR_INT, HFUTPagePayload::page,
            ByteBufCodecs.STRING_UTF8, HFUTPagePayload::filter,
            HFUTPagePayload::new);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
