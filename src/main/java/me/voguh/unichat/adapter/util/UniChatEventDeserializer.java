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

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.voguh.unichat.adapter.event.UniChatEvent;
import me.voguh.unichat.adapter.event.UniChatEventCustom;
import me.voguh.unichat.adapter.event.UniChatEventTypes;

import java.lang.reflect.Type;
import java.util.Objects;

public final class UniChatEventDeserializer implements JsonDeserializer<UniChatEvent> {

    @Override
    public UniChatEvent deserialize(JsonElement json, Type type, JsonDeserializationContext context) {
        JsonObject envelope = json.getAsJsonObject();
        JsonElement eventTypeRaw = Objects.requireNonNull(envelope.get("type"), "Missing 'type' field in JSON");
        JsonElement data = Objects.requireNonNull(envelope.get("data"), "Missing 'data' field in JSON");

        String eventType = eventTypeRaw.getAsString();
        if (eventType.equals(UniChatEventCustom.TYPE)) {
            return new UniChatEventCustom(data);
        }

        return context.deserialize(data, UniChatEventTypes.classOf(eventType));
    }

}
