/*!******************************************************************************
 * Copyright (c) 2026 Voguh
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

package me.voguh.unichat.adapter.gui.chat.image;

import net.minecraft.client.Minecraft;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum ImageStore {
    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageStore.class);

    private static final long FAILURE_TTL_MILLIS = Duration.ofMinutes(5).toMillis();
    private static final String PROXY_PREFIX = "/proxy/";
    private static final String WEBP_SUFFIX = ".webp";

    private final Map<String, Long> failedAt;
    private final HttpClient client;

    /* ====================================================================== */

    private ImageStore() {
        this.failedAt = new ConcurrentHashMap<>();
        this.client = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(5))
            .build();
    }

    public Path fetch(String url) throws IOException {
        Path file = fileFor(url);
        if (Files.isRegularFile(file)) {
            return file;
        }

        if (isFailing(url)) {
            throw new IOException("recently failed: " + url);
        }

        try {
            return download(url);
        } catch (IOException e) {
            failedAt.put(url, System.currentTimeMillis());
            throw e;
        }
    }

    public void reject(String url) {
        failedAt.put(url, System.currentTimeMillis());

        try {
            Files.deleteIfExists(fileFor(url));
        } catch (IOException e) {
            LOGGER.warn("[UniChat Adapter] Failed to delete cache entry for '{}'", url, e);
        }
    }

    /* ====================================================================== */

    private Path download(String url) throws IOException {
        boolean proxied = url.startsWith(PROXY_PREFIX);
        String target = proxied ? proxyTarget(url) : url;
        String referer = proxied ? proxyReferer(url) : null;

        byte[] data = get(primary(target), referer);
        if (data == null) {
            String fallback = fallback(target);
            data = fallback == null ? null : get(fallback, referer);
        }

        if (data == null) {
            throw new IOException("no usable variant for " + target);
        }

        return store(url, data);
    }

    private byte @Nullable [] get(String url, @Nullable String referer) throws IOException {
        HttpRequest request;

        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(url)).timeout(Duration.ofSeconds(10));
            if (referer != null) {
                builder.header("Referer", referer);
            }

            request = builder.build();
        } catch (IllegalArgumentException e) {
            throw new IOException(e);
        }

        try {
            HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

            return response.statusCode() == 200 ? response.body() : null;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException(e);
        }
    }

    private Path store(String key, byte[] data) throws IOException {
        Path file = fileFor(key);
        Files.createDirectories(file.getParent());

        // Readers check the cache with isRegularFile from another thread, so the entry has to appear
        // whole or not at all.
        Path temp = Files.createTempFile(file.getParent(), file.getFileName().toString(), ".tmp");

        try {
            Files.write(temp, data);
            Files.move(temp, file, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            Files.deleteIfExists(temp);
            throw e;
        }

        return file;
    }

    private boolean isFailing(String url) {
        Long when = failedAt.get(url);
        if (when == null) {
            return false;
        }

        if (System.currentTimeMillis() - when < FAILURE_TTL_MILLIS) {
            return true;
        }

        failedAt.remove(url, when);

        return false;
    }

    private Path fileFor(String url) {
        return Minecraft.getInstance().gameDirectory.toPath().resolve("unichat_adapter").resolve("cache").resolve(sha1(url));
    }

    /* ====================================================================== */

    private static String proxyTarget(String url) {
        String path = url.substring(PROXY_PREFIX.length());
        int query = path.indexOf('?');
        String encoded = query < 0 ? path : path.substring(0, query);
        String decoded = new String(Base64.getUrlDecoder().decode(encoded), StandardCharsets.UTF_8);

        return normalize(decoded);
    }

    private static String normalize(String url) {
        if (url.startsWith("//")) {
            return "https:" + url;
        }

        if (url.startsWith("http://")) {
            return url.replaceFirst("^http://", "https://");
        }

        if (!url.startsWith("https://")) {
            return "https://" + url;
        }

        return url;
    }

    private static @Nullable String proxyReferer(String url) {
        int query = url.indexOf('?');
        if (query < 0) {
            return null;
        }

        for (String pair : url.substring(query + 1).split("&")) {
            if (pair.startsWith("referer=")) {
                return URLDecoder.decode(pair.substring("referer=".length()), StandardCharsets.UTF_8);
            }
        }

        return null;
    }

    private static String primary(String url) {
        // ImageIO has no WebP reader; the .gif comes before the .png because it is the only fallback
        // that preserves animation.
        return url.endsWith(WEBP_SUFFIX) ? withSuffix(url, ".gif") : url;
    }

    private static @Nullable String fallback(String url) {
        return url.endsWith(WEBP_SUFFIX) ? withSuffix(url, ".png") : null;
    }

    private static String withSuffix(String url, String suffix) {
        return url.substring(0, url.length() - WEBP_SUFFIX.length()) + suffix;
    }

    private static String sha1(String url) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-1").digest(url.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

}
