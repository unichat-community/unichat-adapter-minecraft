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

import me.voguh.unichat.adapter.server.MinecraftServerHolder;
import net.minecraft.commands.CommandSourceStack;

import java.util.List;

public final class ServerCommandDispatch {

    public static void dispatch(List<String> commands) {
        if (commands.isEmpty()) {
            return;
        }

        MinecraftServerHolder.execute((server) -> {
            CommandSourceStack source = server.createCommandSourceStack();
            commands.forEach(command -> server.getCommands().performPrefixedCommand(source, command));
        });
    }

    /* ====================================================================== */

    private ServerCommandDispatch() {
        throw new IllegalStateException("Utility class");
    }

}
