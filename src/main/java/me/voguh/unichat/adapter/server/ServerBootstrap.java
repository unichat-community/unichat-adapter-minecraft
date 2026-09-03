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

import me.voguh.unichat.adapter.config.CommonConfig;
import me.voguh.unichat.adapter.server.dispatch.ConnectionNoticeDispatch;
import me.voguh.unichat.adapter.worker.Workers;
import me.voguh.unichat.adapter.ws.UniChatWebSocket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;

public class ServerBootstrap {

    public static void onServerStarted(ServerStartedEvent event) {
        ServerEventHandler.INSTANCE.setServer(event.getServer());
        Workers.INSTANCE.reload();

        if (CommonConfig.autoConnect()) {
            UniChatWebSocket.INSTANCE.connect(CommonConfig.websocketUrl(), CommonConfig.autoConnect());
        }
    }

    public static void onServerStopping(ServerStoppingEvent event) {
        UniChatWebSocket.INSTANCE.disconnect();
    }

    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (UniChatWebSocket.INSTANCE.isConnected() && event.getEntity() instanceof ServerPlayer player) {
            ConnectionNoticeDispatch.connected(player);
        }
    }

    /* ====================================================================== */

    private ServerBootstrap() {
        throw new IllegalStateException("Utility class");
    }

}
