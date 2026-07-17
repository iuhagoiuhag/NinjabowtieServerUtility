package com.ninjabowtie.nsu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NsuConfigScreen extends Screen {
    private final Screen parent;
    private final List<String> keys = new ArrayList<>();
    private final List<String> commands = new ArrayList<>();
    private EditBox[] commandFields;
    private Button[] keyButtons;
    private int selectedKeyIndex = -1;

    protected NsuConfigScreen(Screen parent) {
        super(Component.translatable("nsu.config.title"));
        this.parent = parent;
        ModConfig config = ModConfig.get();
        for (Map.Entry<String, String> entry : config.binds.entrySet()) {
            keys.add(entry.getKey());
            commands.add(entry.getValue());
        }
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreenAndShow(this.parent);
    }

    @Override
    protected void init() {
        int count = keys.size();
        commandFields = new EditBox[count];
        keyButtons = new Button[count];
        int centerX = width / 2;

        for (int i = 0; i < count; i++) {
            int y = 50 + i * 30;
            int idx = i;

            keyButtons[i] = Button.builder(
                Component.literal("Key " + (i + 1) + ": " + keys.get(i)),
                btn -> startKeyCapture(idx)
            )
            .bounds(centerX - 155, y, 120, 20)
            .build();
            addRenderableWidget(keyButtons[i]);

            commandFields[i] = new EditBox(
                font, centerX - 25, y, 180, 20,
                Component.translatable("nsu.command" + (i + 1))
            );
            commandFields[i].setValue(commands.get(i));
            commandFields[i].setMaxLength(256);
            addRenderableWidget(commandFields[i]);
        }

        int addY = 50 + count * 30;
        addRenderableWidget(Button.builder(
            Component.literal("+ Add Bind"),
            btn -> addBind()
        )
        .bounds(centerX - 95, addY, 190, 20)
        .build());

        addRenderableWidget(Button.builder(
            Component.translatable("nsu.done"),
            btn -> saveAndClose()
        )
        .bounds(centerX - 95, addY + 30, 90, 20)
        .build());

        addRenderableWidget(Button.builder(
            Component.translatable("nsu.cancel"),
            btn -> onClose()
        )
        .bounds(centerX + 5, addY + 30, 90, 20)
        .build());
    }

    private void addBind() {
        for (int i = 0; i < commandFields.length; i++) {
            commands.set(i, commandFields[i].getValue());
        }
        keys.add("");
        commands.add("");
        clearWidgets();
        init();
    }

    private void startKeyCapture(int index) {
        selectedKeyIndex = index;
        keyButtons[index].setMessage(Component.literal("> Press a key..."));
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (selectedKeyIndex >= 0) {
            int keyCode = event.key();
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                selectedKeyIndex = -1;
                updateKeyButtonTexts();
                return super.keyPressed(event);
            }
            String keyName = glfwKeyToName(keyCode);
            if (keyName == null) return true;
            keys.set(selectedKeyIndex, keyName);
            selectedKeyIndex = -1;
            updateKeyButtonTexts();
            return true;
        }
        if (event.key() == GLFW.GLFW_KEY_ESCAPE) {
            onClose();
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean captured) {
        if (selectedKeyIndex >= 0) {
            selectedKeyIndex = -1;
            updateKeyButtonTexts();
        }
        return super.mouseClicked(event, captured);
    }

    private void updateKeyButtonTexts() {
        for (int i = 0; i < keyButtons.length; i++) {
            String msg = "Key " + (i + 1) + ": " + keys.get(i);
            keyButtons[i].setMessage(Component.literal(msg));
        }
    }

    private void saveAndClose() {
        for (int i = 0; i < keys.size(); i++) {
            commands.set(i, commandFields[i].getValue());
        }
        ModConfig config = ModConfig.get();
        config.binds.clear();
        for (int i = 0; i < keys.size(); i++) {
            if (!keys.get(i).isEmpty()) {
                config.binds.put(keys.get(i), commands.get(i));
            }
        }
        ModConfig.save();
        try {
            ClientKeyHandler.register();
        } catch (Exception e) {
            System.err.println("[NSU] Failed to re-register keybindings: " + e.getMessage());
        }
        onClose();
    }

    private static String glfwKeyToName(int keyCode) {
        if (keyCode >= GLFW.GLFW_KEY_A && keyCode <= GLFW.GLFW_KEY_Z) {
            return String.valueOf((char) ('A' + keyCode - GLFW.GLFW_KEY_A));
        }
        if (keyCode >= GLFW.GLFW_KEY_0 && keyCode <= GLFW.GLFW_KEY_9) {
            return String.valueOf((char) ('0' + keyCode - GLFW.GLFW_KEY_0));
        }
        return switch (keyCode) {
            case GLFW.GLFW_KEY_LEFT_BRACKET -> "[";
            case GLFW.GLFW_KEY_RIGHT_BRACKET -> "]";
            case GLFW.GLFW_KEY_BACKSLASH -> "\\";
            case GLFW.GLFW_KEY_MINUS -> "-";
            case GLFW.GLFW_KEY_EQUAL -> "=";
            case GLFW.GLFW_KEY_COMMA -> ",";
            case GLFW.GLFW_KEY_PERIOD -> ".";
            case GLFW.GLFW_KEY_SEMICOLON -> ";";
            case GLFW.GLFW_KEY_APOSTROPHE -> "'";
            case GLFW.GLFW_KEY_SLASH -> "/";
            case GLFW.GLFW_KEY_GRAVE_ACCENT -> "`";
            case GLFW.GLFW_KEY_SPACE -> "SPACE";
            case GLFW.GLFW_KEY_LEFT_SHIFT -> "SHIFT";
            case GLFW.GLFW_KEY_RIGHT_SHIFT -> "RSHIFT";
            case GLFW.GLFW_KEY_LEFT_CONTROL -> "CTRL";
            case GLFW.GLFW_KEY_RIGHT_CONTROL -> "RCTRL";
            case GLFW.GLFW_KEY_LEFT_ALT -> "ALT";
            case GLFW.GLFW_KEY_RIGHT_ALT -> "RALT";
            case GLFW.GLFW_KEY_TAB -> "TAB";
            case GLFW.GLFW_KEY_ENTER -> "ENTER";
            case GLFW.GLFW_KEY_CAPS_LOCK -> "CAPSLOCK";
            case GLFW.GLFW_KEY_NUM_LOCK -> "NUMLOCK";
            case GLFW.GLFW_KEY_SCROLL_LOCK -> "SCROLLLOCK";
            case GLFW.GLFW_KEY_PAUSE -> "PAUSE";
            case GLFW.GLFW_KEY_BACKSPACE -> "BACKSPACE";
            case GLFW.GLFW_KEY_DELETE -> "DELETE";
            case GLFW.GLFW_KEY_INSERT -> "INSERT";
            case GLFW.GLFW_KEY_HOME -> "HOME";
            case GLFW.GLFW_KEY_END -> "END";
            case GLFW.GLFW_KEY_PAGE_UP -> "PAGEUP";
            case GLFW.GLFW_KEY_PAGE_DOWN -> "PAGEDOWN";
            case GLFW.GLFW_KEY_UP -> "UP";
            case GLFW.GLFW_KEY_DOWN -> "DOWN";
            case GLFW.GLFW_KEY_LEFT -> "LEFT";
            case GLFW.GLFW_KEY_RIGHT -> "RIGHT";
            case GLFW.GLFW_KEY_F1 -> "F1";
            case GLFW.GLFW_KEY_F2 -> "F2";
            case GLFW.GLFW_KEY_F3 -> "F3";
            case GLFW.GLFW_KEY_F4 -> "F4";
            case GLFW.GLFW_KEY_F5 -> "F5";
            case GLFW.GLFW_KEY_F6 -> "F6";
            case GLFW.GLFW_KEY_F7 -> "F7";
            case GLFW.GLFW_KEY_F8 -> "F8";
            case GLFW.GLFW_KEY_F9 -> "F9";
            case GLFW.GLFW_KEY_F10 -> "F10";
            case GLFW.GLFW_KEY_F11 -> "F11";
            case GLFW.GLFW_KEY_F12 -> "F12";
            case GLFW.GLFW_KEY_MENU -> "MENU";
            default -> null;
        };
    }
}