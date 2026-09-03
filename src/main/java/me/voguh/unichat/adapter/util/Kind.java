/*!******************************************************************************
 * Copyright (c) 2026 Voguh
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

package me.voguh.unichat.adapter.util;

import org.jspecify.annotations.Nullable;

import java.lang.invoke.MethodType;

public enum Kind {
    NUMBER(Number.class),
    STRING(String.class),
    BOOLEAN(Boolean.class);

    private final Class<?> aClass;

    public Class<?> javaKind() {
        return aClass;
    }

    /* ====================================================================== */

    private Kind(Class<?> aClass) {
        this.aClass = aClass;
    }

    /* ====================================================================== */

    public static @Nullable Kind fromClass(Class<?> clazz) {
        if (clazz.isPrimitive()) {
            clazz = MethodType.methodType(clazz).wrap().returnType();
        }

        if (clazz == String.class) {
            return STRING;
        } else if (clazz == Boolean.class) {
            return BOOLEAN;
        } else if (Number.class.isAssignableFrom(clazz)) {
            return NUMBER;
        }

        return null;
    }

}
