package dev.whisperlyric.carpet_hfut_addition.client;

//#if MC >= 260200
//$$ import com.mojang.brigadier.arguments.BoolArgumentType;
//$$ import com.mojang.brigadier.context.CommandContext;
//$$ import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
//$$ import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
//$$ import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
//$$ import net.minecraft.network.chat.Component;
//#endif

/**
 * Client-only commands under the {@code /hfutclient} root. They never reach the
 * server: each one flips a {@link ClientToggles} field, saves it and reports
 * back, so no permission rule applies.
 */
public final class HFUTClientCommands {

    private HFUTClientCommands() {
    }

    public static void init() {
        //#if MC >= 260200
        //$$ var hidePauseSocial = ClientCommands.literal("hidepausesocial")
        //$$         .executes(ctx -> togglePauseSocial(ctx, !ClientToggles.hidePauseSocialRow))
        //$$         .then(ClientCommands.argument("state", BoolArgumentType.bool())
        //$$                 .executes(ctx -> togglePauseSocial(ctx, BoolArgumentType.getBool(ctx, "state"))));
        //$$
        //$$ ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
        //$$         ClientCommands.literal("hfutclient").then(hidePauseSocial)));
        //#endif
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
}
