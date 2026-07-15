package com.ninjabowtie.nsu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;

public class NsuConfigScreen extends Screen {
    private static final int BINDINGS = 3;

    private final Screen parent;
    private EditBox[] commandFields;
    private Button[] keyButtons;
    private int selectedKeyIndex = -1;
    private String[] keys;
    private String[] commands;

    protected NsuConfigScreen(Screen parent) {
        super(Component.translatable("nsu.config.title"));
        this.parent = parent;
        ModConfig config = ModConfig.get();
        keys = new String[]{config.key1, config.key2, config.key3};
        commands = new String[]{config.command1, config.command2, config.command3};
    }

    @Override
    protected void init() {
        commandFields = new EditBox[BINDINGS];
        keyButtons = new Button[BINDINGS];
        int centerX = width / 2;

        for (int i = 0; i < BINDINGS; i++) {
            int y = 50 + i * 30;
            int idx = i;

            keyButtons[i] = Button.builder(
                Component.literal("Key " + (i + 1) + ": " + keys[i]),
                btn -> startKeyCapture(idx)
            )
            .bounds(centerX - 155, y, 120, 20)
            .build();
            addRenderableWidget(keyButtons[i]);

            commandFields[i] = new EditBox(
                font, centerX - 25, y, 180, 20,
                Component.translatable("nsu.command" + (i + 1))
            );
            commandFields[i].setValue(commands[i]);
            commandFields[i].setMaxLength(256);
            addRenderableWidget(commandFields[i]);
        }

        addRenderableWidget(Button.builder(
            Component.translatable("nsu.save"),
            btn -> saveAndClose()
        )
        .bounds(centerX - 95, 150, 90, 20)
        .build());

        addRenderableWidget(Button.builder(
            Component.translatable("nsu.cancel"),
            btn -> onClose()
        )
        .bounds(centerX + 5, 150, 90, 20)
        .build());
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
                keyButtons[selectedKeyIndex].setMessage(
                    Component.literal("Key " + (selectedKeyIndex + 1) + ": " + keys[selectedKeyIndex])
                );
                selectedKeyIndex = -1;
                return true;
            }
            String keyName = glfwKeyToName(keyCode);
            if (keyName != null) {
                keys[selectedKeyIndex] = keyName;
                keyButtons[selectedKeyIndex].setMessage(
                    Component.literal("Key " + (selectedKeyIndex + 1) + ": " + keyName)
                );
            }
            selectedKeyIndex = -1;
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean captured) {
        if (selectedKeyIndex >= 0) {
            keyButtons[selectedKeyIndex].setMessage(
                Component.literal("Key " + (selectedKeyIndex + 1) + ": " + keys[selectedKeyIndex])
            );
            selectedKeyIndex = -1;
        }
        return super.mouseClicked(event, captured);
    }

    private void saveAndClose() {
        for (int i = 0; i < BINDINGS; i++) {
            commands[i] = commandFields[i].getValue();
        }
        ModConfig config = ModConfig.get();
        config.key1 = keys[0];
        config.command1 = commands[0];
        config.key2 = keys[1];
        config.command2 = commands[1];
        config.key3 = keys[2];
        config.command3 = commands[2];
        ModConfig.save();
        ClientKeyHandler.register();
        onClose();
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(parent);
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
            case GLFW.GLFW_KEY_SPACE -> "SPACE";
            case GLFW.GLFW_KEY_LEFT_SHIFT -> "SHIFT";
            case GLFW.GLFW_KEY_LEFT_CONTROL -> "CTRL";
            case GLFW.GLFW_KEY_LEFT_ALT -> "ALT";
            case GLFW.GLFW_KEY_TAB -> "TAB";
            case GLFW.GLFW_KEY_ENTER -> "ENTER";
            case GLFW.GLFW_KEY_BACKSPACE -> "BACKSPACE";
            case GLFW.GLFW_KEY_DELETE -> "DELETE";
            case GLFW.GLFW_KEY_INSERT -> "INSERT";
            case GLFW.GLFW_KEY_HOME -> "HOME";
            case GLFW.GLFW_KEY_END -> "END";
            case GLFW.GLFW_KEY_PAGE_UP -> "PAGEUP";
            case GLFW.GLFW_KEY_PAGE_DOWN -> "PAGEDOWN";
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
            default -> null;
        };
    }
}
