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
import me.voguh.unichat.adapter.util.Kind;
import me.voguh.unichat.adapter.util.Property;
import me.voguh.unichat.adapter.util.Strings;
import me.voguh.unichat.adapter.worker.Worker;
import me.voguh.unichat.adapter.worker.WorkerOperator;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

record RawCondition(@Nullable String property, @Nullable String operator, @Nullable Object value) {

    static List<Worker.Condition> parse(String eventType, @Nullable List<@Nullable RawCondition> rawConditions) {
        if (rawConditions == null) {
            throw new IllegalArgumentException("Property 'conditions' is missing");
        }

        List<Worker.Condition> conditions = new ArrayList<>();
        for (int i = 0; i < rawConditions.size(); i++) {
            RawCondition rawCondition = rawConditions.get(i);
            if (rawCondition == null) {
                throw new IllegalArgumentException("Property 'conditions[" + i + "]' is null");
            }

            Property property = parseConditionProperty(eventType, rawCondition.property(), i);
            WorkerOperator operator = parseConditionOperator(property, rawCondition.operator(), i);
            Object value = parseConditionValue(property, rawCondition.value(), i);
            conditions.add(new Worker.Condition(property, operator, value));
        }

        return conditions;
    }

    private static Property parseConditionProperty(String eventType, @Nullable String property, int index) {
        String throwPrefix = "Property 'conditions[" + index + "].property'";

        if (Strings.isNullOrEmpty(property)) {
            throw new IllegalArgumentException(throwPrefix + " is missing or blank");
        }

        Optional<Property> optProperty = UniChatEventUtils.getEventProperty(eventType, property);
        if (optProperty.isEmpty()) {
            throw new IllegalArgumentException(throwPrefix + " has an invalid property '" + property + "' for event type '" + eventType + "'");
        }

        return optProperty.get();
    }

    private static WorkerOperator parseConditionOperator(Property property, @Nullable String rawOperator, int index) {
        String throwPrefix = "Property 'conditions[" + index + "].operator'";

        if (Strings.isNullOrEmpty(rawOperator)) {
            throw new IllegalArgumentException(throwPrefix + " is missing or blank");
        }

        Optional<WorkerOperator> optOperator = WorkerOperator.fromString(rawOperator);
        if (optOperator.isEmpty()) {
            throw new IllegalArgumentException(throwPrefix + " has an invalid operator '" + rawOperator + "'");
        }

        WorkerOperator operator = optOperator.get();
        if (!operator.isValidFor(property.kind())) {
            throw new IllegalArgumentException(throwPrefix + " has an invalid operator '" + rawOperator + "' for property '" + property.name() + "' of kind '" + property.kind() + "'");
        }

        return operator;
    }

    private static @Nullable Object parseConditionValue(Property property, @Nullable Object value, int index) {
        if (value == null) {
            return null;
        }

        Kind valueKind = Kind.fromClass(value.getClass());
        Kind propertyKind = property.kind();
        if (valueKind != propertyKind) {
            throw new IllegalArgumentException("Property 'conditions[" + index + "].value' has an invalid value type '" + valueKind + "' for property '" + property.name() + "' of kind '" + propertyKind + "'");
        }

        return value;
    }

}
