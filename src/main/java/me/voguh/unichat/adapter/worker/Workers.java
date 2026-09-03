/*!******************************************************************************
 * Copyright (c) 2026 Voguh
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

package me.voguh.unichat.adapter.worker;

import me.voguh.unichat.adapter.event.UniChatEvent;
import me.voguh.unichat.adapter.worker.loader.WorkerLoader;

import java.util.Collections;
import java.util.List;

public enum Workers {
    INSTANCE;

    private volatile List<Worker> workers = Collections.emptyList();

    public int reload() {
        workers = WorkerLoader.load();

        return workers.size();
    }

    public List<String> commandsFor(String eventType, UniChatEvent event) {
        return workers.stream()
            .filter(worker -> worker.matches(eventType, event))
            .flatMap(worker -> worker.actions().execCommands().stream())
            .toList();
    }

}
