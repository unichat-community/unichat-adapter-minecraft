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

import me.voguh.unichat.adapter.util.Kind;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public enum WorkerOperator {
    EQUALS(Kind.NUMBER, Kind.STRING, Kind.BOOLEAN),
    NOT_EQUALS(Kind.NUMBER, Kind.STRING, Kind.BOOLEAN),
    GREATER_THAN(Kind.NUMBER),
    GREATER_THAN_OR_EQUAL(Kind.NUMBER),
    LESS_THAN(Kind.NUMBER),
    LESS_THAN_OR_EQUAL(Kind.NUMBER),
    CONTAINS(Kind.STRING),
    STARTS_WITH(Kind.STRING),
    ENDS_WITH(Kind.STRING);

    private final Set<Kind> kinds;

    /* ====================================================================== */

    private WorkerOperator(Kind... kinds) {
        this.kinds = Set.of(kinds);
    }

    /* ====================================================================== */

    public boolean isValidFor(Kind kind) {
        return kinds.contains(kind);
    }

    public boolean matchesNumber(@Nullable Double evtValue, @Nullable Double targetValue) {
        if (this == EQUALS) {
            return Objects.equals(evtValue, targetValue);
        } else if (this == NOT_EQUALS) {
            return !Objects.equals(evtValue, targetValue);
        } else if (evtValue == null || targetValue == null) {
            return false;
        }

        return switch (this) {
            case GREATER_THAN -> evtValue > targetValue;
            case GREATER_THAN_OR_EQUAL -> evtValue >= targetValue;
            case LESS_THAN -> evtValue < targetValue;
            case LESS_THAN_OR_EQUAL -> evtValue <= targetValue;
            default -> false;
        };
    }

    public boolean matchesString(@Nullable String evtValue, @Nullable String targetValue) {
        if (this == EQUALS) {
            return Objects.equals(evtValue, targetValue);
        } else if (this == NOT_EQUALS) {
            return !Objects.equals(evtValue, targetValue);
        } else if (evtValue == null || targetValue == null) {
            return false;
        }

        return switch (this) {
            case CONTAINS -> evtValue.contains(targetValue);
            case STARTS_WITH -> evtValue.startsWith(targetValue);
            case ENDS_WITH -> evtValue.endsWith(targetValue);
            default -> false;
        };
    }

    public boolean matchesBoolean(@Nullable Boolean evtValue, @Nullable Boolean targetValue) {
        if (this == EQUALS) {
            return Objects.equals(evtValue, targetValue);
        } else if (this == NOT_EQUALS) {
            return !Objects.equals(evtValue, targetValue);
        }

        return false;
    }

    /* ====================================================================== */

    public static Optional<WorkerOperator> fromString(String operator) {
        return switch (operator) {
            case "EQUALS" -> Optional.of(EQUALS);
            case "NOT_EQUALS" -> Optional.of(NOT_EQUALS);
            case "GREATER_THAN" -> Optional.of(GREATER_THAN);
            case "GREATER_THAN_OR_EQUAL" -> Optional.of(GREATER_THAN_OR_EQUAL);
            case "LESS_THAN" -> Optional.of(LESS_THAN);
            case "LESS_THAN_OR_EQUAL" -> Optional.of(LESS_THAN_OR_EQUAL);
            case "CONTAINS" -> Optional.of(CONTAINS);
            case "STARTS_WITH" -> Optional.of(STARTS_WITH);
            case "ENDS_WITH" -> Optional.of(ENDS_WITH);
            default -> Optional.empty();
        };
    }

}
