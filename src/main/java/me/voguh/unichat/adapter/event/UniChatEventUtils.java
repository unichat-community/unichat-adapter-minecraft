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

import me.voguh.unichat.adapter.util.Kind;
import me.voguh.unichat.adapter.util.Property;
import me.voguh.unichat.adapter.util.Strings;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.RecordComponent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class UniChatEventUtils {

    private static boolean locked = false;
    private static final Map<String, Class<? extends UniChatEvent>> BY_EVENT_TYPE = new HashMap<>();
    private static final Map<Class<? extends UniChatEvent>, String> BY_EVENT_CLASS = new HashMap<>();
    private static final Map<String, List<Property>> EVENT_PROPERTIES_BY_TYPE = new HashMap<>();

    /* ====================================================================== */

    public static void initialize() {
        if (locked) {
            throw new IllegalStateException("UniChatEventUtils has already been initialized");
        }

        for (Class<?> sub : UniChatEvent.class.getPermittedSubclasses()) {
            if (!sub.isRecord()) {
                throw new IllegalStateException("Event '" + sub.getSimpleName() + "' must be a record");
            }

            String type = initializeGetEventType(sub);
            if (BY_EVENT_TYPE.containsKey(type)) {
                throw new IllegalStateException("Duplicate event kind '" + type + "' for events '" + BY_EVENT_TYPE.get(type).getSimpleName() + "' and '" + sub.getSimpleName() + "'");
            }

            Class<? extends UniChatEvent> aClass = sub.asSubclass(UniChatEvent.class);
            BY_EVENT_TYPE.put(type, aClass);
            BY_EVENT_CLASS.put(aClass, type);
            EVENT_PROPERTIES_BY_TYPE.put(type, initializeGetEventProperties(aClass));
        }

        locked = true;
    }

    private static String initializeGetEventType(Class<?> sub) {
        String throwPrefix = "Event '" + sub.getSimpleName() + "' field 'TYPE'";

        try {
            Field field = sub.getDeclaredField("TYPE");

            int mod = field.getModifiers();
            if (!Modifier.isPublic(mod) || !Modifier.isStatic(mod) || !Modifier.isFinal(mod)) {
                throw new IllegalStateException(throwPrefix + " must be public static final");
            }

            String type = (String) field.get(null);
            if (Strings.isNullOrEmpty(type)) {
                throw new IllegalStateException(throwPrefix + " is missing or blank");
            }

            return type;
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException(throwPrefix + " is missing");
        } catch (IllegalAccessException e) {
            throw new AssertionError(throwPrefix + " must be accessible", e);
        } catch (ClassCastException e) {
            throw new IllegalStateException(throwPrefix + " must be String");
        }
    }

    private static List<Property> initializeGetEventProperties(Class<? extends UniChatEvent> aClass) {
        List<Property> properties = new ArrayList<>();
        for (RecordComponent rc : aClass.getRecordComponents()) {
            Kind kind = Kind.fromClass(rc.getType());
            if (kind == null) {
                continue;
            }

            properties.add(new Property(rc.getName(), kind, rc.getAccessor()));
        }

        return List.copyOf(properties);
    }

    /* ====================================================================== */

    /**
     * Returns the event kind for the given event class.
     *
     * @param eventClass the event class
     * @return the event kind
     * @throws IllegalArgumentException if the event class is unknown
     */
    public static String getEventType(Class<? extends UniChatEvent> eventClass) {
        String type = BY_EVENT_CLASS.get(eventClass);
        if (type == null) {
            throw new IllegalArgumentException("Unknown event class '" + eventClass.getSimpleName() + "'");
        }

        return type;
    }

    /**
     * Returns the event class for the given event kind.
     *
     * @param eventType the event kind
     * @return the event class
     * @throws IllegalArgumentException if the event kind is unknown
     */
    public static Class<? extends UniChatEvent> getEventClass(String eventType) {
        Class<? extends UniChatEvent> target = BY_EVENT_TYPE.get(eventType);
        if (target == null) {
            throw new IllegalArgumentException("Unknown event kind '" + eventType + "'");
        }

        return target;
    }

    /* ====================================================================== */

    /**
     * Validates if the given event kind is known.
     *
     * @param eventType the event kind
     * @return true if the event kind is known, false otherwise
     */
    public static boolean isValidEventType(String eventType) {
        return BY_EVENT_TYPE.containsKey(eventType);
    }

    /* ====================================================================== */

    public static Optional<Property> getEventProperty(String eventType, String property) {
        List<Property> properties = EVENT_PROPERTIES_BY_TYPE.get(eventType);
        if (properties == null) {
            throw new IllegalArgumentException("Unknown event kind '" + eventType + "'");
        }

        return properties.stream().filter(p -> p.name().equals(property)).findFirst();
    }

    public static List<Property> getEventProperties(String eventType) {
        List<Property> properties = EVENT_PROPERTIES_BY_TYPE.get(eventType);
        if (properties == null) {
            throw new IllegalArgumentException("Unknown event kind '" + eventType + "'");
        }

        return properties;
    }

    /* ====================================================================== */

    private UniChatEventUtils() {
        throw new IllegalStateException("Utility class");
    }

}
