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

import com.mojang.blaze3d.platform.NativeImage;
import me.voguh.unichat.adapter.util.IdentifierUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import org.jspecify.annotations.Nullable;

import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public enum ImageTextures {
    INSTANCE;

    private static final int MAX_EMOTES = 256;

    private final Map<String, ImageTexture> uploaded;

    /* ====================================================================== */

    private ImageTextures() {
        this.uploaded = Collections.synchronizedMap(new LinkedHashMap<>(64, 0.75F, true) {

            @Override
            protected boolean removeEldestEntry(Map.Entry<String, ImageTexture> eldest) {
                if (size() <= MAX_EMOTES) {
                    return false;
                }

                eldest.getValue().release();

                return true;
            }
        });
    }

    public boolean has(String url) {
        return uploaded.containsKey(url);
    }

    public @Nullable ImageTexture get(String url) {
        return uploaded.get(url);
    }

    public List<String> urls() {
        synchronized (uploaded) {
            return List.copyOf(uploaded.keySet());
        }
    }

    public void upload(String url, Path file, DecodedImage decoded) {
        ResourceLocation id = IdentifierUtils.getIdentifier("image/" + file.getFileName());
        NativeImage atlas = decoded.atlas();
        DynamicTexture texture = new DynamicTexture(atlas);

        Minecraft.getInstance().getTextureManager().register(id, texture);
        uploaded.put(url, new ImageTexture(id, decoded.delays(), atlas.getWidth(), atlas.getHeight()));
    }

}
