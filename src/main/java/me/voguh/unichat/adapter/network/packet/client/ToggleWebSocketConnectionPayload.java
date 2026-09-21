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

import me.voguh.unichat.adapter.util.IdentifierUtils;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ToggleWebSocketConnectionPayload(boolean state) implements CustomPacketPayload {

    public static final Type<ToggleWebSocketConnectionPayload> TYPE = new Type<>(IdentifierUtils.getIdentifier("toggle_websocket_connection"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ToggleWebSocketConnectionPayload> CODEC = StreamCodec.composite(
        ByteBufCodecs.BOOL,
        ToggleWebSocketConnectionPayload::state,
        ToggleWebSocketConnectionPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
