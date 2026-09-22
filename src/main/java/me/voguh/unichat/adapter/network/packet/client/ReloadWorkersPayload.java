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
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ReloadWorkersPayload() implements CustomPacketPayload {

    public static final Type<ReloadWorkersPayload> TYPE = new Type<>(IdentifierUtils.getIdentifier("reload_workers"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ReloadWorkersPayload> CODEC = StreamCodec.unit(new ReloadWorkersPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
