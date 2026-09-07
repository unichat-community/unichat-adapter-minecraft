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

import me.voguh.unichat.adapter.UniChatAdapter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record SendImageBytesPayload(String path, byte[] data) implements CustomPacketPayload {

    // ClientboundCustomPayloadPacket caps the whole packet at 1 MiB.
    public static final int MAX_BYTES = 768 * 1024;

    public static final Type<SendImageBytesPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(UniChatAdapter.MODID, "send_image_bytes"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SendImageBytesPayload> CODEC = StreamCodec.composite(
        ByteBufCodecs.STRING_UTF8,
        SendImageBytesPayload::path,
        ByteBufCodecs.byteArray(MAX_BYTES),
        SendImageBytesPayload::data,
        SendImageBytesPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
