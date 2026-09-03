/*!******************************************************************************
 * Copyright (c) 2026 Voguh
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

package me.voguh.unichat.adapter.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.voguh.unichat.adapter.worker.Workers;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.RegisterCommandsEvent;

public final class CommandsBootstrap {

    public static void register(RegisterCommandsEvent event) {
        LiteralArgumentBuilder<CommandSourceStack> prefix = Commands.literal("unichat")
            .requires(Commands.hasPermission(Commands.LEVEL_ADMINS));

        event.getDispatcher().register(prefix.then(Commands.literal("reload").executes(CommandsBootstrap::reload)));
    }

    private static int reload(CommandContext<CommandSourceStack> context) {
        int count = Workers.INSTANCE.reload();
        context.getSource().sendSuccess(() -> Component.translatable("commands.unichat_adapter.reload", count), true);

        return count;
    }

    /* ====================================================================== */

    private CommandsBootstrap() {
        throw new IllegalStateException("Utility class");
    }

}
