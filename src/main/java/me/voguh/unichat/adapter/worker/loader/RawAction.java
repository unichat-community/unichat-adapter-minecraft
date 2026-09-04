/*!******************************************************************************
 * Copyright (c) 2026 Voguh
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

package me.voguh.unichat.adapter.worker.loader;

import me.voguh.unichat.adapter.event.UniChatEventUtils;
import me.voguh.unichat.adapter.util.Property;
import me.voguh.unichat.adapter.util.Strings;
import me.voguh.unichat.adapter.worker.WorkerCommand;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

record RawAction(@Nullable List<@Nullable String> execCommands) {

    public static List<WorkerCommand> parse(String eventType, @Nullable RawAction rawActions) {
        if (rawActions == null) {
            throw new IllegalArgumentException("Property 'actions' is missing");
        }

        return parseCommands(eventType, rawActions.execCommands());
    }

    private static List<WorkerCommand> parseCommands(String eventType, @Nullable List<@Nullable String> rawCommands) {
        if (rawCommands == null || rawCommands.isEmpty()) {
            return Collections.emptyList();
        }

        List<Property> properties = UniChatEventUtils.getEventProperties(eventType);

        List<WorkerCommand> commands = new ArrayList<>();
        for (int i = 0; i < rawCommands.size(); i++) {
            String rawCommand = rawCommands.get(i);
            if (Strings.isNullOrEmpty(rawCommand)) {
                continue;
            }

            commands.add(CommandParser.parse("Property 'actions.execCommands[" + i + "]'", properties, rawCommand));
        }

        return List.copyOf(commands);
    }

}
