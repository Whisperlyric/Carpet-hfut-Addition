package dev.whisperlyric.carpet_hfut_addition.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import dev.whisperlyric.carpet_hfut_addition.HFUTServer;
import dev.whisperlyric.carpet_hfut_addition.HFUTServerMod;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

/**
 * The {@code /hfut} root: only a version stub lives here; real features attach
 * to carpet's own trees ({@code /player <player> tickingStage ...}).
 */
public class HFUTCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandBuildContext) {
        dispatcher.register(Commands.literal("hfut")
                .then(Commands.literal("version").executes(context -> {
                    context.getSource().sendSystemMessage(
                            Component.literal(String.format("%s %s", HFUTServer.fancyName, HFUTServerMod.getVersion()))
                    );
                    return Command.SINGLE_SUCCESS;
                }))
        );
    }
}
