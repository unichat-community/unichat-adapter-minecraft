/*!******************************************************************************
 * Copyright (c) 2026 Voguh
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

package me.voguh.unichat.adapter.server;

import me.voguh.unichat.adapter.network.UniChatNetwork;
import me.voguh.unichat.adapter.network.packet.server.SendConnectionStatusPayload;
import me.voguh.unichat.adapter.network.packet.server.SendServerSettingsPayload;
import me.voguh.unichat.adapter.network.packet.server.SendWorkersPayload;
import me.voguh.unichat.adapter.util.ConnectionStatus;
import me.voguh.unichat.adapter.worker.Workers;
import me.voguh.unichat.adapter.ws.UniChatWebSocket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;

public final class ServerBootstrap {

    public static void onServerStarted(ServerStartedEvent event) {
        MinecraftServerHolder.set(event.getServer());
        Workers.INSTANCE.reload();

        if (ServerConfig.autoConnect()) {
            UniChatWebSocket.INSTANCE.connect(ServerConfig.websocketUrl());
        }
    }

    public static void onServerStopping(ServerStoppingEvent event) {
        UniChatWebSocket.INSTANCE.disconnect();
    }

    public static void onServerStopped(ServerStoppedEvent event) {
        MinecraftServerHolder.set(null);
    }

    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ConnectionStatus status = UniChatWebSocket.INSTANCE.isConnected() ? ConnectionStatus.CONNECTED : ConnectionStatus.DISCONNECTED;
            UniChatNetwork.INSTANCE.sendToPlayer(player, new SendConnectionStatusPayload(status));
            UniChatNetwork.INSTANCE.sendToPlayer(player, new SendServerSettingsPayload(ServerConfig.websocketUrl(), ServerConfig.autoConnect()));
            UniChatNetwork.INSTANCE.sendToPlayer(player, new SendWorkersPayload(Workers.INSTANCE.rawWorkers()));
        }
    }

    /* ====================================================================== */

    private ServerBootstrap() {
        throw new UnsupportedOperationException("Utility class");
    }

}
