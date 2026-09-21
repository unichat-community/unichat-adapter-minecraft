/*!******************************************************************************
 * Copyright (c) 2026 Voguh
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

package me.voguh.unichat.adapter.gui.chat;

import me.voguh.unichat.adapter.client.ClientConfig;
import me.voguh.unichat.adapter.gui.chat.image.DecodedImage;
import me.voguh.unichat.adapter.gui.chat.image.ImageTextures;
import me.voguh.unichat.adapter.gui.chat.image.decoder.ImageDecoder;
import me.voguh.unichat.adapter.network.packet.ChatImage;
import me.voguh.unichat.adapter.network.packet.server.SendChatMessagePayload;
import me.voguh.unichat.adapter.store.ImageStore;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

public enum ChatMessages {
    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatMessages.class);

    private static final Pattern COLOR_PATTERN = Pattern.compile("^#[0-9a-fA-F]{6}$");
    private static final int DEFAULT_COLOR = 0xFFFFFF;
    private static final int WORKERS = 5;

    private final List<SendChatMessagePayload> queue;
    private final ExecutorService imagesExecutor;

    /* ====================================================================== */

    private ChatMessages() {
        this.queue = new ArrayList<>();
        this.imagesExecutor = Executors.newFixedThreadPool(WORKERS, Thread.ofPlatform().daemon().name("unichat-image-", 0).factory());
    }

    public void accept(SendChatMessagePayload payload) {
        if (!ClientConfig.renderMessages()) {
            return;
        }

        boolean idle;
        synchronized (queue) {
            queue.add(payload);
            idle = queue.size() == 1;
        }

        if (idle) {
            CompletableFuture.runAsync(() -> processNext(payload));
        }
    }

    public void clear() {
        synchronized (queue) {
            queue.clear();
        }
    }

    public void reloadImages() {
        List<String> urls = ImageTextures.INSTANCE.urls();

        CompletableFuture.runAsync(() -> {
            Queue<Loaded> loaded = new ConcurrentLinkedQueue<>();
            CountDownLatch done = new CountDownLatch(urls.size());

            for (String url : urls) {
                imagesExecutor.execute(() -> {
                    Loaded value = decode(url);
                    if (value != null) {
                        loaded.add(value);
                    }

                    done.countDown();
                });
            }

            try {
                done.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            Minecraft.getInstance().execute(() -> {
                for (Loaded value : loaded) {
                    ImageTextures.INSTANCE.upload(value.url(), value.file(), value.decoded());
                }
            });
        });
    }

    /* ====================================================================== */

    private void processNext(SendChatMessagePayload payload) {
        List<ChatImage> pending = new ArrayList<>(payload.authorBadges().size() + payload.emotes().size());
        pending.addAll(payload.authorBadges());
        pending.addAll(payload.emotes());

        Queue<Loaded> loaded = new ConcurrentLinkedQueue<>();
        CountDownLatch done = new CountDownLatch(pending.size());

        for (ChatImage image : pending) {
            imagesExecutor.execute(() -> {
                Loaded value = load(image.url());
                if (value != null) {
                    loaded.add(value);
                }

                done.countDown();
            });
        }

        try {
            done.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Minecraft.getInstance().execute(() -> {
            for (Loaded value : loaded) {
                ImageTextures.INSTANCE.upload(value.url(), value.file(), value.decoded());
            }

            MessageComposer.emit(buildMessage(payload));
        });

        SendChatMessagePayload next;
        synchronized (queue) {
            queue.removeFirst();
            next = queue.isEmpty() ? null : queue.getFirst();
        }

        if (next != null) {
            processNext(next);
        }
    }

    private static @Nullable Loaded load(String url) {
        if (ImageTextures.INSTANCE.has(url)) {
            return null;
        }

        return decode(url);
    }

    private static @Nullable Loaded decode(String url) {
        try {
            Path file = ImageStore.INSTANCE.retrieve(url);
            return new Loaded(url, file, ImageDecoder.decode(file));
        } catch (IOException e) {
            LOGGER.warn("[UniChat Adapter] Failed to load image '{}'", url, e);
            return null;
        }
    }

    /* ====================================================================== */

    private static ChatMessage buildMessage(SendChatMessagePayload payload) {
        Component author = buildAuthor(payload.authorDisplayName(), payload.authorDisplayColor());

        return new ChatMessage(author, payload.authorBadges(), parseMessageSegments(payload.messageText(), payload.emotes()));
    }

    private static Component buildAuthor(String name, String color) {
        return Component.literal(name).withStyle(style -> style.withColor(parseAuthorColor(color)));
    }

    private static int parseAuthorColor(String raw) {
        if (!COLOR_PATTERN.matcher(raw).matches()) {
            return DEFAULT_COLOR;
        }

        return Integer.parseInt(raw.substring(1), 16);
    }

    private static List<MessageSegment> parseMessageSegments(String text, List<ChatImage> emotes) {
        Map<String, String> emotesByCode = new HashMap<>();
        for (ChatImage emote : emotes) {
            emotesByCode.put(emote.code(), emote.url());
        }

        List<MessageSegment> segments = new ArrayList<>();
        for (String token : withoutFormatting(text).split("[\\s\\p{Z}]+")) {
            if (token.isBlank()) {
                continue;
            }

            String url = emotesByCode.get(token);
            segments.add(url == null ? new MessageSegment.Text(token) : new MessageSegment.Image(token, url));
        }

        return segments;
    }

    private static String withoutFormatting(String raw) {
        return raw.replace(ChatFormatting.PREFIX_CODE, ' ');
    }

    private record Loaded(String url, Path file, DecodedImage decoded) {

    }

}
