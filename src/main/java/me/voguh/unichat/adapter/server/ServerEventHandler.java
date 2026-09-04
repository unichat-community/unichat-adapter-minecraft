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
import me.voguh.unichat.adapter.server.dispatch.ConnectionNoticeDispatch;
import me.voguh.unichat.adapter.server.dispatch.NetworkEventDispatch;
import me.voguh.unichat.adapter.server.dispatch.ServerCommandDispatch;
import me.voguh.unichat.adapter.util.JSONParser;
import me.voguh.unichat.adapter.worker.Workers;
import net.minecraft.server.MinecraftServer;
import org.jspecify.annotations.Nullable;

public enum ServerEventHandler {
    INSTANCE;

    private static final String TYPE_CONNECTED = "unichat:connected";
    private static final String TYPE_HISTORY = "unichat:history";

    private @Nullable MinecraftServer server;

    void setServer(MinecraftServer server) {
        this.server = server;
    }

    /* ====================================================================== */

    public void handleConnected() {
        if (server == null) {
            throw new IllegalStateException("Server is not set");
        }

        server.execute(() -> server.getPlayerList().getPlayers().forEach(ConnectionNoticeDispatch::connected));
    }

    public void handleDisconnected() {
        if (server == null) {
            throw new IllegalStateException("Server is not set");
        }

        server.execute(() -> server.getPlayerList().getPlayers().forEach(ConnectionNoticeDispatch::disconnected));
    }

    public void handleEvent(String raw) {
        if (server == null) {
            throw new IllegalStateException("Server is not set");
        }

        JsonObject envelope = JsonParser.parseString(raw).getAsJsonObject();

        String eventType = envelope.get("type").getAsString();
        if (eventType.equals(TYPE_CONNECTED) || eventType.equals(TYPE_HISTORY)) {
            return;
        }

        NetworkEventDispatch.dispatch(server, raw);

        UniChatEvent eventData = JSONParser.fromJson(envelope, UniChatEvent.class);
        if (eventData != null) {
            ServerCommandDispatch.dispatch(server, Workers.INSTANCE.commandsFor(eventType, eventData));
        }
    }

}
