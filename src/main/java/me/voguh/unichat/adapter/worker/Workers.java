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
import me.voguh.unichat.adapter.event.UniChatEventUtils;
import me.voguh.unichat.adapter.util.Property;
import me.voguh.unichat.adapter.worker.loader.WorkerLoader;

import java.util.Collections;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum Workers {
    INSTANCE;

    private static final Pattern PLACEHOLDER = Pattern.compile("\\{(\\w+)}");

    private volatile List<Worker> workers = Collections.emptyList();

    public int reload() {
        workers = WorkerLoader.load();

        return workers.size();
    }

    public List<String> commandsFor(String eventType, UniChatEvent event) {
        List<Property> eventProperties = UniChatEventUtils.getEventProperties(eventType);

        return workers.stream()
            .filter(worker -> worker.matches(eventType, event))
            .flatMap(worker -> worker.actions().execCommands().stream())
            .map(command -> resolvePlaceholders(command, eventProperties, event))
            .toList();
    }

    private String resolvePlaceholders(String rawCommand, List<Property> eventProperties, UniChatEvent event) {
        return PLACEHOLDER.matcher(rawCommand).replaceAll(match -> replacement(match, eventProperties, event));
    }

    private String replacement(MatchResult match, List<Property> eventProperties, UniChatEvent event) {
        Object value = eventProperties.stream()
            .filter(property -> property.name().equals(match.group(1)))
            .findFirst()
            .map(property -> property.getValue(event))
            .orElse(null);

        if (value == null) {
            return match.group();
        }

        return Matcher.quoteReplacement(String.valueOf(value));
    }

}
