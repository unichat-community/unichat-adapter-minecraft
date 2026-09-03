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

import me.voguh.unichat.adapter.util.Strings;
import me.voguh.unichat.adapter.worker.Worker;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

record RawAction(@Nullable List<@Nullable String> execCommands) {

    static Worker.Action parse(String eventType, @Nullable RawAction rawActions) {
        if (rawActions == null) {
            throw new IllegalArgumentException("Property 'actions' is missing");
        }

        List<String> commands = parseCommands(eventType, rawActions.execCommands());

        return new Worker.Action(commands);
    }

    private static List<String> parseCommands(String eventType, @Nullable List<@Nullable String> rawCommands) {
        if (rawCommands == null || rawCommands.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> commands = new ArrayList<>();
        for (String rawCommand : rawCommands) {
            if (Strings.isNullOrEmpty(rawCommand)) {
                continue;
            }

            commands.add(rawCommand);
        }

        return List.copyOf(commands);
    }

}
