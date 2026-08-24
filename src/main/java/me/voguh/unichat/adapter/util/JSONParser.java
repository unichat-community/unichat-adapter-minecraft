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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.voguh.unichat.adapter.event.UniChatEvent;

public final class JSONParser {

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(UniChatEvent.class, new UniChatEventDeserializer())
            .create();

    public static <T> T fromJson(String json, Class<T> clazz) {
        return GSON.fromJson(json, clazz);
    }

    public static String toJson(Object obj) {
        return GSON.toJson(obj);
    }

    /* ====================================================================== */

    private JSONParser() {
        throw new IllegalStateException("Utility class");
    }

}
