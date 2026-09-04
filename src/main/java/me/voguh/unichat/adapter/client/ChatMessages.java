/*!******************************************************************************
 * Copyright (c) 2026 Voguh
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

package me.voguh.unichat.adapter.client;

import me.voguh.unichat.adapter.network.ChatMessagePayload;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.regex.Pattern;

public final class ChatMessages {

    private static final Pattern COLOR_PATTERN = Pattern.compile("^#[0-9a-fA-F]{6}$");
    private static final int DEFAULT_COLOR = 0xFFFFFF;

    public static void accept(ChatMessagePayload payload) {
        if (!ClientConfig.renderMessages()) {
            return;
        }

        Component author = buildAuthor(payload.authorDisplayName(), payload.authorDisplayColor());
        Component line = Component.empty().append(author).append(": ").append(withoutFormatting(payload.messageText()));

        Minecraft.getInstance().gui.getChat().addMessage(line);
    }

    /* ====================================================================== */

    private static Component buildAuthor(String name, String color) {
        return Component.literal(name).withStyle(style -> style.withColor(parseAuthorColor(color)));
    }

    private static int parseAuthorColor(String raw) {
        if (!COLOR_PATTERN.matcher(raw).matches()) {
            return DEFAULT_COLOR;
        }

        return Integer.parseInt(raw.substring(1), 16);
    }

    private static String withoutFormatting(String raw) {
        return raw.replace(ChatFormatting.PREFIX_CODE, ' ');
    }

    /* ====================================================================== */

    private ChatMessages() {
        throw new IllegalStateException("Utility class");
    }

}
