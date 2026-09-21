/*!******************************************************************************
 * Copyright (c) 2026 Voguh
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

package me.voguh.unichat.adapter.gui.chat;

import me.voguh.unichat.adapter.gui.chat.image.ImageTexture;
import me.voguh.unichat.adapter.gui.chat.image.ImageTextures;
import me.voguh.unichat.adapter.util.IdentifierUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

public final class InlineImageRenderer {

    private static final ResourceLocation MISSING = IdentifierUtils.getIdentifier("textures/gui/placeholder.png");
    private static final int MISSING_SIZE = 16;

    public static void render(GuiGraphics graphics, int x, int y, float alpha, FormattedCharSequence content) {
        if (hasNoPlaceholder(content)) {
            return;
        }

        Cursor cursor = new Cursor(Minecraft.getInstance().font.getSplitter(), x);

        content.accept((index, style, codepoint) -> {
            if (!ImageFont.isPlaceholder(style, codepoint)) {
                cursor.append(style, codepoint);

                return true;
            }

            int width = ImageFont.width(codepoint);
            draw(graphics, cursor.mark(), y, width, ImageFont.kind(codepoint).height(), alpha, style);
            cursor.skip(width);

            return true;
        });
    }

    private static boolean hasNoPlaceholder(FormattedCharSequence content) {
        return content.accept((index, style, codepoint) -> !ImageFont.isPlaceholder(style, codepoint));
    }

    private static void draw(GuiGraphics graphics, int x, int y, int width, int height, float alpha, Style style) {
        int top = y - (height - ImageFont.TEXT_LINE_HEIGHT) / 2;

        graphics.setColor(1.0F, 1.0F, 1.0F, alpha);

        ImageTexture texture = ImageTextures.INSTANCE.get(style.getInsertion());
        if (texture == null) {
            int size = MISSING_SIZE;

            graphics.blit(MISSING, x, top, width, height, 0.0F, 0.0F, size, size, size, size);
        } else {
            int srcW = texture.width();
            int srcH = texture.height();
            float v = texture.frameOffset();

            graphics.blit(texture.id(), x, top, width, height, 0.0F, v, srcW, srcH, srcW, texture.atlasHeight());
        }

        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private static final class Cursor {

        private final StringSplitter splitter;
        private final StringBuilder pending;
        private Style style = Style.EMPTY;
        private float x;

        Cursor(StringSplitter splitter, float x) {
            this.splitter = splitter;
            this.pending = new StringBuilder();
            this.x = x;
        }

        void append(Style style, int codepoint) {
            if (this.style != style) {
                flush();
                this.style = style;
            }

            pending.appendCodePoint(codepoint);
        }

        int mark() {
            flush();

            return Math.round(x);
        }

        void skip(int width) {
            x += width;
        }

        private void flush() {
            if (pending.isEmpty()) {
                return;
            }

            x += splitter.stringWidth(FormattedCharSequence.forward(pending.toString(), style));
            pending.setLength(0);
        }

    }

    /* ====================================================================== */

    private InlineImageRenderer() {
        throw new UnsupportedOperationException("Utility class");
    }

}
