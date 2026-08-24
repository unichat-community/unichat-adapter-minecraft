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
import me.voguh.unichat.adapter.ws.UniChatWebSocket;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;

public class ServerBootstrap {

    public static void onServerStarted(ServerStartedEvent event) {
        if (CommonConfig.autoConnect()) {
            UniChatWebSocket.INSTANCE.connect(CommonConfig.websocketUrl(), CommonConfig.autoConnect());
        }
    }

    public static void onServerStopping(ServerStoppingEvent event) {
        UniChatWebSocket.INSTANCE.disconnect();
    }

    /* ====================================================================== */

    private ServerBootstrap() {
        throw new IllegalStateException("Utility class");
    }

}
