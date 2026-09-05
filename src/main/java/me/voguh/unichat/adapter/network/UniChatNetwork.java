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
import me.voguh.unichat.adapter.client.ClientServerSettingsHolder;
import me.voguh.unichat.adapter.network.packet.client.UpdateServerSettingsPayload;
import me.voguh.unichat.adapter.network.packet.server.SendChatMessagePayload;
import me.voguh.unichat.adapter.network.packet.server.SendConnectionStatusPayload;
import me.voguh.unichat.adapter.network.packet.server.SendServerSettingsPayload;
import me.voguh.unichat.adapter.server.MinecraftServerHolder;
import me.voguh.unichat.adapter.server.ServerConfig;
import me.voguh.unichat.adapter.ws.UniChatWebSocket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;
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
            .clientbound()
            .addMain(SendChatMessagePayload.TYPE, SendChatMessagePayload.CODEC, this::onChatMessage)
            .addMain(SendConnectionStatusPayload.TYPE, SendConnectionStatusPayload.CODEC, this::onConnectionStatus)
            .addMain(SendServerSettingsPayload.TYPE, SendServerSettingsPayload.CODEC, this::onServerSettings)
            .serverbound()
            .addMain(UpdateServerSettingsPayload.TYPE, UpdateServerSettingsPayload.CODEC, this::updateServerSettings)
            .build();
    }

    /* <=======================================[ SERVER ]=======================================> */
    public void sendToPlayers(CustomPacketPayload payload) {
        if (channel == null) {
            throw new IllegalStateException("Channel is not registered");
        }

        MinecraftServerHolder.execute((server) -> {
            List<Connection> targets = server.getPlayerList().getPlayers().stream().filter(this::hasChannel)
                .map(p -> p.connection.getConnection()).toList();

            channel.send(payload, PacketDistributor.NMLIST.with(targets));
        });
    }

    private boolean hasChannel(ServerPlayer player) {
        if (channel == null) {
            throw new IllegalStateException("Channel is not registered");
        }

        return channel.isRemotePresent(player.connection.getConnection());
    }

    public void sendToPlayer(ServerPlayer player, CustomPacketPayload payload) {
        if (channel == null) {
            throw new IllegalStateException("Channel is not registered");
        } else if (!hasChannel(player)) {
            return;
        }

        channel.send(payload, PacketDistributor.PLAYER.with(player));
    }

    private void updateServerSettings(UpdateServerSettingsPayload payload, CustomPayloadEvent.Context context) {
        ServerPlayer player = context.getSender();
        if (player == null) {
            throw new IllegalStateException("Method must be called from the server");
        } else if (!player.permissions().hasPermission(Permissions.COMMANDS_ADMIN)) {
            return;
        }

        ServerConfig.updateSettings(payload.websocketUrl(), payload.autoConnect());
        sendToPlayers(new SendServerSettingsPayload(payload.websocketUrl(), payload.autoConnect()));

        if (!UniChatWebSocket.INSTANCE.isConnected() && payload.autoConnect()) {
            UniChatWebSocket.INSTANCE.connect(payload.websocketUrl());
        }
    }
    /* <=====================================[ END SERVER ]=====================================> */

    /* <=======================================[ CLIENT ]=======================================> */
    public void sendToServer(CustomPacketPayload payload) {
        if (channel == null) {
            throw new IllegalStateException("Channel is not registered");
        }

        channel.send(payload, PacketDistributor.SERVER.noArg());
    }

    private void onChatMessage(SendChatMessagePayload payload, CustomPayloadEvent.Context context) {
        ChatMessages.accept(payload);
    }

    private void onConnectionStatus(SendConnectionStatusPayload payload, CustomPayloadEvent.Context context) {
        Gui gui = Minecraft.getInstance().gui;
        String prefix = "actionbar.unichat_adapter";

        switch (payload.connectionStatus()) {
            case CONNECTING -> gui.setOverlayMessage(Component.translatable(prefix + ".connecting"), false);
            case CONNECTED -> gui.setOverlayMessage(Component.translatable(prefix + ".connected"), false);
            case DISCONNECTED -> gui.setOverlayMessage(Component.translatable(prefix + ".disconnected"), false);
        }
    }

    private void onServerSettings(SendServerSettingsPayload payload, CustomPayloadEvent.Context context) {
        ClientServerSettingsHolder.INSTANCE.set(payload.websocketUrl(), payload.autoConnect());
    }
    /* <=====================================[ END CLIENT ]=====================================> */

}
