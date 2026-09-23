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

import me.voguh.unichat.adapter.util.IdentifierUtils;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiConsumer;
import java.util.function.Predicate;

public final class CustomEditBox extends LinearLayout {

    private static final int DEFAULT_MAX_LENGTH = 32;

    private final SpriteEditBox editBox;

    /* ====================================================================== */

    public CustomEditBox(Font font, int width, Component message, String initialValue, BiConsumer<CustomEditBox, String> onChange) {
        this(font, 0, 0, width, message, initialValue, onChange, DEFAULT_MAX_LENGTH);
    }

    public CustomEditBox(Font font, int x, int y, int width, Component message, String initialValue, BiConsumer<CustomEditBox, String> onChange) {
        this(font, x, y, width, message, initialValue, onChange, DEFAULT_MAX_LENGTH);
    }

    public CustomEditBox(Font font, int width, Component message, String initialValue, BiConsumer<CustomEditBox, String> onChange, int maxLength) {
        this(font, 0, 0, width, message, initialValue, onChange, maxLength);
    }

    public CustomEditBox(Font font, int x, int y, int width, Component message, String initialValue, BiConsumer<CustomEditBox, String> onChange, int maxLength) {
        super(0, 0, Orientation.VERTICAL);
        spacing(0);
        addChild(new StringWidget(width, font.lineHeight, message, font).alignLeft());

        editBox = new SpriteEditBox(font, x, y, width, message);
        editBox.setMaxLength(maxLength);
        editBox.setValue(initialValue);
        editBox.setResponder((text) -> onChange.accept(this, text));

        // Unbordered EditBox has no built-in text inset; give the padding back here.
        addChild(editBox, (settings) -> settings.paddingHorizontal(SpriteEditBox.PADDING_X).paddingVertical(SpriteEditBox.PADDING_Y));
    }

    /* ====================================================================== */

    public void setFilter(Predicate<String> filter) {
        editBox.setFilter(filter);
    }

    /* ====================================================================== */

    private static final class SpriteEditBox extends EditBox {

        private static final ResourceLocation BOX = IdentifierUtils.getIdentifier("editbox/background");
        private static final int PADDING_X = 6;
        private static final int PADDING_Y = 6;

        /* ================================================================== */

        private SpriteEditBox(Font font, int x, int y, int width, Component message) {
            super(font, x + PADDING_X, y + PADDING_Y, width - PADDING_X * 2, Button.DEFAULT_HEIGHT - PADDING_Y * 2, message);
            setBordered(false);
            setTextColor(0xFFF0F0F1);
        }

        @Override
        public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            int x = getX() - PADDING_X;
            int y = getY() - PADDING_Y;
            int width = getWidth() + PADDING_X * 2;
            int height = getHeight() + PADDING_Y * 2;
            graphics.blitSprite(BOX, x, y, width, height);

            super.renderWidget(graphics, mouseX, mouseY, partialTick);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (!isMouseOver(mouseX, mouseY)) {
                return false;
            }

            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public boolean isMouseOver(double mouseX, double mouseY) {
            int x = getX();
            int y = getY();

            return active && visible
                && mouseX >= x - PADDING_X && mouseX < x + getWidth() + PADDING_X
                && mouseY >= y - PADDING_Y && mouseY < y + getHeight() + PADDING_Y;
        }

    }

}
