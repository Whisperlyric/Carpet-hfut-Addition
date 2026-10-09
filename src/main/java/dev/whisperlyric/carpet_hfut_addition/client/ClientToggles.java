package dev.whisperlyric.carpet_hfut_addition.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.whisperlyric.carpet_hfut_addition.HFUTServerMod;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/** Client-side switches, persisted to {@code config/carpet-hfut-addition/client_settings.json}. */
public final class ClientToggles {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir()
            .resolve(HFUTServerMod.getModId()).resolve("client_settings.json");
    private static final String HIDE_PAUSE_SOCIAL_ROW = "hidePauseSocialRow";

    /** Hides the social/friends button row on the 26.2+ pause screen and title screen. */
    public static boolean hidePauseSocialRow = false;

    /**
     * ModMenu's game-menu button style before we switched it to the full-row style, or null when we
     * never switched it. Not persisted, so a style changed in an earlier session cannot be restored.
     */
    private static String modMenuPreviousGameMenuStyle;

    static {
        load();
    }

    private ClientToggles() {
    }

    public static void save() {
        JsonObject json = new JsonObject();
        json.addProperty(HIDE_PAUSE_SOCIAL_ROW, hidePauseSocialRow);
        try {
            Files.createDirectories(FILE.getParent());
            try (Writer writer = Files.newBufferedWriter(FILE, StandardCharsets.UTF_8)) {
                GSON.toJson(json, writer);
            }
        } catch (IOException e) {
            HFUTServerMod.LOGGER.warn("[HFUT] Failed to save client settings", e);
        }
    }

    private static void load() {
        if (!Files.exists(FILE)) {
            return;
        }
        try (Reader reader = Files.newBufferedReader(FILE, StandardCharsets.UTF_8)) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            hidePauseSocialRow = json.has(HIDE_PAUSE_SOCIAL_ROW)
                    && json.get(HIDE_PAUSE_SOCIAL_ROW).getAsBoolean();
        } catch (IOException | RuntimeException e) {
            HFUTServerMod.LOGGER.warn("[HFUT] Failed to load client settings", e);
        }
    }

    /**
     * Asks ModMenu (when installed) to render its pause-menu entry as a full row while the social
     * row is hidden, restoring the style it had before once the row comes back. ModMenu is an
     * optional dependency, so every access goes through reflection and silently no-ops when it is
     * missing or when its config internals move.
     */
    public static void syncModMenuGameMenuStyle(boolean hidden) {
        try {
            Class<?> configClass = Class.forName("com.terraformersmc.modmenu.config.ModMenuConfig");
            Object option = configClass.getField("GAME_MENU_BUTTON_STYLE").get(null);
            Method getValue = option.getClass().getMethod("getValue");
            Method setValue = option.getClass().getMethod("setValue", Enum.class);
            Class<?> styleClass = Class.forName(
                    "com.terraformersmc.modmenu.config.ModMenuConfig$GameMenuButtonStyle");
            Object current = getValue.invoke(option);
            if (hidden) {
                if (modMenuPreviousGameMenuStyle == null && current instanceof Enum<?> style) {
                    modMenuPreviousGameMenuStyle = style.name();
                }
                Object insert = styleConstant(styleClass, "INSERT");
                if (insert != null) {
                    setValue.invoke(option, insert);
                    saveModMenuConfig();
                }
            } else if (modMenuPreviousGameMenuStyle != null) {
                Object previous = styleConstant(styleClass, modMenuPreviousGameMenuStyle);
                if (previous != null) {
                    setValue.invoke(option, previous);
                    saveModMenuConfig();
                }
                modMenuPreviousGameMenuStyle = null;
            }
        } catch (ReflectiveOperationException | RuntimeException ignored) {
            // ModMenu absent or its config changed shape - the toggle still applies on its own.
        }
    }

    private static Object styleConstant(Class<?> styleClass, String name) {
        for (Object constant : styleClass.getEnumConstants()) {
            if (((Enum<?>) constant).name().equals(name)) {
                return constant;
            }
        }
        return null;
    }

    private static void saveModMenuConfig() throws ReflectiveOperationException {
        Class<?> managerClass = Class.forName("com.terraformersmc.modmenu.config.ModMenuConfigManager");
        managerClass.getMethod("save").invoke(null);
    }
}
