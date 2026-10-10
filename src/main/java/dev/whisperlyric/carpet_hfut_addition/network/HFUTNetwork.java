package dev.whisperlyric.carpet_hfut_addition.network;

import dev.whisperlyric.carpet_hfut_addition.HFUTSettings;
import dev.whisperlyric.carpet_hfut_addition.commands.PearlTraceCommand;
import dev.whisperlyric.carpet_hfut_addition.commands.SimpleLazyChunkCommand;
import dev.whisperlyric.carpet_hfut_addition.utils.CommandUtil;
import dev.whisperlyric.carpet_hfut_addition.utils.HFUTChatPage;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

/**
 * Registers the page-request payload and answers it on the server. The payload
 * type is registered on both sides so a client can encode the request; the
 * receiver runs on the server thread, so it may touch game state directly.
 * Page arrows never go through Brigadier, so each request re-checks the
 * command's own permission rule before running the listing.
 */
public final class HFUTNetwork {

    private HFUTNetwork() {
    }

    public static void init() {
        //#if MC >= 260100
        //$$ PayloadTypeRegistry.serverboundPlay().register(HFUTPagePayload.TYPE, HFUTPagePayload.CODEC);
        //#else
        PayloadTypeRegistry.playC2S().register(HFUTPagePayload.TYPE, HFUTPagePayload.CODEC);
        //#endif
        ServerPlayNetworking.registerGlobalReceiver(HFUTPagePayload.TYPE, (payload, context) -> {
            ServerPlayer player = context.player();
            // Remap would rewrite this to Entity.createCommandSourceStackForNameResolution(ServerLevel),
            // which does not typecheck; suppress it here. The directives are consumed by the remap
            // layer, so the preprocessor's "unknown directive" warnings are expected.
            //#disable-remap
            CommandSourceStack source = player.createCommandSourceStack();
            //#enable-remap
            switch (payload.channel()) {
                case HFUTChatPage.LAZYCHUNK -> {
                    if (CommandUtil.canUseCommand(source, HFUTSettings.commandSimpleLazyChunk)) {
                        SimpleLazyChunkCommand.query(source, payload.page());
                    }
                }
                case HFUTChatPage.PEARLTRACE -> {
                    if (CommandUtil.canUseCommand(source, HFUTSettings.commandPearlTrace)) {
                        PearlTraceCommand.list(source, payload.filter().isEmpty() ? null : payload.filter(),
                                payload.page());
                    }
                }
                default -> {
                }
            }
        });
    }
}
