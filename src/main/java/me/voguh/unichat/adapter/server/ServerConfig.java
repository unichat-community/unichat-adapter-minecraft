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

import net.neoforged.neoforge.common.ModConfigSpec;

import java.net.URI;

public final class ServerConfig {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.ConfigValue<String> WEBSOCKET_URL_SPEC = BUILDER
        .define("websocketUrl", "ws://localhost:9527/ws", ServerConfig::isWebSocketUrl);

    private static final ModConfigSpec.BooleanValue AUTO_CONNECT_SPEC = BUILDER.define("autoConnect", false);

    public static final ModConfigSpec SPEC = BUILDER.build();

    /* ====================================================================== */

    private static boolean isWebSocketUrl(final Object url) {
        if (!(url instanceof String raw) || raw.isBlank()) {
            return false;
        }

        try {
            String scheme = URI.create(raw).getScheme();
            return "ws".equals(scheme) || "wss".equals(scheme);
        } catch (Exception e) {
            return false;
        }
    }

    /* ====================================================================== */

    public static String websocketUrl() {
        return WEBSOCKET_URL_SPEC.get();
    }

    public static boolean autoConnect() {
        return AUTO_CONNECT_SPEC.get();
    }

    public static void updateSettings(String websocketUrl, boolean autoConnect) {
        if (!isWebSocketUrl(websocketUrl)) {
            throw new IllegalArgumentException("Invalid WebSocket URL: " + websocketUrl);
        }

        WEBSOCKET_URL_SPEC.set(websocketUrl);
        AUTO_CONNECT_SPEC.set(autoConnect);
    }

}
