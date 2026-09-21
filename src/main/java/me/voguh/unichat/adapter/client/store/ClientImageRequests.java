/*!******************************************************************************
 * Copyright (c) 2026 Voguh
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

package me.voguh.unichat.adapter.client.store;

import me.voguh.unichat.adapter.network.UniChatNetwork;
import me.voguh.unichat.adapter.network.packet.client.RequestImagePayload;
import me.voguh.unichat.adapter.util.Base64Utils;
import me.voguh.unichat.adapter.util.ImageRequests;
import me.voguh.unichat.adapter.util.Strings;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public enum ClientImageRequests {
    INSTANCE;

    private static final long REQUEST_TIMEOUT = 10;
    private static final String PROXY_PREFIX = "/proxy/";

    private final Map<String, Consumer<byte[]>> proxiedPendingRequests;

    /* ====================================================================== */

    private ClientImageRequests() {
        this.proxiedPendingRequests = new ConcurrentHashMap<>();
    }

    /* ====================================================================== */

    public void onServerImageResponse(String path, byte[] data) {
        Consumer<byte[]> callback = proxiedPendingRequests.remove(path);
        if (callback != null) {
            callback.accept(data);
        }
    }

    /* ====================================================================== */

    public byte @Nullable [] fetch(String url) throws IOException {
        try {
            if (ImageRequests.INSTANCE.isFailing(url)) {
                throw new IOException("recently failed: " + url);
            }

            URI uri = URI.create(url);
            if (uri.getHost() == null && !uri.getPath().startsWith(PROXY_PREFIX)) {
                return proxiedFetch(uri.getPath());
            } else {
                return remoteFetch(uri);
            }
        } catch (IOException e) {
            ImageRequests.INSTANCE.markFailed(url);
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

        return ImageRequests.INSTANCE.fetch(target, referer);
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

}
