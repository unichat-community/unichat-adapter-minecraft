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
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;

public final class ImageTexture {

    private final ResourceLocation id;
    private final int[] delays;
    private final int frameWidth;
    private final int frameHeight;
    private final int atlasHeight;
    private final int cycleMillis;
    private final long startedAt;

    /* ====================================================================== */

    public ResourceLocation id() {
        return id;
    }

    public int width() {
        return frameWidth;
    }

    public int height() {
        return frameHeight;
    }

    public int atlasHeight() {
        return atlasHeight;
    }

    /* ====================================================================== */

    public ImageTexture(ResourceLocation id, int[] delays, int frameWidth, int atlasHeight) {
        this.id = id;
        this.delays = delays;
        this.frameWidth = frameWidth;
        this.frameHeight = atlasHeight / delays.length;
        this.atlasHeight = atlasHeight;
        this.cycleMillis = Arrays.stream(delays).sum();
        this.startedAt = System.currentTimeMillis();
    }

    /* ====================================================================== */

    public float frameOffset() {
        if (delays.length == 1) {
            return 0.0F;
        }

        long elapsed = (System.currentTimeMillis() - startedAt) % cycleMillis;
        int cursor = 0;

        for (int index = 0; index < delays.length; index++) {
            cursor += delays[index];

            if (elapsed < cursor) {
                return (float) index * frameHeight;
            }
        }

        return (float) (delays.length - 1) * frameHeight;
    }

    public void release() {
        Minecraft.getInstance().getTextureManager().release(id);
    }

}
