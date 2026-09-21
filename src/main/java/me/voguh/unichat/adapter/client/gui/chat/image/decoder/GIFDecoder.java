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

import org.jspecify.annotations.Nullable;
import org.w3c.dom.Node;

import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

public final class GIFDecoder extends AbstractDecoder {

    private @Nullable Rectangle pending;

    private final BufferedImage canvas;

    /* ====================================================================== */

    public GIFDecoder(ImageReader reader) throws IOException {
        super(reader);
        this.canvas = buildCanvas(reader);
    }

    @Override
    protected Frame frame(int index) throws IOException {
        BufferedImage image = reader.read(index);
        Node root = root(index);
        Node descriptor = child(root, "ImageDescriptor");
        Node control = child(root, "GraphicControlExtension");
        int left = attribute(descriptor, "imageLeftPosition", 0);
        int top = attribute(descriptor, "imageTopPosition", 0);

        clear();
        draw(image, left, top);

        if (restoresBackground(control)) {
            pending = new Rectangle(left, top, image.getWidth(), image.getHeight());
        }

        return new Frame(canvas, delay(control));
    }

    /* ====================================================================== */

    private void clear() {
        if (pending == null) {
            return;
        }

        Graphics2D graphics = canvas.createGraphics();
        graphics.setComposite(AlphaComposite.Clear);
        graphics.fill(pending);
        graphics.dispose();

        pending = null;
    }

    private void draw(BufferedImage image, int left, int top) {
        Graphics2D graphics = canvas.createGraphics();
        graphics.drawImage(image, left, top, null);
        graphics.dispose();
    }

    private Node root(int index) throws IOException {
        IIOMetadata metadata = reader.getImageMetadata(index);

        return metadata.getAsTree(metadata.getNativeMetadataFormatName());
    }

    /* ====================================================================== */

    private static BufferedImage buildCanvas(ImageReader reader) throws IOException {
        IIOMetadata metadata = reader.getStreamMetadata();
        Node screen = child(metadata.getAsTree(metadata.getNativeMetadataFormatName()), "LogicalScreenDescriptor");
        int width = attribute(screen, "logicalScreenWidth", reader.getWidth(0));
        int height = attribute(screen, "logicalScreenHeight", reader.getHeight(0));

        return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }

    private static boolean restoresBackground(@Nullable Node control) {
        return control != null && text(control, "disposalMethod").equals("restoreToBackgroundColor");
    }

    private static int delay(@Nullable Node control) {
        if (control == null) {
            return DEFAULT_DELAY_MILLIS;
        }

        int centiseconds = attribute(control, "delayTime", 0);

        return centiseconds <= 1 ? DEFAULT_DELAY_MILLIS : centiseconds * 10;
    }

    private static @Nullable Node child(Node parent, String name) {
        for (Node node = parent.getFirstChild(); node != null; node = node.getNextSibling()) {
            if (name.equals(node.getNodeName())) {
                return node;
            }
        }

        return null;
    }

    private static String text(Node node, String attribute) {
        return node.getAttributes().getNamedItem(attribute).getNodeValue();
    }

    private static int attribute(@Nullable Node node, String name, int fallback) {
        return node == null ? fallback : Integer.parseInt(text(node, name));
    }

}
