package me.voguh.unichat.adapter.util;

import me.voguh.unichat.adapter.server.ServerConfig;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum ImageRequests {
    INSTANCE;

    private static final long REQUEST_TIMEOUT = 10;
    private static final long FAILURE_TTL_MILLIS = Duration.ofMinutes(5).toMillis();

    private final Map<String, Long> failedAt;
    private final HttpClient client;

    /* ====================================================================== */

    private ImageRequests() {
        this.failedAt = new ConcurrentHashMap<>();
        this.client = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(5))
            .build();
    }

    /* ====================================================================== */

    public boolean isFailing(String url) {
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

    public void markFailed(String url) {
        failedAt.putIfAbsent(url, System.currentTimeMillis());
    }

    /* ====================================================================== */

    public byte[] serverFetch(String url) throws IOException {
        try {
            if (isFailing(url)) {
                throw new IOException("recently failed: " + url);
            }

            URI wsURI = URI.create(ServerConfig.websocketUrl());
            String scheme = wsURI.getScheme().equals("wss") ? "https" : "http";
            String host = wsURI.getHost();
            String normalized = scheme + "://" + host + url;

            return fetch(normalized, null);
        } catch (IOException e) {
            markFailed(url);
            throw e;
        }
    }

    /* ====================================================================== */

    public byte[] fetch(String url, @Nullable String referer) throws IOException {
        try {
            HttpRequest.Builder request = HttpRequest.newBuilder(URI.create(url)).timeout(Duration.ofSeconds(REQUEST_TIMEOUT));
            if (referer != null) {
                request.header("Referer", referer);
            }

            HttpResponse<byte[]> response = client.send(request.build(), HttpResponse.BodyHandlers.ofByteArray());
            if (response.statusCode() != 200) {
                throw new IOException("failed to fetch " + url + ": " + response.statusCode());
            }

            return response.body();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException(e);
        }
    }

}
