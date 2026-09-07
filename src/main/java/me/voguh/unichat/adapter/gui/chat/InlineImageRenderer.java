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

import com.mojang.blaze3d.pipeline.RenderPipeline;
import me.voguh.unichat.adapter.UniChatAdapter;
import me.voguh.unichat.adapter.gui.chat.image.ImageTexture;
import me.voguh.unichat.adapter.gui.chat.image.ImageTextures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;

public final class InlineImageRenderer {

    private static final Identifier MISSING = Identifier.fromNamespaceAndPath(UniChatAdapter.MODID, "textures/gui/placeholder.png");
    private static final RenderPipeline PIPELINE = RenderPipelines.GUI_TEXTURED;
    private static final int MISSING_SIZE = 16;

    public static void render(GuiGraphics graphics, int y, float alpha, FormattedCharSequence content) {
        if (hasNoPlaceholder(content)) {
            return;
        }

        Cursor cursor = new Cursor(Minecraft.getInstance().font.getSplitter());
        int color = ARGB.white(alpha);

        content.accept((index, style, codepoint) -> {
            if (!ImageFont.isPlaceholder(style, codepoint)) {
                cursor.append(style, codepoint);

                return true;
            }

            int width = ImageFont.width(codepoint);
            draw(graphics, cursor.mark(), y, width, ImageFont.kind(codepoint).height(), color, style);
            cursor.skip(width);

            return true;
        });
    }

    private static boolean hasNoPlaceholder(FormattedCharSequence content) {
        return content.accept((index, style, codepoint) -> !ImageFont.isPlaceholder(style, codepoint));
    }

    private static void draw(GuiGraphics graphics, int x, int y, int width, int height, int color, Style style) {
        int top = y - (height - ChatComponent.MESSAGE_BOTTOM_TO_MESSAGE_TOP) / 2;

        ImageTexture texture = ImageTextures.INSTANCE.get(style.getInsertion());
        if (texture == null) {
            int size = MISSING_SIZE;

            graphics.blit(PIPELINE, MISSING, x, top, 0.0F, 0.0F, width, height, size, size, size, size, color);
            return;
        }

        int srcW = texture.width();
        int srcH = texture.height();
        float v = texture.frameOffset();

        graphics.blit(PIPELINE, texture.id(), x, top, 0.0F, v, width, height, srcW, srcH, srcW, texture.atlasHeight(), color);
    }

    private static final class Cursor {

        private final StringSplitter splitter;
        private final StringBuilder pending;
        private Style style = Style.EMPTY;
        private float x;

        Cursor(StringSplitter splitter) {
            this.splitter = splitter;
            this.pending = new StringBuilder();
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
        throw new IllegalStateException("Utility class");
    }

}
