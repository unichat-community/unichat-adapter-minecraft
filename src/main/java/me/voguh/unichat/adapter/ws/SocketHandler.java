/*!******************************************************************************
 * Copyright (c) 2026 Voguh
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

package me.voguh.unichat.adapter.ws;

import org.jspecify.annotations.Nullable;

import java.net.http.WebSocket;
import java.util.concurrent.CompletionStage;

final class SocketHandler implements WebSocket.Listener {

    private static final int MAX_BUFFER_SIZE = 1 << 20;

    private final long generation;
    private final StringBuilder buffer = new StringBuilder();

    /* ====================================================================== */

    public SocketHandler(long generation) {
        this.generation = generation;
    }

    /* ====================================================================== */

    @Override
    public void onOpen(WebSocket ws) {
        ws.request(1);
        UniChatWebSocket.INSTANCE.onOpen(generation);
    }

    @Override
    public @Nullable CompletionStage<?> onText(WebSocket ws, CharSequence data, boolean last) {
        ws.request(1);

        if (buffer.length() + data.length() > MAX_BUFFER_SIZE) {
            buffer.setLength(0);
            ws.abort();
        } else {
            buffer.append(data);
            if (last) {
                String raw = buffer.toString();
                buffer.setLength(0);
                UniChatWebSocket.INSTANCE.onMessage(generation, raw);
            }
        }

        return null;
    }

    @Override
    public @Nullable CompletionStage<?> onClose(WebSocket ws, int statusCode, String reason) {
        UniChatWebSocket.INSTANCE.onClose(generation, statusCode, reason);
        return null;
    }

    @Override
    public void onError(WebSocket ws, Throwable error) {
        UniChatWebSocket.INSTANCE.onError(generation, error);
    }

}
