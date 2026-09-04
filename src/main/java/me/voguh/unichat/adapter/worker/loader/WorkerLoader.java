/*!******************************************************************************
 * Copyright (c) 2026 Voguh
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

package me.voguh.unichat.adapter.worker.loader;

import com.google.gson.JsonParseException;
import me.voguh.unichat.adapter.event.UniChatEventUtils;
import me.voguh.unichat.adapter.util.JSONParser;
import me.voguh.unichat.adapter.util.Strings;
import me.voguh.unichat.adapter.worker.Worker;
import me.voguh.unichat.adapter.worker.WorkerCommand;
import net.minecraftforge.fml.loading.FMLPaths;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WorkerLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerLoader.class);

    private static final String FILE_NAME = "unichat_adapter-workers.jsonc";

    public static List<Worker> load() {
        Path file = FMLPaths.CONFIGDIR.get().resolve(FILE_NAME);

        try {
            if (Files.notExists(file)) {
                try (InputStream template = WorkerLoader.class.getResourceAsStream("/" + FILE_NAME)) {
                    Files.copy(template, file);
                }
            }

            List<@Nullable RawWorker> rawEntries = JSONParser.fromJson(Files.readString(file), RawWorker.TYPE);
            if (rawEntries == null) {
                return Collections.emptyList();
            }

            List<Worker> workers = new ArrayList<>();
            for (int i = 0; i < rawEntries.size(); i++) {
                RawWorker entry = rawEntries.get(i);
                if (entry == null) {
                    LOGGER.error("[UniChat Adapter] Worker #{} was skipped: entry is null", i);
                    continue;
                }

                try {
                    workers.add(buildWorker(entry));
                } catch (Exception e) {
                    LOGGER.error("[UniChat Adapter] An error occurred on load worker #{}", i, e);
                }
            }

            LOGGER.info("[UniChat Adapter] Loaded {} workers", workers.size());

            return workers;
        } catch (IOException | JsonParseException e) {
            LOGGER.error("[UniChat Adapter] Failed to load workers from '{}'", file, e);

            return Collections.emptyList();
        }
    }

    private static Worker buildWorker(RawWorker entry) {
        String name = Strings.requiresNonNullOrEmpty(entry.name(), "Property 'name' is missing or blank");
        String eventType = parseEventType(entry.onEvent());
        List<Worker.Condition> conditions = RawCondition.parse(eventType, entry.conditions());
        List<WorkerCommand> execCommands = RawAction.parse(eventType, entry.actions());

        return new Worker(name, eventType, conditions, execCommands);
    }

    private static String parseEventType(@Nullable String eventType) {
        if (Strings.isNullOrEmpty(eventType)) {
            throw new IllegalArgumentException("Property 'eventType' is missing or blank");
        } else if (!UniChatEventUtils.isValidEventType(eventType)) {
            throw new IllegalArgumentException("Property 'eventType' has an invalid event type '" + eventType + "'");
        }

        return eventType;
    }

    /* ====================================================================== */

    private WorkerLoader() {
        throw new IllegalStateException("Utility class");
    }

}
