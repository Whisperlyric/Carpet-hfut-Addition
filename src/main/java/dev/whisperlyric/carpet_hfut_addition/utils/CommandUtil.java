package dev.whisperlyric.carpet_hfut_addition.utils;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import java.util.Locale;

/**
 * Carpet-style command permission resolution for our command-gating rules:
 * "true" / "false" / "ops" (level 2) / "0"-"4".
 */
public final class CommandUtil {
    private CommandUtil() {
    }

    public static boolean canUseCommand(CommandSourceStack source, String level) {
        String l = level == null || level.isEmpty() ? "ops" : level.toLowerCase(Locale.ROOT);
        return switch (l) {
            case "true" -> true;
            case "false" -> false;
            case "ops" -> checkLevel(source, 2);
            default -> {
                int n;
                try {
                    n = Integer.parseInt(l);
                } catch (NumberFormatException e) {
                    yield checkLevel(source, 2);
                }
                yield checkLevel(source, Math.max(0, Math.min(4, n)));
            }
        };
    }

    private static boolean checkLevel(CommandSourceStack source, int level) {
        //#if MC >= 12111
        //$$ net.minecraft.server.permissions.PermissionCheck check = switch (level) {
        //$$     case 0 -> Commands.LEVEL_ALL;
        //$$     case 1 -> Commands.LEVEL_MODERATORS;
        //$$     case 3 -> Commands.LEVEL_ADMINS;
        //$$     case 4 -> Commands.LEVEL_OWNERS;
        //$$     default -> Commands.LEVEL_GAMEMASTERS;
        //$$ };
        //$$ return check.check(source.permissions());
        //#else
        return source.hasPermission(level);
        //#endif
    }
}
