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
import me.voguh.unichat.adapter.util.JSONParser;
import me.voguh.unichat.adapter.worker.RawWorker;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.List;

public record SaveWorkersPayload(List<RawWorker> workers) implements CustomPacketPayload {

    public static final Type<SaveWorkersPayload> TYPE = new Type<>(IdentifierUtils.getIdentifier("save_workers"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SaveWorkersPayload> CODEC = ByteBufCodecs.STRING_UTF8
        .map(SaveWorkersPayload::fromJson, SaveWorkersPayload::toJson)
        .cast();

    private static SaveWorkersPayload fromJson(String json) {
        return new SaveWorkersPayload(JSONParser.fromJson(json, RawWorker.TYPE));
    }

    private static String toJson(SaveWorkersPayload payload) {
        return JSONParser.toJson(payload.workers());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
