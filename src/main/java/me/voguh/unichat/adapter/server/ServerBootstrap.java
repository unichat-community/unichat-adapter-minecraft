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
import me.voguh.unichat.adapter.server.worker.Workers;
import me.voguh.unichat.adapter.server.ws.UniChatWebSocket;
import me.voguh.unichat.adapter.util.ConnectionStatus;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;

public final class ServerBootstrap {

    public static void register(IEventBus modEventBus, ModContainer container) {
        container.registerConfig(ModConfig.Type.SERVER, ServerConfig.SPEC);

        NeoForge.EVENT_BUS.addListener(ServerBootstrap::onServerStarted);
        NeoForge.EVENT_BUS.addListener(ServerBootstrap::onServerStopping);
        NeoForge.EVENT_BUS.addListener(ServerBootstrap::onServerStopped);
        NeoForge.EVENT_BUS.addListener(ServerBootstrap::onPlayerLoggedIn);
    }

    /* ====================================================================== */

    private static void onServerStarted(ServerStartedEvent event) {
        MinecraftServerHolder.set(event.getServer());
        Workers.INSTANCE.reload();

        if (ServerConfig.autoConnect()) {
            UniChatWebSocket.INSTANCE.connect(ServerConfig.websocketUrl());
        }
    }

    private static void onServerStopping(ServerStoppingEvent event) {
        UniChatWebSocket.INSTANCE.disconnect();
    }

    private static void onServerStopped(ServerStoppedEvent event) {
        MinecraftServerHolder.set(null);
    }

    private static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ConnectionStatus status = UniChatWebSocket.INSTANCE.isConnected() ? ConnectionStatus.CONNECTED : ConnectionStatus.DISCONNECTED;
            UniChatNetwork.sendToPlayer(player, new SendConnectionStatusPayload(status));
            UniChatNetwork.sendToPlayer(player, new SendServerSettingsPayload(ServerConfig.websocketUrl(), ServerConfig.autoConnect()));
            UniChatNetwork.sendToPlayer(player, new SendWorkersPayload(Workers.INSTANCE.rawWorkers()));
        }
    }

    /* ====================================================================== */

    private ServerBootstrap() {
        throw new UnsupportedOperationException("Utility class");
    }

}
