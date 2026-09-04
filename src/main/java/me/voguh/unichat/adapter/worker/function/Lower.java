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
import java.util.Locale;

public final class Lower implements WorkerFunction {

    @Override
    public String id() {
        return "lower";
    }

    @Override
    public Object apply(List<@Nullable Object> args) {
        return ((String) args.getFirst()).toLowerCase(Locale.ROOT);
    }

}
