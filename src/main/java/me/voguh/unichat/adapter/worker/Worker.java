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

import java.util.List;

public record Worker(
    String name,
    String eventType,
    List<WorkerCondition> conditions,
    List<WorkerCommand> execCommands
) {

    public boolean matches(String eventType, UniChatEvent event) {
        if (!this.eventType.equals(eventType)) {
            return false;
        }

        return conditions.stream().allMatch(cond -> cond.matches(event));
    }

}
