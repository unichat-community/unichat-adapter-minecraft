package me.voguh.unichat.adapter.client.gui.component;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.network.chat.Component;

import java.util.function.BiConsumer;

public class CustomCheckbox extends Checkbox {

    public CustomCheckbox(Font font, int width, Component label, Boolean initialValue, BiConsumer<Checkbox, Boolean> onChange) {
        this(font, 0, 0, width, label, initialValue, onChange);
    }

    public CustomCheckbox(Font font, int x, int y, int width, Component label, Boolean initialValue, BiConsumer<Checkbox, Boolean> onChange) {
        super(x, y, width, label, font, initialValue, onChange::accept);
    }

}
