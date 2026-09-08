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
import me.voguh.unichat.adapter.network.UniChatNetwork;
import me.voguh.unichat.adapter.network.packet.server.SendChatMessagePayload;

import java.util.List;

public final class ChatMessageEventDispatch {

    public static void dispatch(UniChatEvent event) {
        if (!(event instanceof UniChatEventMessage message)) {
            return;
        }

        List<ChatImage> badges = message.authorBadges().stream().map(badge -> new ChatImage(badge.code(), badge.url())).toList();
        List<ChatImage> emotes = message.emotes().stream().map(emote -> new ChatImage(emote.code(), emote.url())).toList();

        SendChatMessagePayload payload = new SendChatMessagePayload(
            message.authorDisplayName(),
            message.authorDisplayColor(),
            message.messageText(),
            badges,
            emotes
        );

        UniChatNetwork.INSTANCE.sendToPlayers(payload);
    }

    /* ====================================================================== */

    private ChatMessageEventDispatch() {
        throw new UnsupportedOperationException("Utility class");
    }

}
