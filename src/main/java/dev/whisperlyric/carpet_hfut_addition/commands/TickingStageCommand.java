package dev.whisperlyric.carpet_hfut_addition.commands;

import carpet.patches.EntityPlayerMPFake;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.arguments.StringArgumentType;
import dev.whisperlyric.carpet_hfut_addition.HFUTSettings;
import dev.whisperlyric.carpet_hfut_addition.helpers.compat.guglecarpetaddition.GcaInvertButton;
import dev.whisperlyric.carpet_hfut_addition.helpers.rule.fakePlayerTickStage.FakePlayerTickStage;
import dev.whisperlyric.carpet_hfut_addition.helpers.rule.fakePlayerTickStage.FakePlayerTickStage.StageMode;
import dev.whisperlyric.carpet_hfut_addition.utils.CommandUtil;
import dev.whisperlyric.carpet_hfut_addition.utils.HFUTText;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

import static net.minecraft.commands.Commands.literal;

/**
 * {@code /player <player> tickingStage <global|invert|origin|likeReal>}: the
 * per-fake-player tick stage override, attached to carpet's {@code /player}
 * tree and gated by the {@code commandPlayerTickingStage} rule.
 */
public final class TickingStageCommand {
    private TickingStageCommand() {
    }

    public static LiteralArgumentBuilder<CommandSourceStack> tickingStageNode() {
        return literal("tickingStage")
                .requires(source -> CommandUtil.canUseCommand(source, HFUTSettings.commandPlayerTickingStage))
                .then(stage("global", StageMode.GLOBAL))
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
        StageMode applied = FakePlayerTickStage.setMode(fake, mode);
        boolean effective = FakePlayerTickStage.effectiveTicksLikeRealPlayer(fake);
        String stageKey = effective ? "hfut.fakePlayerTickStage.stage.t" : "hfut.fakePlayerTickStage.stage.f";
        source.sendSuccess(() -> HFUTText.forViewer(
                source, "hfut.fakePlayerTickStage.toggled",
                fake.getName().getString(), GcaInvertButton.boolText(effective),
                HFUTText.forViewer(source, stageKey).getString(),
                GcaInvertButton.boolText(FakePlayerTickStage.globalTicksLikeRealPlayer())
        ), true);
        return Command.SINGLE_SUCCESS;
    }
}
