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

import me.voguh.unichat.adapter.UniChatAdapter;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public final class ImageFont {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(UniChatAdapter.MODID, "image");

    public enum Kind {
        EMOTE(0xE000, 12),
        BADGE(0xE100, ChatComponent.MESSAGE_BOTTOM_TO_MESSAGE_TOP);

        private final int base;
        private final int height;

        Kind(int base, int height) {
            this.base = base;
            this.height = height;
        }

        public int height() {
            return height;
        }

    }

    public static final int MAX_HEIGHT = Math.max(Kind.EMOTE.height, Kind.BADGE.height);

    private static final FontDescription DESCRIPTION = new FontDescription.Resource(ID);
    private static final int MAX_WIDTH = 256;

    public static Style style(String insertion) {
        return Style.EMPTY.withFont(DESCRIPTION).withInsertion(insertion);
    }

    public static String placeholder(Kind kind, int width) {
        return Character.toString(kind.base + Mth.clamp(width, 1, MAX_WIDTH) - 1);
    }

    public static boolean isPlaceholder(Style style, int codepoint) {
        if (codepoint < Kind.EMOTE.base || codepoint > Kind.BADGE.base + MAX_WIDTH - 1) {
            return false;
        }

        return style.getFont() instanceof FontDescription.Resource resource && ID.equals(resource.id());
    }

    public static Kind kind(int codepoint) {
        return codepoint < Kind.BADGE.base ? Kind.EMOTE : Kind.BADGE;
    }

    public static int width(int codepoint) {
        return codepoint - kind(codepoint).base + 1;
    }

    /* ====================================================================== */

    private ImageFont() {
        throw new UnsupportedOperationException("Utility class");
    }

}
