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
import me.voguh.unichat.adapter.gui.chat.ChatMessages;
import me.voguh.unichat.adapter.client.ServerStateHolder;
import me.voguh.unichat.adapter.gui.UniChatToast;
import me.voguh.unichat.adapter.network.packet.client.ToggleWebSocketConnectionPayload;
import me.voguh.unichat.adapter.network.packet.client.UpdateServerSettingsPayload;
import me.voguh.unichat.adapter.network.packet.server.SendChatMessagePayload;
import me.voguh.unichat.adapter.network.packet.server.SendConnectionStatusPayload;
import me.voguh.unichat.adapter.network.packet.server.SendServerSettingsPayload;
import me.voguh.unichat.adapter.server.MinecraftServerHolder;
import me.voguh.unichat.adapter.server.ServerConfig;
import me.voguh.unichat.adapter.util.ConnectionStatus;
import me.voguh.unichat.adapter.ws.UniChatWebSocket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.ToastManager;
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

    private static final String STATUS_PREFIX = "gui.unichat_adapter.status_";

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
            .addMain(ToggleWebSocketConnectionPayload.TYPE, ToggleWebSocketConnectionPayload.CODEC, this::toggleWebSocketConnection)
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

    private void toggleWebSocketConnection(ToggleWebSocketConnectionPayload payload, CustomPayloadEvent.Context context) {
        ServerPlayer player = context.getSender();
        if (player == null) {
            throw new IllegalStateException("Method must be called from the server");
        } else if (!player.permissions().hasPermission(Permissions.COMMANDS_ADMIN)) {
            return;
        }

        if (payload.state()) {
            UniChatWebSocket.INSTANCE.connect(ServerConfig.websocketUrl());
        } else {
            UniChatWebSocket.INSTANCE.disconnect();
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
        ChatMessages.INSTANCE.accept(payload);
    }

    private void onConnectionStatus(SendConnectionStatusPayload payload, CustomPayloadEvent.Context context) {
        ServerStateHolder.INSTANCE.setConnectionStatus(payload.status());

        Component msg = null;
        if (payload.status() == ConnectionStatus.CONNECTED) {
            msg = Component.translatable(STATUS_PREFIX + "connected");
        } else if (payload.status() == ConnectionStatus.DISCONNECTED) {
            msg = Component.translatable(STATUS_PREFIX + "disconnected");
        }

        if (msg != null) {
            ToastManager toastManager = Minecraft.getInstance().getToastManager();
            UniChatToast toast = new UniChatToast(Component.literal("UniChat"), msg);
            toastManager.addToast(toast);
        }
    }

    private void onServerSettings(SendServerSettingsPayload payload, CustomPayloadEvent.Context context) {
        ServerStateHolder.INSTANCE.setSettings(payload.websocketUrl(), payload.autoConnect());
    }
    /* <=====================================[ END CLIENT ]=====================================> */

}
