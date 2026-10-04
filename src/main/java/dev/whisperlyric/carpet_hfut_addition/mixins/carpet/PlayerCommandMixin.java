package dev.whisperlyric.carpet_hfut_addition.mixins.carpet;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import dev.whisperlyric.carpet_hfut_addition.commands.TickingStageCommand;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Attaches {@code /player <player> tickingStage <...>} to carpet's own
 * {@code /player} command tree once carpet has built it.
 */
@Mixin(carpet.commands.PlayerCommand.class)
public abstract class PlayerCommandMixin {
    @Inject(method = "register", at = @At("RETURN"), remap = false)
    private static void hfut$extendPlayerTree(
            CommandDispatcher<CommandSourceStack> dispatcher,
            CommandBuildContext commandBuildContext,
            CallbackInfo ci
    ) {
        CommandNode<CommandSourceStack> playerLiteral = dispatcher.getRoot().getChild("player");
        CommandNode<CommandSourceStack> targets = playerLiteral == null ? null : playerLiteral.getChild("player");
        if (targets != null) {
            targets.addChild(TickingStageCommand.tickingStageNode().build());
        }
    }
}
