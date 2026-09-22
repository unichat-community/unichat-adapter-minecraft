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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.voguh.unichat.adapter.event.UniChatEvent;
import me.voguh.unichat.adapter.server.dispatch.ChatMessageEventDispatch;
import me.voguh.unichat.adapter.server.dispatch.ConnectionStatusDispatch;
import me.voguh.unichat.adapter.server.dispatch.ServerCommandDispatch;
import me.voguh.unichat.adapter.util.ConnectionStatus;
import me.voguh.unichat.adapter.util.JSONParser;
import me.voguh.unichat.adapter.worker.Workers;
import net.minecraft.server.MinecraftServer;

public enum ServerEventHandler {
    INSTANCE;

    private static final String TYPE_CONNECTED = "unichat:connected";
    private static final String TYPE_HISTORY = "unichat:history";

    /* ====================================================================== */

    public void handleConnectionStatus(ConnectionStatus status) {
        ConnectionStatusDispatch.dispatch(status);
    }

    public void handleEvent(String raw) {
        MinecraftServer server = MinecraftServerHolder.getInstance();
        JsonObject envelope = JsonParser.parseString(raw).getAsJsonObject();

        String eventType = envelope.get("type").getAsString();
        if (eventType.equals(TYPE_CONNECTED) || eventType.equals(TYPE_HISTORY)) {
            return;
        }

        UniChatEvent eventData = JSONParser.fromJson(envelope, UniChatEvent.class);
        if (eventData == null) {
            return;
        }

        ChatMessageEventDispatch.dispatch(eventData);
        ServerCommandDispatch.dispatch(Workers.INSTANCE.commandsFor(eventType, eventData));
    }

}
