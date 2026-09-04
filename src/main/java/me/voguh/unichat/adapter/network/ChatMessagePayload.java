/*!******************************************************************************
 * Copyright (c) 2026 Voguh
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

package me.voguh.unichat.adapter.network;

import io.netty.buffer.ByteBuf;
import me.voguh.unichat.adapter.UniChatAdapter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.List;

public record ChatMessagePayload(
    String authorDisplayName,
    String authorDisplayColor,
    String messageText,
    List<ChatImage> authorBadges,
    List<ChatImage> emotes
) implements CustomPacketPayload {

    private static final StreamCodec<ByteBuf, List<ChatImage>> IMAGES_CODEC = ChatImage.CODEC.apply(ByteBufCodecs.list());

    public static final Type<ChatMessagePayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(UniChatAdapter.MODID, "chat_message"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ChatMessagePayload> CODEC = StreamCodec.composite(
        ByteBufCodecs.STRING_UTF8,
        ChatMessagePayload::authorDisplayName,
        ByteBufCodecs.STRING_UTF8,
        ChatMessagePayload::authorDisplayColor,
        ByteBufCodecs.STRING_UTF8,
        ChatMessagePayload::messageText,
        IMAGES_CODEC,
        ChatMessagePayload::authorBadges,
        IMAGES_CODEC,
        ChatMessagePayload::emotes,
        ChatMessagePayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
