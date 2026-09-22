package me.voguh.unichat.adapter.worker.loader;

import me.voguh.unichat.adapter.event.UniChatEventUtils;
import me.voguh.unichat.adapter.util.Kind;
import me.voguh.unichat.adapter.util.Property;
import me.voguh.unichat.adapter.util.Strings;
import me.voguh.unichat.adapter.worker.RawCondition;
import me.voguh.unichat.adapter.worker.WorkerCondition;
import me.voguh.unichat.adapter.worker.WorkerOperator;
import me.voguh.unichat.adapter.worker.WorkerValidationException;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

final class RawConditionParser {

    public static List<WorkerCondition> parse(String eventType, @Nullable List<@Nullable RawCondition> rawConditions) {
        if (rawConditions == null) {
            throw new WorkerValidationException("missing_conditions");
        }

        List<WorkerCondition> conditions = new ArrayList<>();
        for (int i = 0; i < rawConditions.size(); i++) {
            int position = i + 1;

            RawCondition rawCondition = rawConditions.get(i);
            if (rawCondition == null) {
                throw new WorkerValidationException("null_condition", position);
            }

            Property property = parseConditionProperty(eventType, rawCondition.property(), position);
            WorkerOperator operator = parseConditionOperator(property, rawCondition.operator(), position);
            Object value = parseConditionValue(property, rawCondition.value(), position);
            conditions.add(new WorkerCondition(property, operator, value));
        }

        return conditions;
    }

    private static Property parseConditionProperty(String eventType, @Nullable String property, int position) {
        if (Strings.isNullOrEmpty(property)) {
            throw new WorkerValidationException("missing_condition_property", position);
        }

        Optional<Property> optProperty = UniChatEventUtils.getEventProperty(eventType, property);
        if (optProperty.isEmpty()) {
            throw new WorkerValidationException("invalid_condition_property", position, property, eventType);
        }

        return optProperty.get();
    }

    private static WorkerOperator parseConditionOperator(Property property, @Nullable String rawOperator, int position) {
        if (Strings.isNullOrEmpty(rawOperator)) {
            throw new WorkerValidationException("missing_condition_operator", position);
        }

        Optional<WorkerOperator> optOperator = WorkerOperator.fromString(rawOperator);
        if (optOperator.isEmpty()) {
            throw new WorkerValidationException("invalid_condition_operator", position, rawOperator);
        }

        WorkerOperator operator = optOperator.get();
        if (!operator.isValidFor(property.kind())) {
            throw new WorkerValidationException("incompatible_condition_operator", position, rawOperator, property.name(), property.kind());
        }

        return operator;
    }

    private static @Nullable Object parseConditionValue(Property property, @Nullable Object value, int position) {
        if (value == null) {
            return null;
        }

        Kind valueKind = Kind.fromClass(value.getClass());
        Kind propertyKind = property.kind();
        if (valueKind != propertyKind) {
            throw new WorkerValidationException("invalid_condition_value", position, valueKind, property.name(), propertyKind);
        }

        return value;
    }

    /* ====================================================================== */

    private RawConditionParser() {
        throw new UnsupportedOperationException("Utility class");
    }

}
