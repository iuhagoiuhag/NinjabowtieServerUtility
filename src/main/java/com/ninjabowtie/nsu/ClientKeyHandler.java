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
        if (currentScreenField != null) {
            try {
                return currentScreenField.get(client) != null;
            } catch (Exception ignored) {}
        }
        return true;
    }

    private static int getKeyCode(String keyName) {
        return switch (keyName.toUpperCase()) {
            case "[" -> 91; // GLFW.GLFW_KEY_LEFT_BRACKET
            case "]" -> 93; // GLFW.GLFW_KEY_RIGHT_BRACKET
            case "\\" -> 92; // GLFW.GLFW_KEY_BACKSLASH
            case "-" -> 45; // GLFW.GLFW_KEY_MINUS
            case "=" -> 61; // GLFW.GLFW_KEY_EQUAL
            case "SPACE" -> 32; // GLFW.GLFW_KEY_SPACE
            case "SHIFT" -> 340; // GLFW.GLFW_KEY_LEFT_SHIFT
            case "CTRL" -> 341; // GLFW.GLFW_KEY_LEFT_CONTROL
            case "ALT" -> 342; // GLFW.GLFW_KEY_LEFT_ALT
            case "TAB" -> 258; // GLFW.GLFW_KEY_TAB
            case "ENTER" -> 257; // GLFW.GLFW_KEY_ENTER
            case "BACKSPACE" -> 259; // GLFW.GLFW_KEY_BACKSPACE
            case "DELETE" -> 261; // GLFW.GLFW_KEY_DELETE
            case "INSERT" -> 260; // GLFW.GLFW_KEY_INSERT
            case "HOME" -> 268; // GLFW.GLFW_KEY_HOME
            case "END" -> 269; // GLFW.GLFW_KEY_END
            case "PAGEUP" -> 266; // GLFW.GLFW_KEY_PAGE_UP
            case "PAGEDOWN" -> 267; // GLFW.GLFW_KEY_PAGE_DOWN
            case "F1" -> 290; // GLFW.GLFW_KEY_F1
            case "F2" -> 291; // GLFW.GLFW_KEY_F2
            case "F3" -> 292; // GLFW.GLFW_KEY_F3
            case "F4" -> 293; // GLFW.GLFW_KEY_F4
            case "F5" -> 294; // GLFW.GLFW_KEY_F5
            case "F6" -> 295; // GLFW.GLFW_KEY_F6
            case "F7" -> 296; // GLFW.GLFW_KEY_F7
            case "F8" -> 297; // GLFW.GLFW_KEY_F8
            case "F9" -> 298; // GLFW.GLFW_KEY_F9
            case "F10" -> 299; // GLFW.GLFW_KEY_F10
            case "F11" -> 300; // GLFW.GLFW_KEY_F11
            case "F12" -> 301; // GLFW.GLFW_KEY_F12
            case "," -> 44; // GLFW.GLFW_KEY_COMMA
            case "." -> 46; // GLFW.GLFW_KEY_PERIOD
            case "/" -> 47; // GLFW.GLFW_KEY_SLASH
            case ";" -> 59; // GLFW.GLFW_KEY_SEMICOLON
            case "'" -> 39; // GLFW.GLFW_KEY_APOSTROPHE
            case "`" -> 96; // GLFW.GLFW_KEY_GRAVE_ACCENT
            case "UP" -> 265; // GLFW.GLFW_KEY_UP
            case "DOWN" -> 264; // GLFW.GLFW_KEY_DOWN
            case "LEFT" -> 263; // GLFW.GLFW_KEY_LEFT
            case "RIGHT" -> 262; // GLFW.GLFW_KEY_RIGHT
            case "CAPS_LOCK" -> 280; // GLFW.GLFW_KEY_CAPS_LOCK
            case "NUM_LOCK" -> 282; // GLFW.GLFW_KEY_NUM_LOCK
            case "SCROLL_LOCK" -> 281; // GLFW.GLFW_KEY_SCROLL_LOCK
            case "MENU" -> 348; // GLFW.GLFW_KEY_MENU
            default -> {
                if (keyName.length() == 1) {
                    yield keyName.charAt(0);
                }
                yield -1; // GLFW.GLFW_KEY_UNKNOWN
            }
        };
    }
}