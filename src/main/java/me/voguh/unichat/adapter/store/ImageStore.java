/*!******************************************************************************
 * Copyright (c) 2026 Voguh
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

package me.voguh.unichat.adapter.store;

import me.voguh.unichat.adapter.util.HEXUtils;
import me.voguh.unichat.adapter.util.Strings;
import net.neoforged.fml.loading.FMLPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.stream.Stream;

public enum ImageStore {
    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageStore.class);

    public Path retrieve(String url) throws IOException {
        Path file = resolveFileCachePath(url);
        if (Files.isRegularFile(file)) {
            return file;
        }

        byte[] data = ImageRequests.INSTANCE.clientFetch(url);
        if (data == null) {
            throw new IOException("no usable variant for " + url);
        }

        Files.createDirectories(file.getParent());
        Path temp = Files.createTempFile(file.getParent(), file.getFileName().toString(), ".tmp");
        Files.write(temp, data);
        Files.move(temp, file, StandardCopyOption.ATOMIC_MOVE);
        return file;
    }

    public void clear() {
        try {
            Path basePath = cachePath();
            if (Files.exists(basePath)) {
                try (Stream<Path> files = Files.walk(basePath)) {
                    files.sorted(Comparator.reverseOrder())
                        .forEach(path -> {
                            try {
                                Files.delete(path);
                            } catch (IOException e) {
                                LOGGER.error("[UniChat Adapter] Failed to delete file: {}", path, e);
                            }
                        });
                }
            }
        } catch (Exception e) {
            LOGGER.error("[UniChat Adapter] Failed to clear image cache", e);
        }
    }

    /* ====================================================================== */

    private Path cachePath() {
        return FMLPaths.GAMEDIR.get().resolve("unichat_adapter").resolve("cache");
    }

    private Path resolveFileCachePath(String url) {
        return cachePath().resolve(HEXUtils.encode(Strings.sha1(url)));
    }

}
