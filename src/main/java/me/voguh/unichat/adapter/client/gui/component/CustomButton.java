package me.voguh.unichat.adapter.client.gui.component;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class CustomButton extends Button {

    public CustomButton(int x, int y, int width, Component message, OnPress onPress) {
        super(x, y, width, 20, message, onPress, Button.DEFAULT_NARRATION);
    }

    public CustomButton(int width, Component message, OnPress onPress) {
        super(0, 0, width, 20, message, onPress, Button.DEFAULT_NARRATION);
    }

}
