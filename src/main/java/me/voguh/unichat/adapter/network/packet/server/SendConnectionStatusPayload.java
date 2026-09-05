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
import me.voguh.unichat.adapter.util.ConnectionStatus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record SendConnectionStatusPayload(ConnectionStatus connectionStatus) implements CustomPacketPayload {

    private static final ConnectionStatus[] VALUES = ConnectionStatus.values();

    public static final Type<SendConnectionStatusPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(UniChatAdapter.MODID, "send_connection_status"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SendConnectionStatusPayload> CODEC = StreamCodec.composite(
        ByteBufCodecs.idMapper(i -> VALUES[i], ConnectionStatus::ordinal),
        SendConnectionStatusPayload::connectionStatus,
        SendConnectionStatusPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
