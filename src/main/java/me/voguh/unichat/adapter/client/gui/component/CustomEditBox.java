/*!******************************************************************************
 * Copyright (c) 2026 Voguh
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

package me.voguh.unichat.adapter.client.gui.component;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;
import java.util.function.Predicate;

public final class CustomEditBox extends LinearLayout {

    private final EditBox editBox;

    /* ====================================================================== */

    public CustomEditBox(Font font, int width, Component label, String initialValue, Consumer<String> onChange) {
        this(font, 0, 0, width, label, initialValue, onChange);
    }

    public CustomEditBox(Font font, int x, int y, int width, Component label, String initialValue, Consumer<String> onChange) {
        super(0, 0, Orientation.VERTICAL);
        spacing(0);
        addChild(new StringWidget(width, font.lineHeight, label, font).alignLeft());

        editBox = new EditBox(font, x, y, width, Button.DEFAULT_HEIGHT, label);
        editBox.setValue(initialValue);
        editBox.setResponder(onChange);
        addChild(editBox);
    }

    /* ====================================================================== */

    public void setMaxLength(int maxLength) {
        editBox.setMaxLength(maxLength);
    }

    public void setFilter(Predicate<String> filter) {
        editBox.setFilter(filter);
    }

}
