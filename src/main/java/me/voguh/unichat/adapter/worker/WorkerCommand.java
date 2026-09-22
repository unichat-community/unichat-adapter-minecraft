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

import me.voguh.unichat.adapter.event.UniChatEvent;
import me.voguh.unichat.adapter.util.Property;
import me.voguh.unichat.adapter.worker.function.WorkerFunction;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public record WorkerCommand(List<Segment> segments) {

    public String resolve(UniChatEvent event) {
        StringBuilder command = new StringBuilder();

        for (Segment segment : segments) {
            command.append(segment.resolve(event));
        }

        return command.toString();
    }

    /* ====================================================================== */

    public sealed interface Segment permits Segment.Literal, Segment.Template {

        String resolve(UniChatEvent event);

        record Literal(String text) implements Segment {

            @Override
            public String resolve(UniChatEvent event) {
                return text;
            }

        }

        record Template(String source, Node node) implements Segment {

            @Override
            public String resolve(UniChatEvent event) {
                Object value = node.resolve(event);

                return value == null ? source : String.valueOf(value);
            }

        }

    }

    /* ====================================================================== */

    public sealed interface Node permits Node.Constant, Node.PropertyRef, Node.Call {

        @Nullable
        Object resolve(UniChatEvent event);

        record Constant(Object value) implements Node {

            @Override
            public Object resolve(UniChatEvent event) {
                return value;
            }

        }

        record PropertyRef(Property property) implements Node {

            @Override
            public @Nullable Object resolve(UniChatEvent event) {
                return property.getValue(event);
            }

        }

        record Call(WorkerFunction function, List<Node> args) implements Node {

            @Override
            public @Nullable Object resolve(UniChatEvent event) {
                List<@Nullable Object> values = new ArrayList<>();

                for (Node arg : args) {
                    Object value = arg.resolve(event);
                    if (value == null && !function.acceptsNull()) {
                        return null;
                    }

                    values.add(value);
                }

                return function.apply(values);
            }

        }

    }

}
