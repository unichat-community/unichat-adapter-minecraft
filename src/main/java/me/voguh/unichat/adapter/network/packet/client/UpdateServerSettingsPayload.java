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

public record UpdateServerSettingsPayload(String websocketUrl, Boolean autoConnect) implements CustomPacketPayload {

    public static final Type<UpdateServerSettingsPayload> TYPE = new Type<>(IdentifierUtils.getIdentifier("update_server_settings"));
    public static final StreamCodec<RegistryFriendlyByteBuf, UpdateServerSettingsPayload> CODEC = StreamCodec.composite(
        ByteBufCodecs.STRING_UTF8,
        UpdateServerSettingsPayload::websocketUrl,
        ByteBufCodecs.BOOL,
        UpdateServerSettingsPayload::autoConnect,
        UpdateServerSettingsPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
