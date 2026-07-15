package com.ninjabowtie.nsu;

import net.fabricmc.api.ClientModInitializer;

public class NSU implements ClientModInitializer {
    public static final String MOD_ID = "nsu";

    @Override
    public void onInitializeClient() {
        ModConfig.load();
        ClientKeyHandler.register();
    }
}
