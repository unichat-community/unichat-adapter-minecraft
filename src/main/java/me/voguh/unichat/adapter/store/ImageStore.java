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
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public enum ImageStore {
    INSTANCE;

    public Path retrieve(String url) throws IOException {
        Path file = resolveCachePath(url);
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

    /* ====================================================================== */

    private Path resolveCachePath(String url) {
        Path basePath = FMLPaths.GAMEDIR.get().resolve("unichat_adapter").resolve("cache");
        return basePath.resolve(HEXUtils.encode(Strings.sha1(url)));
    }

}
