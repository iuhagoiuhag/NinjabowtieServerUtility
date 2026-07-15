package com.ninjabowtie.nsu;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class ModConfig {
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("nsu.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static ModConfig INSTANCE = new ModConfig();

    public String key1 = "[";
    public String command1 = "/ah";
    public String key2 = "]";
    public String command2 = "/rtp";
    public String key3 = "\\";
    public String command3 = "/home";

    public static void load() {
        if (Files.exists(CONFIG_PATH)) {
            try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                INSTANCE = GSON.fromJson(reader, ModConfig.class);
            } catch (IOException e) {
                System.err.println("[NSU] Failed to load config: " + e.getMessage());
            }
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
