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
import me.voguh.unichat.adapter.gui.chat.ImageFont;
import org.jspecify.annotations.Nullable;
import org.w3c.dom.Node;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;

public final class ImageDecoder {

    private static final int SUPERSAMPLE = 1;
    private static final int MAX_FRAMES = 200;
    private static final int DEFAULT_DELAY_MILLIS = 100;

    public static DecodedImage decode(Path file) throws IOException {
        // Handing ImageIO the File avoids the FileCacheImageInputStream, which copies the whole image
        // into a temporary file before decoding.
        try (ImageInputStream iis = ImageIO.createImageInputStream(file.toFile())) {
            Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
            if (!readers.hasNext()) {
                throw new IOException("no decoder for " + file);
            }

            ImageReader reader = readers.next();
            reader.setInput(iis);

            try {
                return read(reader);
            } finally {
                reader.dispose();
            }
        }
    }

    /* ====================================================================== */

    private static DecodedImage read(ImageReader reader) throws IOException {
        // A long animation is truncated instead of scaled down: the atlas is one texture, so its
        // height grows with the frame count and eventually passes GL_MAX_TEXTURE_SIZE.
        int count = Math.min(reader.getNumImages(true), MAX_FRAMES);
        BufferedImage first = reader.read(0);
        BufferedImage canvas = buildCanvas(reader, first);

        int[] delays = new int[count];
        int height = ImageFont.MAX_HEIGHT * SUPERSAMPLE;
        int width = Math.max(1, Math.round(height * canvas.getWidth() / (float) canvas.getHeight()));
        BufferedImage atlas = new BufferedImage(width, height * count, BufferedImage.TYPE_INT_ARGB);

        for (int index = 0; index < count; index++) {
            BufferedImage frame = index == 0 ? first : reader.read(index);
            IIOMetadata metadata = reader.getImageMetadata(index);
            Node root = metadata.getAsTree(metadata.getNativeMetadataFormatName());

            compose(canvas, frame, root);
            stack(atlas, canvas, width, height, index);
            delays[index] = delay(root);

            dispose(canvas, frame, root);
        }

        return new DecodedImage(toNativeImage(atlas), delays);
    }

    private static BufferedImage buildCanvas(ImageReader reader, BufferedImage first) throws IOException {
        IIOMetadata metadata = reader.getStreamMetadata();
        if (metadata == null) {
            return new BufferedImage(first.getWidth(), first.getHeight(), BufferedImage.TYPE_INT_ARGB);
        }

        Node screen = child(metadata.getAsTree(metadata.getNativeMetadataFormatName()), "LogicalScreenDescriptor");
        int width = attribute(screen, "logicalScreenWidth", first.getWidth());
        int height = attribute(screen, "logicalScreenHeight", first.getHeight());

        return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }

    private static void compose(BufferedImage canvas, BufferedImage frame, Node root) {
        Node descriptor = child(root, "ImageDescriptor");
        int left = attribute(descriptor, "imageLeftPosition", 0);
        int top = attribute(descriptor, "imageTopPosition", 0);

        Graphics2D graphics = canvas.createGraphics();
        graphics.drawImage(frame, left, top, null);
        graphics.dispose();
    }

    private static void dispose(BufferedImage canvas, BufferedImage frame, Node root) {
        Node control = child(root, "GraphicControlExtension");
        if (control == null || !"restoreToBackgroundColor".equals(text(control, "disposalMethod"))) {
            return;
        }

        Node descriptor = child(root, "ImageDescriptor");
        int left = attribute(descriptor, "imageLeftPosition", 0);
        int top = attribute(descriptor, "imageTopPosition", 0);

        Graphics2D graphics = canvas.createGraphics();
        graphics.setComposite(AlphaComposite.Clear);
        graphics.fillRect(left, top, frame.getWidth(), frame.getHeight());
        graphics.dispose();
    }

    private static int delay(Node root) {
        Node control = child(root, "GraphicControlExtension");
        if (control == null) {
            return DEFAULT_DELAY_MILLIS;
        }

        String raw = text(control, "delayTime");
        if (raw == null) {
            return DEFAULT_DELAY_MILLIS;
        }

        int centiseconds = Integer.parseInt(raw);

        return centiseconds <= 1 ? DEFAULT_DELAY_MILLIS : centiseconds * 10;
    }

    private static void stack(BufferedImage atlas, BufferedImage canvas, int width, int height, int index) {
        Graphics2D graphics = atlas.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics.drawImage(canvas, 0, index * height, width, height, null);
        graphics.dispose();
    }

    private static NativeImage toNativeImage(BufferedImage source) {
        NativeImage image = new NativeImage(source.getWidth(), source.getHeight(), false);

        for (int y = 0; y < source.getHeight(); y++) {
            for (int x = 0; x < source.getWidth(); x++) {
                int argb = source.getRGB(x, y);
                int abgr = (argb & 0xFF00FF00) | ((argb & 0x00FF0000) >> 16) | ((argb & 0x000000FF) << 16);
                image.setPixelABGR(x, y, abgr);
            }
        }

        return image;
    }

    private static @Nullable Node child(Node parent, String name) {
        for (Node node = parent.getFirstChild(); node != null; node = node.getNextSibling()) {
            if (name.equals(node.getNodeName())) {
                return node;
            }
        }

        return null;
    }

    private static @Nullable String text(Node node, String attribute) {
        Node value = node.getAttributes().getNamedItem(attribute);

        return value == null ? null : value.getNodeValue();
    }

    private static int attribute(@Nullable Node node, String name, int fallback) {
        String raw = node == null ? null : text(node, name);

        return raw == null ? fallback : Integer.parseInt(raw);
    }

    /* ====================================================================== */

    private ImageDecoder() {
        throw new IllegalStateException("Utility class");
    }

}
