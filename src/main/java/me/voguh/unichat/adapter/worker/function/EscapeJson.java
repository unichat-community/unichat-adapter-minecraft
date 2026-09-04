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

public final class EscapeJson implements WorkerFunction {

    @Override
    public String id() {
        return "escapeJson";
    }

    @Override
    public Object apply(List<@Nullable Object> args) {
        String value = (String) args.getFirst();
        StringBuilder escaped = new StringBuilder(value.length());

        for (int index = 0; index < value.length(); index++) {
            char character = value.charAt(index);

            switch (character) {
                case '"' -> escaped.append("\\\"");
                case '\\' -> escaped.append("\\\\");
                case '\n' -> escaped.append("\\n");
                case '\r' -> escaped.append("\\r");
                case '\t' -> escaped.append("\\t");
                default -> escaped.append(character < ' ' ? String.format("\\u%04x", (int) character) : character);
            }
        }

        return escaped.toString();
    }

}
