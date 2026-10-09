package dev.whisperlyric.carpet_hfut_addition.client;

import net.fabricmc.api.ClientModInitializer;

public class HFUTClientMod implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        HFUTClientCommands.init();
    }
}
