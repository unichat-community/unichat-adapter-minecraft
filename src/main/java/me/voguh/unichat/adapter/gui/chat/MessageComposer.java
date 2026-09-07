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
import me.voguh.unichat.adapter.network.ChatImage;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

import java.util.List;

public final class MessageComposer {

    public static void emit(ChatMessage message) {
        MutableComponent line = Component.empty();

        for (ChatImage badge : message.badges()) {
            line.append(imageOrCode(badge.code(), badge.url(), ImageFont.Kind.BADGE)).append(" ");
        }

        line.append(message.author()).append(": ").append(buildBody(message.segments()));

        Minecraft.getInstance().gui.getChat().addMessage(line);
    }

    private static Component buildBody(List<MessageSegment> segments) {
        MutableComponent body = Component.empty();

        for (int i = 0; i < segments.size(); i++) {
            if (i > 0) {
                body.append(" ");
            }

            append(body, segments.get(i));
        }

        return body;
    }

    private static void append(MutableComponent body, MessageSegment segment) {
        switch (segment) {
            case MessageSegment.Text text -> body.append(text.value());
            case MessageSegment.Image image -> body.append(imageOrCode(image.code(), image.url(), ImageFont.Kind.EMOTE));
        }
    }

    private static Component imageOrCode(String code, String url, ImageFont.Kind kind) {
        ImageTexture texture = ImageTextures.INSTANCE.get(url);
        if (texture == null) {
            return Component.literal(code);
        }

        int width = Math.round(kind.height() * texture.width() / (float) texture.height());
        Style style = ImageFont.style(url).withHoverEvent(new HoverEvent.ShowText(Component.literal(code)));

        return Component.literal(ImageFont.placeholder(kind, width)).withStyle(style);
    }

    /* ====================================================================== */

    private MessageComposer() {
        throw new IllegalStateException("Utility class");
    }

}
