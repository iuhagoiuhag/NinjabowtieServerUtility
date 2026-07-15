package com.ninjabowtie.nsu;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public class ModConfig {
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("nsu.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static ModConfig INSTANCE = new ModConfig();

    public Map<String, String> binds = new LinkedHashMap<>();

    public static void load() {
        if (Files.exists(CONFIG_PATH)) {
            try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                INSTANCE = GSON.fromJson(reader, ModConfig.class);
            } catch (IOException e) {
                System.err.println("[NSU] Failed to load config: " + e.getMessage());
            }
        }
        if (INSTANCE.binds == null || INSTANCE.binds.isEmpty()) {
            INSTANCE.binds = new LinkedHashMap<>();
            INSTANCE.binds.put("[", "/ah");
            INSTANCE.binds.put("]", "/rtp");
            INSTANCE.binds.put("\\", "/home");
        }
        save();
    }

    public static void save() {
        try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
            GSON.toJson(INSTANCE, writer);
        } catch (IOException e) {
            System.err.println("[NSU] Failed to save config: " + e.getMessage());
        }
    }

    public static ModConfig get() {
        return INSTANCE;
    }
}
