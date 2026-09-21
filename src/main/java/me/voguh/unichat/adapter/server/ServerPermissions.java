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

import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public final class ServerPermissions {

    public static boolean canManage(ServerPlayer player) {
        return player.server.isSingleplayerOwner(player.getGameProfile()) || player.hasPermissions(Commands.LEVEL_ADMINS);
    }

    /* ====================================================================== */

    private ServerPermissions() {
        throw new UnsupportedOperationException("Utility class");
    }

}
