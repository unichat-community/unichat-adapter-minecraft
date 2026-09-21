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

import me.voguh.unichat.adapter.client.gui.chat.image.DecodedImage;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;

public final class ImageDecoder {

    private static final String WEBP_FORMAT = "webp";
    private static final String GIF_FORMAT = "gif";

    public static DecodedImage decode(Path file) throws IOException {
        try (ImageInputStream iis = ImageIO.createImageInputStream(file.toFile())) {
            Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
            if (!readers.hasNext()) {
                throw new IOException("no decoder for " + file);
            }

            ImageReader reader = readers.next();
            reader.setInput(iis);

            try {
                return decoder(reader, file).decode();
            } finally {
                reader.dispose();
            }
        }
    }

    /* ====================================================================== */

    private static AbstractDecoder decoder(ImageReader reader, Path file) throws IOException {
        String format = reader.getFormatName();
        if (WEBP_FORMAT.equals(format)) {
            return new WebPDecoder(reader, file);
        } else if (GIF_FORMAT.equals(format)) {
            return new GIFDecoder(reader);
        }

        return new PNGDecoder(reader);
    }

    /* ====================================================================== */

    private ImageDecoder() {
        throw new UnsupportedOperationException("Utility class");
    }

}
