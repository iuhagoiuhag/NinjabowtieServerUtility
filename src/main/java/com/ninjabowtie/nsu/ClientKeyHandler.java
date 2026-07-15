package com.ninjabowtie.nsu;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ClientKeyHandler {
    private static final Map<Integer, String> KEY_MAP = new HashMap<>();
    private static final Map<Integer, KeyMapping> KEYBINDINGS = new HashMap<>();
    private static Method guiScreenMethod;
    private static boolean tickRegistered = false;

    public static void register() {
        ModConfig config = ModConfig.get();

        int index = 0;
        for (Map.Entry<String, String> entry : config.binds.entrySet()) {
            index++;
            int keyCode = getKeyCode(entry.getKey());

            KeyMapping kb = KeyMappingHelper.registerKeyMapping(
                new KeyMapping("key.nsu.command" + index, InputConstants.Type.KEYSYM, keyCode, KeyMapping.Category.MISC));

            KEYBINDINGS.put(keyCode, kb);
            KEY_MAP.put(keyCode, entry.getValue());
        }

        if (!tickRegistered) {
            tickRegistered = true;
            try {
                guiScreenMethod = Gui.class.getMethod("screen");
            } catch (NoSuchMethodException ignored) {}

            ClientTickEvents.END_CLIENT_TICK.register(client -> {
                if (client.player == null || hasScreen(client)) return;

                for (Map.Entry<Integer, KeyMapping> entry : KEYBINDINGS.entrySet()) {
                    if (entry.getValue().consumeClick()) {
                        String command = KEY_MAP.get(entry.getKey());
                        if (command != null && !command.isEmpty()) {
                            client.player.connection.sendCommand(command.startsWith("/") ? command.substring(1) : command);
                        }
                    }
                }
            });
        }
    }

    private static boolean hasScreen(Minecraft client) {
        if (guiScreenMethod != null) {
            try {
                return guiScreenMethod.invoke(client.gui) != null;
            } catch (Exception ignored) {
                return true;
            }
        }
        return client.screen != null;
    }

    private static int getKeyCode(String keyName) {
        return switch (keyName.toUpperCase()) {
            case "[" -> GLFW.GLFW_KEY_LEFT_BRACKET;
            case "]" -> GLFW.GLFW_KEY_RIGHT_BRACKET;
            case "\\" -> GLFW.GLFW_KEY_BACKSLASH;
            case "-" -> GLFW.GLFW_KEY_MINUS;
            case "=" -> GLFW.GLFW_KEY_EQUAL;
            case "SPACE" -> GLFW.GLFW_KEY_SPACE;
            case "SHIFT" -> GLFW.GLFW_KEY_LEFT_SHIFT;
            case "CTRL" -> GLFW.GLFW_KEY_LEFT_CONTROL;
            case "ALT" -> GLFW.GLFW_KEY_LEFT_ALT;
            case "TAB" -> GLFW.GLFW_KEY_TAB;
            case "ENTER" -> GLFW.GLFW_KEY_ENTER;
            case "BACKSPACE" -> GLFW.GLFW_KEY_BACKSPACE;
            case "DELETE" -> GLFW.GLFW_KEY_DELETE;
            case "INSERT" -> GLFW.GLFW_KEY_INSERT;
            case "HOME" -> GLFW.GLFW_KEY_HOME;
            case "END" -> GLFW.GLFW_KEY_END;
            case "PAGEUP" -> GLFW.GLFW_KEY_PAGE_UP;
            case "PAGEDOWN" -> GLFW.GLFW_KEY_PAGE_DOWN;
            case "F1" -> GLFW.GLFW_KEY_F1;
            case "F2" -> GLFW.GLFW_KEY_F2;
            case "F3" -> GLFW.GLFW_KEY_F3;
            case "F4" -> GLFW.GLFW_KEY_F4;
            case "F5" -> GLFW.GLFW_KEY_F5;
            case "F6" -> GLFW.GLFW_KEY_F6;
            case "F7" -> GLFW.GLFW_KEY_F7;
            case "F8" -> GLFW.GLFW_KEY_F8;
            case "F9" -> GLFW.GLFW_KEY_F9;
            case "F10" -> GLFW.GLFW_KEY_F10;
            case "F11" -> GLFW.GLFW_KEY_F11;
            case "F12" -> GLFW.GLFW_KEY_F12;
            default -> {
                if (keyName.length() == 1) {
                    yield keyName.charAt(0);
                }
                yield GLFW.GLFW_KEY_UNKNOWN;
            }
        };
    }
}
