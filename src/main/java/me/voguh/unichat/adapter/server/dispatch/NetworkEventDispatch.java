/*!******************************************************************************
 * Copyright (c) 2026 Voguh
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

package me.voguh.unichat.adapter.server.dispatch;

import me.voguh.unichat.adapter.event.UniChatEvent;
import me.voguh.unichat.adapter.event.UniChatEventMessage;
import me.voguh.unichat.adapter.network.ChatImage;
import me.voguh.unichat.adapter.network.ChatMessagePayload;
import me.voguh.unichat.adapter.network.UniChatNetwork;
import net.minecraft.server.MinecraftServer;

import java.util.List;

public final class NetworkEventDispatch {

    public static void dispatch(MinecraftServer server, UniChatEvent event) {
        if (!(event instanceof UniChatEventMessage message)) {
            return;
        }

        List<ChatImage> badges = message.authorBadges().stream().map(badge -> new ChatImage(badge.code(), badge.url())).toList();
        List<ChatImage> emotes = message.emotes().stream().map(emote -> new ChatImage(emote.code(), emote.url())).toList();

        ChatMessagePayload payload = new ChatMessagePayload(
            message.authorDisplayName(),
            message.authorDisplayColor(),
            message.messageText(),
            badges,
            emotes
        );

        server.execute(() -> UniChatNetwork.INSTANCE.broadcast(server, payload));
    }

    /* ====================================================================== */

    private NetworkEventDispatch() {
        throw new IllegalStateException("Utility class");
    }

}
