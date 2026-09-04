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
import me.voguh.unichat.adapter.util.Property;
import org.jspecify.annotations.Nullable;

import java.util.List;

public record Worker(String name, String eventType, List<Condition> conditions, Action actions) {

    public record Condition(Property property, WorkerOperator operator, @Nullable Object value) {

        public boolean matches(UniChatEvent event) {
            Object evtValue = property.getValue(event);

            return switch (property.kind()) {
                case NUMBER -> operator.matchesNumber(toDouble(evtValue), toDouble(value));
                case STRING -> operator.matchesString((String) evtValue, (String) value);
                case BOOLEAN -> operator.matchesBoolean((Boolean) evtValue, (Boolean) value);
            };
        }

        private static @Nullable Double toDouble(@Nullable Object raw) {
            return raw == null ? null : ((Number) raw).doubleValue();
        }

    }

    public record Action(List<String> execCommands) {

    }

    /* ====================================================================== */

    public boolean matches(String eventType, UniChatEvent event) {
        if (!this.eventType.equals(eventType)) {
            return false;
        }

        return conditions.stream().allMatch(cond -> cond.matches(event));
    }

}
