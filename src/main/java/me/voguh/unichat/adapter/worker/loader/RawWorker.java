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

import com.google.gson.reflect.TypeToken;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.List;

record RawWorker(
    @Nullable String name,
    @Nullable String onEvent,
    @Nullable List<@Nullable RawCondition> conditions,
    @Nullable RawAction actions
) {

    public static Type TYPE = TypeToken.getParameterized(List.class, RawWorker.class).getType();

}
