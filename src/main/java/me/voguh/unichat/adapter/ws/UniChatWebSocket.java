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

import me.voguh.unichat.adapter.server.ServerEventHandler;
import me.voguh.unichat.adapter.util.Strings;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public enum UniChatWebSocket {
    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(UniChatWebSocket.class);

    private static final long CLOSE_GRACE_SECONDS = 5;
    private static final long RECONNECT_DELAY_SECONDS = 5;

    private @Nullable WebSocket socket;
    private @Nullable ScheduledFuture<?> pendingReconnect;
    private volatile long generation;
    private String url;
    private boolean keepAlive;

    private final HttpClient client;
    private final ScheduledExecutorService scheduler;

    /* ====================================================================== */

    private UniChatWebSocket() {
        this.generation = 0;
        this.url = "ws://localhost:9527/ws";
        this.keepAlive = true;

        ExecutorService wsExecutor = Executors.newCachedThreadPool(Thread.ofPlatform().daemon().name("unichat-ws-", 0).factory());
        this.client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).executor(wsExecutor).build();
        this.scheduler = Executors.newSingleThreadScheduledExecutor(Thread.ofPlatform().daemon().name("unichat-ws-timer").factory());
    }

    /* ====================================================================== */

    public boolean isConnected() {
        return isConnected(socket);
    }

    private boolean isConnected(@Nullable WebSocket ws) {
        return ws != null && !ws.isInputClosed() && !ws.isOutputClosed();
    }

    boolean isCurrent(long gen) {
        return this.generation == gen;
    }

    /* ====================================================================== */

    public synchronized void connect(String url, boolean keepAlive) {
        if (isConnected(socket)) {
            if (this.url.equals(url)) {
                return;
            }

            LOGGER.info("[UniChat Adapter] WebSocket URL changed, disconnecting from {} and connecting to {}", this.url, url);
            disconnect();
        } else if (pendingReconnect != null) {
            pendingReconnect.cancel(false);
            pendingReconnect = null;
        }

        final long gen = ++generation;
        this.url = url;
        this.keepAlive = keepAlive;
        client.newWebSocketBuilder().buildAsync(URI.create(url), new SocketHandler(gen))
                .whenComplete((ws, error) -> {
                    synchronized (this) {
                        if (gen != generation) {
                            if (ws != null) {
                                ws.abort();
                            }

                            return;
                        }

                        if (error != null) {
                            onError(gen, error);
                        } else {
                            socket = ws;
                        }
                    }
                });
    }

    public synchronized void disconnect() {
        if (pendingReconnect != null) {
            pendingReconnect.cancel(false);
            pendingReconnect = null;
        }

        final WebSocket ws = socket;
        socket = null;
        this.keepAlive = false;
        if (!isConnected(ws)) {
            return;
        }

        ws.sendClose(WebSocket.NORMAL_CLOSURE, "Disconnecting")
                .whenComplete((_ws, error) -> {
                    if (error != null) {
                        ws.abort();
                    } else {
                        scheduler.schedule(ws::abort, CLOSE_GRACE_SECONDS, TimeUnit.SECONDS);
                    }
                });
    }

    /* ====================================================================== */

    synchronized void onOpen(long gen) {
        if (!isCurrent(gen)) {
            return;
        }

        try {
            ServerEventHandler.INSTANCE.handleConnected();
        } catch (Exception e) {
            LOGGER.error("[UniChat Adapter] Error handling open: {}", e.getMessage(), e);
        } finally {
            LOGGER.info("[UniChat Adapter] WebSocket connected");
        }
    }

    void onMessage(long gen, String raw) {
        if (!isCurrent(gen)) {
            return;
        }

        try {
            ServerEventHandler.INSTANCE.handleEvent(raw);
        } catch (Exception e) {
            LOGGER.error("[UniChat Adapter] Error handling message: {}", e.getMessage(), e);
        }
    }

    synchronized void onClose(long gen, int statusCode, String reason) {
        if (!isCurrent(gen)) {
            return;
        }

        try {
            ServerEventHandler.INSTANCE.handleDisconnected();
        } catch (Exception e) {
            LOGGER.error("[UniChat Adapter] Error handling close: {}", e.getMessage(), e);
        } finally {
            scheduleReconnect();
            LOGGER.info("[UniChat Adapter] WebSocket closed with status code {} and reason: {}", statusCode, reason);
        }
    }

    synchronized void onError(long gen, Throwable error) {
        if (!isCurrent(gen)) {
            return;
        }

        try {
            ServerEventHandler.INSTANCE.handleDisconnected();
        } catch (Exception e) {
            LOGGER.error("[UniChat Adapter] Error handling error: {}", e.getMessage(), e);
        } finally {
            scheduleReconnect();
            LOGGER.error("[UniChat Adapter] WebSocket error: {}", error.getMessage(), error);
        }
    }

    /* ====================================================================== */

    private void scheduleReconnect() {
        if (!keepAlive) {
            return;
        }

        if (pendingReconnect != null && !pendingReconnect.isDone()) {
            return;
        }

        final String currentUrl = url;
        if (Strings.isNullOrEmpty(currentUrl)) {
            return;
        }

        LOGGER.info("[UniChat Adapter] Attempting to reconnect in {} seconds...", RECONNECT_DELAY_SECONDS);
        this.pendingReconnect = scheduler.schedule(() -> connect(currentUrl, true), RECONNECT_DELAY_SECONDS, TimeUnit.SECONDS);
    }

}
