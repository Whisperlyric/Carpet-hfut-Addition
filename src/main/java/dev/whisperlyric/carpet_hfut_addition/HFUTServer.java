package dev.whisperlyric.carpet_hfut_addition;

import carpet.CarpetExtension;
import carpet.CarpetServer;
import carpet.api.settings.Rule;
import carpet.api.settings.SettingsManager;
import com.mojang.brigadier.CommandDispatcher;
import dev.whisperlyric.carpet_hfut_addition.FakePlayerOpenStorageSettings;
import dev.whisperlyric.carpet_hfut_addition.commands.HFUTCommand;
import dev.whisperlyric.carpet_hfut_addition.commands.PearlTraceCommand;
import dev.whisperlyric.carpet_hfut_addition.commands.SimpleLazyChunkCommand;
//#if MC >= 260100
//$$ import dev.whisperlyric.carpet_hfut_addition.commands.TradeSeqCommand;
//#endif
import dev.whisperlyric.carpet_hfut_addition.helpers.rule.fakePlayerTickStage.FakePlayerTickStage;
import dev.whisperlyric.carpet_hfut_addition.helpers.rule.fakePlayerTickStage.TisBridge;
import dev.whisperlyric.carpet_hfut_addition.helpers.rule.fakePlayerOpen.FakePlayerStorageEditor;
import dev.whisperlyric.carpet_hfut_addition.helpers.rule.ghostPearl.GhostPearlGuard;
import dev.whisperlyric.carpet_hfut_addition.helpers.rule.ghostPearl.PearlTraceStore;
import dev.whisperlyric.carpet_hfut_addition.helpers.rule.simpleLazyChunk.SimpleLazyChunkManager;
import dev.whisperlyric.carpet_hfut_addition.logger.HFUTLoggers;
import dev.whisperlyric.carpet_hfut_addition.network.HFUTNetwork;
import dev.whisperlyric.carpet_hfut_addition.utils.Translations;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Map;

public class HFUTServer implements CarpetExtension {
    public static final String fancyName = HFUTServerMod.MOD_NAME;
    public static final String MOD_ID = HFUTServerMod.getModId();
    public static final Logger LOGGER = HFUTServerMod.LOGGER;
    private static final HFUTServer INSTANCE = new HFUTServer();

    public static HFUTServer getInstance() {
        return INSTANCE;
    }

    public static void init() {
        CarpetServer.manageExtension(INSTANCE);
        // both sides: the client needs the codec registered to send a page request
        HFUTNetwork.init();
    }

    @Override
    public void onGameStarted() {
        CarpetServer.settingsManager.parseSettingsClass(HFUTSettings.class);
        if (FakePlayerTickStage.isTisPresent()) {
            // TIS owns the same-name rule and its machinery; registering ours would conflict
            LOGGER.info("[HFUT] carpet-tis-addition present: its fakePlayerTicksLikeRealPlayer rule is used, HFUT only adds the per-player override");
        } else {
            try {
                CarpetServer.settingsManager.parseSettingsClass(FakePlayerTickStageSettings.class);
            } catch (UnsupportedOperationException e) {
                LOGGER.info("[HFUT] fakePlayerTicksLikeRealPlayer already provided by carpet-tis-addition; deferring to it", e);
            }
        }
        if (GhostPearlGuard.ignyPresent()) {
            // same-name deference: IGNY's ghostEnderPearlFix takes over
            LOGGER.info("[HFUT] carpet-igny-addition present: its ghostEnderPearlFix rule is used, HFUT only adds ghostEnderPearlTrace");
        } else {
            CarpetServer.settingsManager.parseSettingsClass(GhostPearlFixSettings.class);
        }
        // /player <name> open inventory|enderchest: self-contained, no GCA required
        try {
            CarpetServer.settingsManager.parseSettingsClass(FakePlayerOpenStorageSettings.class);
        } catch (UnsupportedOperationException e) {
            LOGGER.info("[HFUT] commandFakePlayerOpenStorage already provided elsewhere; skipping", e);
        }
        SettingsManager.registerGlobalRuleObserver((source, rule, newValue) -> {
            if ("fakePlayerTicksLikeRealPlayer".equals(rule.name())) {
                TisBridge.setShadowGlobal(Boolean.parseBoolean(newValue));
            }
        });
        long ruleCount = Arrays.stream(HFUTSettings.class.getDeclaredFields()).
                filter(field -> field.isAnnotationPresent(Rule.class)).count();
        LOGGER.info("{} registered {} rule(s)", fancyName, ruleCount);
    }

    @Override
    public void registerLoggers() {
        HFUTLoggers.registerLoggers();
    }

    @Override
    public void registerCommands(
            CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandBuildContext
    ) {
        HFUTCommand.register(dispatcher, commandBuildContext);
        PearlTraceCommand.register(dispatcher);
        SimpleLazyChunkCommand.register(dispatcher);
        //#if MC >= 260100
        //$$ TradeSeqCommand.register(dispatcher);
        //#endif
    }

    @Override
    public void onServerLoaded(MinecraftServer server) {
        if (FakePlayerTickStage.isTisPresent()) {
            TisBridge.refreshFromField();
        }
        PearlTraceStore.get().attach(server);
        SimpleLazyChunkManager.attach(server);
        FakePlayerStorageEditor.attach(server);
    }

    @Override
    public void onServerClosed(MinecraftServer server) {
        FakePlayerTickStage.clearOnServerStop();
        PearlTraceStore.get().close();
        PearlTraceCommand.clearPending();
        SimpleLazyChunkManager.clear();
        FakePlayerStorageEditor.flushAll();
        FakePlayerStorageEditor.clear();
    }

    @Override
    public String version() {
        return MOD_ID;
    }

    @Override
    public Map<String, String> canHasTranslations(String lang) {
        return Translations.getTranslations(lang);
    }
}
