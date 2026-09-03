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

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;

import java.util.List;

public final class ServerCommandDispatch {

    public static void dispatch(MinecraftServer server, List<String> commands) {
        if (commands.isEmpty()) {
            return;
        }

        server.execute(() -> {
            CommandSourceStack source = server.createCommandSourceStack();
            commands.forEach(command -> server.getCommands().performPrefixedCommand(source, command));
        });
    }

    /* ====================================================================== */

    private ServerCommandDispatch() {
        throw new IllegalStateException("Utility class");
    }

}
