/*!******************************************************************************
 * Copyright (c) 2026 Voguh
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

package me.voguh.unichat.adapter.client.gui.chat.image.decoder;

import com.mojang.blaze3d.platform.NativeImage;
import me.voguh.unichat.adapter.client.ClientConfig;
import me.voguh.unichat.adapter.client.gui.chat.ImageFont;
import me.voguh.unichat.adapter.client.gui.chat.image.DecodedImage;

import javax.imageio.ImageReader;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;

public abstract class AbstractDecoder {

    protected static final int MAX_FRAMES = 200;
    protected static final int DEFAULT_DELAY_MILLIS = 100;

    private final int supersample;
    protected final ImageReader reader;

    /* ====================================================================== */

    protected AbstractDecoder(ImageReader reader) {
        this.supersample = ClientConfig.supersample();
        this.reader = reader;
    }

    /* ====================================================================== */

    public DecodedImage decode() throws IOException {
        int count = Math.min(reader.getNumImages(true), MAX_FRAMES);
        int[] delays = new int[count];

        Frame first = frame(0);
        int height = ImageFont.MAX_HEIGHT * supersample;
        int width = Math.max(1, Math.round(height * first.image().getWidth() / (float) first.image().getHeight()));
        BufferedImage atlas = new BufferedImage(width, height * count, BufferedImage.TYPE_INT_ARGB);

        stack(atlas, first, width, height, 0);
        delays[0] = first.delay();

        for (int index = 1; index < count; index++) {
            Frame frame = frame(index);

            stack(atlas, frame, width, height, index);
            delays[index] = frame.delay();
        }

        return new DecodedImage(toNativeImage(atlas), delays);
    }

    /* ====================================================================== */

    protected abstract Frame frame(int index) throws IOException;

    /* ====================================================================== */

    private static void stack(BufferedImage atlas, Frame frame, int width, int height, int index) {
        Graphics2D graphics = atlas.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics.drawImage(frame.image(), 0, index * height, width, height, null);
        graphics.dispose();
    }

    private static NativeImage toNativeImage(BufferedImage source) {
        NativeImage image = new NativeImage(source.getWidth(), source.getHeight(), false);

        for (int y = 0; y < source.getHeight(); y++) {
            for (int x = 0; x < source.getWidth(); x++) {
                int argb = source.getRGB(x, y);
                int abgr = (argb & 0xFF00FF00) | ((argb & 0x00FF0000) >> 16) | ((argb & 0x000000FF) << 16);
                image.setPixelRGBA(x, y, abgr);
            }
        }

        return image;
    }

    /* ====================================================================== */

    protected record Frame(BufferedImage image, int delay) {

    }

}
