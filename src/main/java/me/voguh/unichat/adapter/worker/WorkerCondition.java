package me.voguh.unichat.adapter.worker;

import me.voguh.unichat.adapter.event.UniChatEvent;
import me.voguh.unichat.adapter.util.Property;
import org.jspecify.annotations.Nullable;

public record WorkerCondition(Property property, WorkerOperator operator, @Nullable Object value) {

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
