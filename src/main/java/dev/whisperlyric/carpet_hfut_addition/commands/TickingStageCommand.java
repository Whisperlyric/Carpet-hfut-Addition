package dev.whisperlyric.carpet_hfut_addition.commands;

import carpet.patches.EntityPlayerMPFake;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.arguments.StringArgumentType;
import dev.whisperlyric.carpet_hfut_addition.HFUTSettings;
import dev.whisperlyric.carpet_hfut_addition.helpers.rule.fakePlayerTickStage.FakePlayerTickStage;
import dev.whisperlyric.carpet_hfut_addition.helpers.rule.fakePlayerTickStage.FakePlayerTickStage.StageMode;
import dev.whisperlyric.carpet_hfut_addition.utils.CommandUtil;
import dev.whisperlyric.carpet_hfut_addition.utils.HFUTText;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

import static net.minecraft.commands.Commands.literal;

/**
 * {@code /player <player> tickingStage}: queries (no argument) or sets the
 * per-fake tick stage. invert flips the CURRENT effective stage; origin pins
 * the entity phase; likeReal pins the network phase. Feedback is gray italic.
 */
public final class TickingStageCommand {
    private TickingStageCommand() {
    }

    public static LiteralArgumentBuilder<CommandSourceStack> tickingStageNode() {
        return literal("tickingStage")
                .requires(source -> CommandUtil.canUseCommand(source, HFUTSettings.commandPlayerTickingStage))
                .executes(context -> query(context.getSource(),
                        StringArgumentType.getString(context, "player")))
                .then(stage("invert", StageMode.INVERT))
                .then(stage("origin", StageMode.ORIGIN))
                .then(stage("likeReal", StageMode.LIKE_REAL));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> stage(String name, StageMode mode) {
        return literal(name).executes(context ->
                setStage(context.getSource(), StringArgumentType.getString(context, "player"), mode));
    }

    private static int setStage(CommandSourceStack source, String playerName, StageMode mode) {
        ServerPlayer target = source.getServer().getPlayerList().getPlayerByName(playerName);
        if (!(target instanceof EntityPlayerMPFake fake)) {
            source.sendFailure(HFUTText.forViewer(source, "hfut.fakePlayerTickStage.not_fake", playerName));
            return 0;
        }
        FakePlayerTickStage.setMode(fake, mode);
        sendStage(source, true, fake);
        return Command.SINGLE_SUCCESS;
    }

    private static int query(CommandSourceStack source, String playerName) {
        ServerPlayer target = source.getServer().getPlayerList().getPlayerByName(playerName);
        if (!(target instanceof EntityPlayerMPFake fake)) {
            source.sendFailure(HFUTText.forViewer(source, "hfut.fakePlayerTickStage.not_fake", playerName));
            return 0;
        }
        sendStage(source, false, fake);
        return Command.SINGLE_SUCCESS;
    }

    /** Gray-italic feedback: the fake's current stage pair, plus the global's for changes. */
    private static void sendStage(CommandSourceStack source, boolean adjusted, EntityPlayerMPFake fake) {
        ServerPlayer viewer = source.getPlayer();
        String key = adjusted ? "hfut.fakePlayerTickStage.adjusted" : "hfut.fakePlayerTickStage.query";
        boolean effective = FakePlayerTickStage.effectiveTicksLikeRealPlayer(fake);
        if (adjusted) {
            source.sendSuccess(() -> HFUTText.forViewer(source, key,
                            fake.getName().getString(),
                            FakePlayerTickStage.stageDisplayFor(viewer, effective),
                            FakePlayerTickStage.stageDisplayFor(viewer, FakePlayerTickStage.globalTicksLikeRealPlayer()))
                    .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC), true);
        } else {
            source.sendSuccess(() -> HFUTText.forViewer(source, key,
                            fake.getName().getString(),
                            FakePlayerTickStage.stageDisplayFor(viewer, effective))
                    .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC), false);
        }
    }
}
