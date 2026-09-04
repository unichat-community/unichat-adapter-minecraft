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

import me.voguh.unichat.adapter.event.UniChatEvent;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public record Property(String name, Kind kind, Method accessor) {

    public @Nullable Object getValue(UniChatEvent instance) {
        try {
            return accessor.invoke(instance);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new AssertionError("Failed to access property '" + name + "' on instance of " + instance.getClass().getSimpleName(), e);
        }
    }

}
