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

import me.voguh.unichat.adapter.client.ServerStateHolder;
import me.voguh.unichat.adapter.gui.UniChatToast;
import me.voguh.unichat.adapter.gui.chat.ChatMessages;
import me.voguh.unichat.adapter.network.packet.client.RequestImagePayload;
import me.voguh.unichat.adapter.network.packet.client.ToggleWebSocketConnectionPayload;
import me.voguh.unichat.adapter.network.packet.client.UpdateServerSettingsPayload;
import me.voguh.unichat.adapter.network.packet.server.SendChatMessagePayload;
import me.voguh.unichat.adapter.network.packet.server.SendConnectionStatusPayload;
import me.voguh.unichat.adapter.network.packet.server.SendImageBytesPayload;
import me.voguh.unichat.adapter.network.packet.server.SendServerSettingsPayload;
import me.voguh.unichat.adapter.network.packet.server.SendWorkersPayload;
import me.voguh.unichat.adapter.server.ServerConfig;
import me.voguh.unichat.adapter.store.ImageRequests;
import me.voguh.unichat.adapter.util.ConnectionStatus;
import me.voguh.unichat.adapter.ws.UniChatWebSocket;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public final class NetworkBootstrap {

    private static final String STATUS_PREFIX = "gui.unichat_adapter.status_";
    private static final String PROTOCOL_VERSION = "1";

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar channel = event.registrar(PROTOCOL_VERSION).optional();
        channel.playToClient(SendConnectionStatusPayload.TYPE, SendConnectionStatusPayload.CODEC, NetworkBootstrap::onConnectionStatus);
        channel.playToClient(SendServerSettingsPayload.TYPE, SendServerSettingsPayload.CODEC, NetworkBootstrap::onServerSettings);
        channel.playToClient(SendWorkersPayload.TYPE, SendWorkersPayload.CODEC, NetworkBootstrap::onWorkers);
        channel.playToClient(SendChatMessagePayload.TYPE, SendChatMessagePayload.CODEC, NetworkBootstrap::onChatMessage);
        channel.playToClient(SendImageBytesPayload.TYPE, SendImageBytesPayload.CODEC, NetworkBootstrap::onImageReceive);
        channel.playToServer(UpdateServerSettingsPayload.TYPE, UpdateServerSettingsPayload.CODEC, NetworkBootstrap::updateServerSettings);
        channel.playToServer(ToggleWebSocketConnectionPayload.TYPE, ToggleWebSocketConnectionPayload.CODEC, NetworkBootstrap::toggleWebSocketConnection);
        channel.playToServer(RequestImagePayload.TYPE, RequestImagePayload.CODEC, NetworkBootstrap::onImageRequest);
    }

    /* <=======================================[ SERVER ]=======================================> */
    private static void updateServerSettings(UpdateServerSettingsPayload payload, IPayloadContext context) {
        ServerPlayer player = (ServerPlayer) context.player();
        if (!player.hasPermissions(Commands.LEVEL_ADMINS)) {
            return;
        }

        ServerConfig.updateSettings(payload.websocketUrl(), payload.autoConnect());
        SendServerSettingsPayload packet = new SendServerSettingsPayload(payload.websocketUrl(), payload.autoConnect());
        UniChatNetwork.sendToPlayers(packet);

        if (!UniChatWebSocket.INSTANCE.isConnected() && payload.autoConnect()) {
            UniChatWebSocket.INSTANCE.connect(payload.websocketUrl());
        }
    }

    private static void toggleWebSocketConnection(ToggleWebSocketConnectionPayload payload, IPayloadContext context) {
        ServerPlayer player = (ServerPlayer) context.player();
        if (!player.hasPermissions(Commands.LEVEL_ADMINS)) {
            return;
        }

        if (payload.state()) {
            UniChatWebSocket.INSTANCE.connect(ServerConfig.websocketUrl());
        } else {
            UniChatWebSocket.INSTANCE.disconnect();
        }
    }

    private static void onImageRequest(RequestImagePayload payload, IPayloadContext context) {
        ServerPlayer player = (ServerPlayer) context.player();

        CompletableFuture.runAsync(() -> {
            try {
                byte[] fetch = ImageRequests.INSTANCE.serverFetch(payload.path());
                UniChatNetwork.sendToPlayer(player, new SendImageBytesPayload(payload.path(), fetch));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
    /* <=====================================[ END SERVER ]=====================================> */

    /* <=======================================[ CLIENT ]=======================================> */
    private static void onConnectionStatus(SendConnectionStatusPayload payload, IPayloadContext context) {
        ServerStateHolder.INSTANCE.setConnectionStatus(payload.status());

        Component msg = null;
        if (payload.status() == ConnectionStatus.CONNECTED) {
            msg = Component.translatable(STATUS_PREFIX + "connected");
        } else if (payload.status() == ConnectionStatus.DISCONNECTED) {
            msg = Component.translatable(STATUS_PREFIX + "disconnected");
        }

        if (msg == null) {
            return;
        }

        UniChatToast toast = new UniChatToast(Component.literal("UniChat"), msg);
        Minecraft.getInstance().getToasts().addToast(toast);
    }

    private static void onServerSettings(SendServerSettingsPayload payload, IPayloadContext context) {
        ServerStateHolder.INSTANCE.setSettings(payload.websocketUrl(), payload.autoConnect());
    }

    private static void onWorkers(SendWorkersPayload payload, IPayloadContext context) {
        ServerStateHolder.INSTANCE.setWorkers(payload.workers());
    }

    private static void onChatMessage(SendChatMessagePayload payload, IPayloadContext context) {
        ChatMessages.INSTANCE.accept(payload);
    }

    private static void onImageReceive(SendImageBytesPayload payload, IPayloadContext context) {
        ImageRequests.INSTANCE.onServerImageResponse(payload.path(), payload.data());
    }
    /* <=====================================[ END CLIENT ]=====================================> */

    /* ====================================================================== */

    private NetworkBootstrap() {
        throw new UnsupportedOperationException("Utility class");
    }

}
