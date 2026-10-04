package dev.whisperlyric.carpet_hfut_addition;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HFUTServerMod implements ModInitializer {
    public static final String MOD_ID = "carpet-hfut-addition";
    public static final String MOD_NAME = "Carpet HFUT Addition";
    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);
    public static String version = "unknown";

    @Override
    public void onInitialize() {
        version = FabricLoader.getInstance()
                .getModContainer(MOD_ID)
                .map(container -> container.getMetadata().getVersion().getFriendlyString())
                .orElse("unknown");
        HFUTServer.init();
        LOGGER.info("{} v{} loaded", MOD_NAME, version);
    }

    public static String getModId() {
        return MOD_ID;
    }

    public static String getVersion() {
        return version;
    }
}
