/*!******************************************************************************
 * Copyright (c) 2026 Voguh
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

package me.voguh.unichat.adapter.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.net.URI;

public class CommonConfig {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.ConfigValue<String> WEBSOCKET_URL_SPEC = BUILDER
        .define("websocketUrl", "ws://localhost:9527/ws", CommonConfig::isWebSocketUrl);

    private static final ForgeConfigSpec.BooleanValue AUTO_CONNECT_SPEC = BUILDER.define("autoConnect", true);

    public static final ForgeConfigSpec SPEC = BUILDER.build();

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

}
