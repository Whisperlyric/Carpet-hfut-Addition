package dev.whisperlyric.carpet_hfut_addition.helpers.rule.singleFakePlayerLoadRange;

import carpet.patches.EntityPlayerMPFake;
import carpet.utils.Messenger;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.whisperlyric.carpet_hfut_addition.HFUTSettings;
import dev.whisperlyric.carpet_hfut_addition.utils.CommandUtil;
import dev.whisperlyric.carpet_hfut_addition.utils.HFUTText;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

/**
 * {@code /player <player> loadRange <n>}: replaces one fake player's chunk
 * loading with a single ticket of our own; {@code loadRange reset} clears the
 * setting and restores the server default. Only online fake players can be
 * targeted - an offline name answers with carpet's own wording, a real player
 * with ours. Feedback is gray italic.
 */
public final class FakePlayerLoadRangeCommand {
    private FakePlayerLoadRangeCommand() {
    }

    public static LiteralArgumentBuilder<CommandSourceStack> loadRangeNode() {
        return literal("loadRange")
                .requires(source -> CommandUtil.canUseCommand(source,
                        HFUTSettings.commandSingleFakePlayerLoadRange))
                .then(argument("range", IntegerArgumentType.integer(
                                SingleFakePlayerLoadRangeManager.MIN_RADIUS,
                                SingleFakePlayerLoadRangeManager.MAX_RADIUS))
                        .executes(FakePlayerLoadRangeCommand::set))
                .then(literal("reset").executes(FakePlayerLoadRangeCommand::reset));
    }

    private static int set(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        EntityPlayerMPFake fake = fake(context);
        if (fake == null) {
            return 0;
        }
        int range = IntegerArgumentType.getInteger(context, "range");
        SingleFakePlayerLoadRangeManager.setRange(fake, range);
        source.sendSuccess(() -> HFUTText.forViewer(source, "hfut.fakePlayerLoadRange.set",
                        fake.getName().getString(), range)
                .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int reset(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        EntityPlayerMPFake fake = fake(context);
        if (fake == null) {
            return 0;
        }
        SingleFakePlayerLoadRangeManager.reset(fake);
        source.sendSuccess(() -> HFUTText.forViewer(source, "hfut.fakePlayerLoadRange.reset",
                        fake.getName().getString())
                .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC), true);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Resolves the online fake player named by the {@code player} argument, or
     * reports why it cannot be used: an offline name gets carpet's own message,
     * a real player gets ours.
     */
    private static EntityPlayerMPFake fake(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        String name = StringArgumentType.getString(context, "player");
        ServerPlayer target = source.getServer().getPlayerList().getPlayerByName(name);
        if (target == null) {
            Messenger.m(source, "r Can only manipulate existing players");
            return null;
        }
        if (!(target instanceof EntityPlayerMPFake fake)) {
            source.sendFailure(HFUTText.forViewer(source, "hfut.fakePlayerLoadRange.not_fake"));
            return null;
        }
        return fake;
    }
}
