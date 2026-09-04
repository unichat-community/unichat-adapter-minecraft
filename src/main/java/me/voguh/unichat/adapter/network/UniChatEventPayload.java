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

import me.voguh.unichat.adapter.UniChatAdapter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record UniChatEventPayload(String raw) implements CustomPacketPayload {

    public static final Type<UniChatEventPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(UniChatAdapter.MODID, "event"));
    public static final StreamCodec<RegistryFriendlyByteBuf, UniChatEventPayload> CODEC = StreamCodec.composite(
        ByteBufCodecs.STRING_UTF8,
        UniChatEventPayload::raw,
        UniChatEventPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
