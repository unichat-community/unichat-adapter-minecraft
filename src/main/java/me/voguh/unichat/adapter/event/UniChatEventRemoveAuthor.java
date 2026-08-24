/*!******************************************************************************
 * Copyright (c) 2026 Voguh
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

package me.voguh.unichat.adapter.event;

import org.jspecify.annotations.Nullable;

import java.util.Map;

public record UniChatEventRemoveAuthor(
        String channelId,
        @Nullable String channelName,
        String platform,
        Map<String, @Nullable String> flags,
        String authorId,
        long timestamp
) implements UniChatEvent {

    public static final String TYPE = "unichat:remove_author";

}
