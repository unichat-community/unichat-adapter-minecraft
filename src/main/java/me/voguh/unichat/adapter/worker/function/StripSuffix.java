/*!******************************************************************************
 * Copyright (c) 2026 Voguh
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

package me.voguh.unichat.adapter.worker.function;

import org.jspecify.annotations.Nullable;

import java.util.List;

public final class StripSuffix implements WorkerFunction {

    @Override
    public String id() {
        return "stripSuffix";
    }

    @Override
    public Object apply(List<@Nullable Object> args) {
        String value = (String) args.getFirst();
        String suffix = (String) args.get(1);

        return value.endsWith(suffix) ? value.substring(0, value.length() - suffix.length()) : value;
    }

}
