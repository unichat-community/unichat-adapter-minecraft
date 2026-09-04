/*!******************************************************************************
 * Copyright (c) 2026 Voguh
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

package me.voguh.unichat.adapter.worker.function;

import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class WorkerFunctions {

    private static final Map<String, WorkerFunction> BY_ID = Stream.of(
            new Round(),
            new Trim(),
            new Upper(),
            new Lower(),
            new EscapeJson(),
            new StripPrefix(),
            new StripSuffix(),
            new Truncate(),
            new Replace(),
            new Default()
        )
        .collect(Collectors.toMap(WorkerFunction::id, function -> function));

    public static @Nullable WorkerFunction byId(String id) {
        return BY_ID.get(id);
    }

    /* ====================================================================== */

    private WorkerFunctions() {
        throw new IllegalStateException("Utility class");
    }

}
