package dev.whisperlyric.carpet_hfut_addition.utils;

import dev.whisperlyric.carpet_hfut_addition.mixins.ServerPlayerLanguageAccessor;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;

import java.util.Locale;
import java.util.Map;

/**
 * Server-side text for our own feedback messages: resolves the lang map for the
 * viewer's language and returns literal components, so clients without our mod
 * still see readable text.
 */
public final class HFUTText {
    private HFUTText() {
    }

    public static String template(String lang, String key) {
        String l = lang == null || lang.isEmpty() ? "en_us" : lang.toLowerCase(Locale.ROOT);
        String template = Translations.getTranslations(l).get(key);
        if (template == null) {
            template = Translations.getTranslations("en_us").get(key);
        }
        return template != null ? template : key;
    }

    public static MutableComponent forLang(String lang, String key, Object... args) {
        return Component.literal(String.format(template(lang, key), args));
    }

    public static MutableComponent forPlayer(ServerPlayer viewer, String key, Object... args) {
        return forLang(((ServerPlayerLanguageAccessor) viewer).hfut$language(), key, args);
    }

    /** Console and command blocks get the en_us text. */
    public static MutableComponent forViewer(CommandSourceStack source, String key, Object... args) {
        ServerPlayer player = source.getPlayer();
        return player != null ? forPlayer(player, key, args) : forLang("en_us", key, args);
    }
}
