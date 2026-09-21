/*!******************************************************************************
 * Copyright (c) 2026 Voguh
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

package me.voguh.unichat.adapter.network.packet.server;

import io.netty.buffer.ByteBuf;
import me.voguh.unichat.adapter.network.packet.ChatImage;
import me.voguh.unichat.adapter.util.IdentifierUtils;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.List;

public record SendChatMessagePayload(
    String authorDisplayName,
    String authorDisplayColor,
    String messageText,
    List<ChatImage> authorBadges,
    List<ChatImage> emotes
) implements CustomPacketPayload {

    private static final StreamCodec<ByteBuf, List<ChatImage>> IMAGES_CODEC = ChatImage.CODEC.apply(ByteBufCodecs.list());

    public static final Type<SendChatMessagePayload> TYPE = new Type<>(IdentifierUtils.getIdentifier("send_chat_message"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SendChatMessagePayload> CODEC = StreamCodec.composite(
        ByteBufCodecs.STRING_UTF8,
        SendChatMessagePayload::authorDisplayName,
        ByteBufCodecs.STRING_UTF8,
        SendChatMessagePayload::authorDisplayColor,
        ByteBufCodecs.STRING_UTF8,
        SendChatMessagePayload::messageText,
        IMAGES_CODEC,
        SendChatMessagePayload::authorBadges,
        IMAGES_CODEC,
        SendChatMessagePayload::emotes,
        SendChatMessagePayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
