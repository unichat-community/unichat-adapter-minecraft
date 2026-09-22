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
import me.voguh.unichat.adapter.util.Strings;
import me.voguh.unichat.adapter.worker.RawWorker;
import me.voguh.unichat.adapter.worker.Worker;
import me.voguh.unichat.adapter.worker.WorkerCommand;
import me.voguh.unichat.adapter.worker.WorkerCondition;
import me.voguh.unichat.adapter.worker.WorkerValidationException;
import org.jspecify.annotations.Nullable;

import java.util.List;

public final class RawWorkerParser {

    /**
     * Builds a worker from its raw form, running the same validation on both sides.
     *
     * @param entry the raw worker to parse
     * @return the parsed worker
     * @throws WorkerValidationException if the worker cannot be built
     */
    public static Worker parse(RawWorker entry) {
        String name = parseName(entry.name());
        String eventType = parseEventType(entry.onEvent());
        List<WorkerCondition> conditions = RawConditionParser.parse(eventType, entry.conditions());
        List<WorkerCommand> execCommands = RawActionParser.parse(eventType, entry.actions());

        return new Worker(name, eventType, conditions, execCommands);
    }

    private static String parseName(@Nullable String name) {
        if (Strings.isNullOrEmpty(name)) {
            throw new WorkerValidationException("missing_name");
        }

        return name;
    }

    private static String parseEventType(@Nullable String eventType) {
        if (Strings.isNullOrEmpty(eventType)) {
            throw new WorkerValidationException("missing_event");
        } else if (!UniChatEventUtils.isValidEventType(eventType)) {
            throw new WorkerValidationException("invalid_event", eventType);
        }

        return eventType;
    }

    /* ====================================================================== */

    private RawWorkerParser() {
        throw new UnsupportedOperationException("Utility class");
    }

}
