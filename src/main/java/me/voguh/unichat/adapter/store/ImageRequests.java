package me.voguh.unichat.adapter.store;

import me.voguh.unichat.adapter.network.UniChatNetwork;
import me.voguh.unichat.adapter.network.packet.client.RequestImagePayload;
import me.voguh.unichat.adapter.server.ServerConfig;
import me.voguh.unichat.adapter.util.Base64Utils;
import me.voguh.unichat.adapter.util.Strings;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public enum ImageRequests {
    INSTANCE;

    private static final long REQUEST_TIMEOUT = 10;
    private static final long FAILURE_TTL_MILLIS = Duration.ofMinutes(5).toMillis();
    private static final String PROXY_PREFIX = "/proxy/";

    private final Map<String, Long> failedAt;
    private final Map<String, Consumer<byte[]>> proxiedPendingRequests;
    private final HttpClient client;

    /* ====================================================================== */

    private ImageRequests() {
        this.failedAt = new ConcurrentHashMap<>();
        this.proxiedPendingRequests = new ConcurrentHashMap<>();
        this.client = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(5))
            .build();
    }

    /* ====================================================================== */

    public void onServerImageResponse(String path, byte[] data) {
        Consumer<byte[]> callback = proxiedPendingRequests.remove(path);
        if (callback != null) {
            callback.accept(data);
        }
    }

    /* ====================================================================== */

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

    public byte @Nullable [] clientFetch(String url) throws IOException {
        try {
            if (isFailing(url)) {
                throw new IOException("recently failed: " + url);
            }

            URI uri = URI.create(url);
            if (uri.getHost() == null && !uri.getPath().startsWith(PROXY_PREFIX)) {
                return proxiedFetch(uri.getPath());
            } else {
                return remoteFetch(uri);
            }
        } catch (IOException e) {
            failedAt.putIfAbsent(url, System.currentTimeMillis());
            throw e;
        }
    }

    private byte[] remoteFetch(URI url) throws IOException {
        String target = null;
        String referer = null;
        if (url.getHost() == null && url.getRawPath().startsWith(PROXY_PREFIX)) {
            String path = url.getRawPath().substring(PROXY_PREFIX.length());
            String[] split = path.split("\\?");
            String encoded = split[0];
            String queryString = split.length > 1 ? split[1] : null;

            String decoded = new String(Base64Utils.decode(encoded), StandardCharsets.UTF_8);
            target = Strings.normalizeUrl(decoded);
            if (queryString != null) {
                Map<String, String> queryParams = Strings.parseQueryString(queryString);
                referer = queryParams.get("referer");
            }
        } else {
            target = url.toString();
        }

        return regularFetch(target, referer);
    }

    private byte[] proxiedFetch(String path) throws IOException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<byte[]> resultRef = new AtomicReference<>();
        proxiedPendingRequests.putIfAbsent(path, (bytes) -> {
            resultRef.set(bytes);
            latch.countDown();
        });
        UniChatNetwork.sendToServer(new RequestImagePayload(path));

        try {
            if (!latch.await(REQUEST_TIMEOUT, TimeUnit.SECONDS)) {
                throw new IOException("Timeout waiting for image response");
            }

            return resultRef.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Interrupted while waiting for image response", e);
        }
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

            return regularFetch(normalized, null);
        } catch (IOException e) {
            failedAt.putIfAbsent(url, System.currentTimeMillis());
            throw e;
        }
    }

    /* ====================================================================== */

    private byte[] regularFetch(String url, @Nullable String referer) throws IOException {
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
