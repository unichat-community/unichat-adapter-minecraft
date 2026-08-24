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

import java.util.Map;

public final class UniChatEventTypes {

    private static final Map<String, Class<? extends UniChatEvent>> TYPES = Map.ofEntries(
            Map.entry(UniChatEventClear.TYPE, UniChatEventClear.class),
            Map.entry(UniChatEventRemoveMessage.TYPE, UniChatEventRemoveMessage.class),
            Map.entry(UniChatEventRemoveAuthor.TYPE, UniChatEventRemoveAuthor.class),
            Map.entry(UniChatEventMessage.TYPE, UniChatEventMessage.class),
            Map.entry(UniChatEventDonate.TYPE, UniChatEventDonate.class),
            Map.entry(UniChatEventSponsor.TYPE, UniChatEventSponsor.class),
            Map.entry(UniChatEventSponsorGift.TYPE, UniChatEventSponsorGift.class),
            Map.entry(UniChatEventRaid.TYPE, UniChatEventRaid.class),
            Map.entry(UniChatEventRedemption.TYPE, UniChatEventRedemption.class),
            Map.entry(UniChatEventGift.TYPE, UniChatEventGift.class),
            Map.entry(UniChatEventUserstoreUpdate.TYPE, UniChatEventUserstoreUpdate.class),
            Map.entry(UniChatEventCustom.TYPE, UniChatEventCustom.class)
    );

    /**
     * Returns the event class for the given event type.
     *
     * @param type the event type
     * @return the event class
     * @throws IllegalArgumentException if the event type is unknown
     */
    public static Class<? extends UniChatEvent> classOf(String type) {
        Class<? extends UniChatEvent> target = TYPES.get(type);
        if (target == null) {
            throw new IllegalArgumentException("Unknown event type '" + type + "'");
        }

        return target;
    }

    /* ====================================================================== */

    private UniChatEventTypes() {
        throw new IllegalStateException("Utility class");
    }

}
