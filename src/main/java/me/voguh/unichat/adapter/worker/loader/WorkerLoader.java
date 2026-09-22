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

import me.voguh.unichat.adapter.server.MinecraftServerHolder;
import me.voguh.unichat.adapter.util.JSONParser;
import me.voguh.unichat.adapter.worker.RawWorker;
import me.voguh.unichat.adapter.worker.Worker;
import net.minecraft.world.level.storage.LevelResource;
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
import java.util.Objects;

public final class WorkerLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerLoader.class);

    private static final String FILE_NAME = "unichat_adapter-workers.jsonc";

    public static Path file() {
        return MinecraftServerHolder.getInstance().getWorldPath(new LevelResource("serverconfig")).resolve(FILE_NAME);
    }

    public static List<RawWorker> raw() {
        try {
            Path file = file();
            if (Files.notExists(file)) {
                try (InputStream template = WorkerLoader.class.getResourceAsStream("/" + FILE_NAME)) {
                    Files.copy(template, file);
                }
            }

            List<@Nullable RawWorker> rawEntries = JSONParser.fromJson(Files.readString(file), RawWorker.TYPE);
            if (rawEntries == null) {
                return Collections.emptyList();
            }

            return rawEntries.stream().filter(Objects::nonNull).toList();
        } catch (IOException e) {
            LOGGER.error("[UniChat Adapter] Failed to read workers from '{}'", FILE_NAME, e);

            return Collections.emptyList();
        }
    }

    public static void write(List<RawWorker> workers) {
        try {
            Files.writeString(file(), JSONParser.toPrettyJson(workers));
        } catch (IOException e) {
            LOGGER.error("[UniChat Adapter] Failed to write workers to '{}'", FILE_NAME, e);
        }
    }

    public static List<Worker> load(List<RawWorker> rawEntries) {
        List<Worker> workers = new ArrayList<>();
        for (int i = 0; i < rawEntries.size(); i++) {
            RawWorker entry = rawEntries.get(i);

            try {
                workers.add(RawWorkerParser.parse(entry));
            } catch (Exception e) {
                LOGGER.error("[UniChat Adapter] An error occurred on load worker #{}", i, e);
            }
        }

        LOGGER.info("[UniChat Adapter] Loaded {} workers", workers.size());

        return Collections.unmodifiableList(workers);
    }

    /* ====================================================================== */

    private WorkerLoader() {
        throw new UnsupportedOperationException("Utility class");
    }

}
