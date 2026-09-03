/*!******************************************************************************
 * Copyright (c) 2026 Voguh
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

package me.voguh.unichat.adapter.server.dispatch;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public final class ConnectionNoticeDispatch {

    public static void connected(ServerPlayer player) {
        send(player, "connected");
    }

    public static void disconnected(ServerPlayer player) {
        send(player, "disconnected");
    }

    private static void send(ServerPlayer player, String key) {
        player.displayClientMessage(Component.translatable("actionbar.unichat_adapter." + key), true);
    }

    /* ====================================================================== */

    private ConnectionNoticeDispatch() {
        throw new IllegalStateException("Utility class");
    }

}
