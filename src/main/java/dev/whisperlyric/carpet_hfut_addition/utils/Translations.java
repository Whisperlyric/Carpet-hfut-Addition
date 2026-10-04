package dev.whisperlyric.carpet_hfut_addition.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dev.whisperlyric.carpet_hfut_addition.HFUTServerMod;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

/**
 * Loads the mod's lang files and feeds them to Carpet via
 * {@code CarpetExtension#canHasTranslations}, so rule descriptions, categories
 * and command messages get translated.
 */
public final class Translations {
    private static final Gson GSON = new Gson();

    private Translations() {
    }

    public static Map<String, String> getTranslations(String lang) {
        String path = String.format("assets/%s/lang/%s.json", HFUTServerMod.getModId(), lang);
        try (InputStream stream = Translations.class.getClassLoader().getResourceAsStream(path)) {
            if (stream == null) {
                return Collections.emptyMap();
            }
            String json = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            return GSON.fromJson(json, new TypeToken<Map<String, String>>() {
            }.getType());
        } catch (IOException e) {
            return Collections.emptyMap();
        }
    }
}
