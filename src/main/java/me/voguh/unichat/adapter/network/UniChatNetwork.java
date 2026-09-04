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
import me.voguh.unichat.adapter.client.ChatMessages;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.network.Channel;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.NetworkProtocol;
import net.minecraftforge.network.PacketDistributor;
import org.jspecify.annotations.Nullable;

import java.util.List;

public enum UniChatNetwork {
    INSTANCE;

    private @Nullable Channel<CustomPacketPayload> channel;

    public void register() {
        Identifier identifier = Identifier.fromNamespaceAndPath(UniChatAdapter.MODID, "main");
        channel = ChannelBuilder.named(identifier).optional().payloadChannel().protocol(NetworkProtocol.PLAY)
            .clientbound().addMain(ChatMessagePayload.TYPE, ChatMessagePayload.CODEC, this::onChatMessage)
            .build();
    }

    public void broadcast(MinecraftServer server, CustomPacketPayload payload) {
        if (channel == null) {
            throw new IllegalStateException("Channel is not registered");
        }

        List<Connection> targets = server.getPlayerList().getPlayers().stream().filter(this::hasChannel)
            .map(player -> player.connection.getConnection()).toList();

        channel.send(payload, PacketDistributor.NMLIST.with(targets));
    }

    public boolean hasChannel(ServerPlayer player) {
        if (channel == null) {
            throw new IllegalStateException("Channel is not registered");
        }

        return channel.isRemotePresent(player.connection.getConnection());
    }

    private void onChatMessage(ChatMessagePayload payload, CustomPayloadEvent.Context context) {
        ChatMessages.accept(payload);
    }

}
