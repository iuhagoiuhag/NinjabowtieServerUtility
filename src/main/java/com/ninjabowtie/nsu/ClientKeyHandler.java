package com.ninjabowtie.nsu;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class ClientKeyHandler {
    private static final List<KeyMapping> KEYBINDINGS = new ArrayList<>();
    private static final Map<KeyMapping, String> KEY_COMMANDS = new IdentityHashMap<>();
    private static Method guiScreenMethod;
    private static Field currentScreenField;
    private static boolean tickRegistered = false;

    static {
        try {
            currentScreenField = Minecraft.class.getDeclaredField("currentScreen");
            currentScreenField.setAccessible(true);
        } catch (NoSuchFieldException ignored) {
            try {
                currentScreenField = Minecraft.class.getDeclaredField("field_1755");
                currentScreenField.setAccessible(true);
            } catch (NoSuchFieldException ignored2) {}
        }
    }

    public static void register() {
        ModConfig config = ModConfig.get();

        if (KEYBINDINGS.isEmpty()) {
            int index = 0;
            for (Map.Entry<String, String> entry : config.binds.entrySet()) {
                index++;
                int keyCode = getKeyCode(entry.getKey());

                KeyMapping kb = KeyMappingHelper.registerKeyMapping(
                    new KeyMapping("key.nsu.command" + index, InputConstants.Type.KEYSYM, keyCode, KeyMapping.Category.MISC));

                KEYBINDINGS.add(kb);
            }
        } else {
            int index = 0;
            for (Map.Entry<String, String> entry : config.binds.entrySet()) {
                index++;
                if (index <= KEYBINDINGS.size()) {
                    KeyMapping kb = KEYBINDINGS.get(index - 1);
                    kb.setKey(InputConstants.Type.KEYSYM.getOrCreate(getKeyCode(entry.getKey())));
                } else {
                    int keyCode = getKeyCode(entry.getKey());
                    KeyMapping kb = KeyMappingHelper.registerKeyMapping(
                        new KeyMapping("key.nsu.command" + index, InputConstants.Type.KEYSYM, keyCode, KeyMapping.Category.MISC));
                    KEYBINDINGS.add(kb);
                }
            }
            for (int i = index; i < KEYBINDINGS.size(); i++) {
                KEYBINDINGS.get(i).setKey(InputConstants.UNKNOWN);
            }
        }

        KEY_COMMANDS.clear();
        int index = 0;
        for (Map.Entry<String, String> entry : config.binds.entrySet()) {
            index++;
            if (index <= KEYBINDINGS.size()) {
                KEY_COMMANDS.put(KEYBINDINGS.get(index - 1), entry.getValue());
            }
        }

        if (!tickRegistered) {
            tickRegistered = true;
            try {
                guiScreenMethod = Gui.class.getMethod("screen");
            } catch (NoSuchMethodException ignored) {}

            ClientTickEvents.END_CLIENT_TICK.register(client -> {
                if (client.player == null || hasScreen(client)) return;

                for (Map.Entry<KeyMapping, String> entry : KEY_COMMANDS.entrySet()) {
                    if (entry.getKey().consumeClick()) {
                        String command = entry.getValue();
                        if (command != null && !command.isEmpty() && client.player.connection != null) {
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
        if (currentScreenField != null) {
            try {
                return currentScreenField.get(client) != null;
            } catch (Exception ignored) {}
        }
        return true;
    }

    private static int getKeyCode(String keyName) {
        return switch (keyName.toUpperCase()) {
            case "[" -> 91;
            case "]" -> 93;
            case "\\" -> 92;
            case "-" -> 45;
            case "=" -> 61;
            case "," -> 44;
            case "." -> 46;
            case ";" -> 59;
            case "'" -> 39;
            case "/" -> 47;
            case "`" -> 96;
            case "SPACE" -> 32;
            case "SHIFT" -> 340;
            case "RSHIFT" -> 344;
            case "CTRL" -> 341;
            case "RCTRL" -> 345;
            case "ALT" -> 342;
            case "RALT" -> 346;
            case "TAB" -> 258;
            case "ENTER" -> 257;
            case "CAPSLOCK" -> 280;
            case "NUMLOCK" -> 282;
            case "SCROLLLOCK" -> 281;
            case "PAUSE" -> 284;
            case "BACKSPACE" -> 259;
            case "DELETE" -> 261;
            case "INSERT" -> 260;
            case "HOME" -> 268;
            case "END" -> 269;
            case "PAGEUP" -> 266;
            case "PAGEDOWN" -> 267;
            case "UP" -> 265;
            case "DOWN" -> 264;
            case "LEFT" -> 263;
            case "RIGHT" -> 262;
            case "MENU" -> 348;
            case "F1" -> 290;
            case "F2" -> 291;
            case "F3" -> 292;
            case "F4" -> 293;
            case "F5" -> 294;
            case "F6" -> 295;
            case "F7" -> 296;
            case "F8" -> 297;
            case "F9" -> 298;
            case "F10" -> 299;
            case "F11" -> 300;
            case "F12" -> 301;
            default -> {
                if (keyName.length() == 1) {
                    yield keyName.toUpperCase().charAt(0);
                }
                yield -1; // GLFW.GLFW_KEY_UNKNOWN
            }
        };
    }
}