/*!******************************************************************************
 * Copyright (c) 2026 Voguh
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

package me.voguh.unichat.adapter.network.packet.client;

import me.voguh.unichat.adapter.UniChatAdapter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record RequestImagePayload(String path) implements CustomPacketPayload {

    public static final Type<RequestImagePayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(UniChatAdapter.MODID, "request_image"));
    public static final StreamCodec<RegistryFriendlyByteBuf, RequestImagePayload> CODEC = StreamCodec.composite(
        ByteBufCodecs.STRING_UTF8,
        RequestImagePayload::path,
        RequestImagePayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
