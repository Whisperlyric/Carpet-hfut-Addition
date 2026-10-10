package dev.whisperlyric.carpet_hfut_addition.client;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.whisperlyric.carpet_hfut_addition.network.HFUTPagePayload;
import dev.whisperlyric.carpet_hfut_addition.utils.HFUTChatPage;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
//#if MC >= 260100
//$$ import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
//#else
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
//#endif
//#if MC >= 260200
//$$ import com.mojang.brigadier.arguments.BoolArgumentType;
//$$ import net.minecraft.network.chat.Component;
//#endif

/**
 * Client-only commands under the {@code /hfutclient} root. Some never reach the
 * server (they flip a {@link ClientToggles} field and report back, so no
 * permission rule applies); the page arrows ask the server for one page of a
 * listing over our own channel, because a page number has no use as a typed
 * command argument - only a client with this mod can send that request, and the
 * server re-checks the command's permission before answering.
 */
public final class HFUTClientCommands {

    private HFUTClientCommands() {
    }

    public static void init() {
        var root = literal("hfutclient");
        root.then(literal("lazychunk")
                .then(argument("page", IntegerArgumentType.integer(1))
                        .executes(ctx -> requestPage(HFUTChatPage.LAZYCHUNK, ctx, null))));
        root.then(literal("pearl")
                .then(argument("page", IntegerArgumentType.integer(1))
                        .executes(ctx -> requestPage(HFUTChatPage.PEARLTRACE, ctx, null))
                        .then(argument("player", StringArgumentType.word())
                                .executes(ctx -> requestPage(HFUTChatPage.PEARLTRACE, ctx,
                                        StringArgumentType.getString(ctx, "player"))))));
        //#if MC >= 260200
        //$$ root.then(literal("hidepausesocial")
        //$$         .executes(ctx -> togglePauseSocial(ctx, !ClientToggles.hidePauseSocialRow))
        //$$         .then(argument("state", BoolArgumentType.bool())
        //$$                 .executes(ctx -> togglePauseSocial(ctx, BoolArgumentType.getBool(ctx, "state")))));
        //#endif
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(root));
    }

    /** Asks the server to print one page of a listing; the reply arrives as normal chat. */
    private static int requestPage(String channel, CommandContext<FabricClientCommandSource> ctx, String filter) {
        ClientPlayNetworking.send(new HFUTPagePayload(channel,
                IntegerArgumentType.getInteger(ctx, "page"), filter == null ? "" : filter));
        return 1;
    }

    //#if MC >= 260200
    //$$ private static int togglePauseSocial(CommandContext<FabricClientCommandSource> ctx, boolean hidden) {
    //$$     ClientToggles.hidePauseSocialRow = hidden;
    //$$     ClientToggles.save();
    //$$     ClientToggles.syncModMenuGameMenuStyle(hidden);
    //$$     ctx.getSource().sendFeedback(Component.translatable(
    //$$             "hfut.client.hidePauseSocial." + (hidden ? "hidden" : "shown")));
    //$$     return 1;
    //$$ }
    //#endif

    /** 26.x renamed {@code ClientCommandManager} to {@code ClientCommands}; both build the same node. */
    private static LiteralArgumentBuilder<FabricClientCommandSource> literal(String name) {
        //#if MC >= 260100
        //$$ return ClientCommands.literal(name);
        //#else
        return ClientCommandManager.literal(name);
        //#endif
    }

    private static <T> RequiredArgumentBuilder<FabricClientCommandSource, T> argument(String name, ArgumentType<T> type) {
        //#if MC >= 260100
        //$$ return ClientCommands.argument(name, type);
        //#else
        return ClientCommandManager.argument(name, type);
        //#endif
    }
}
