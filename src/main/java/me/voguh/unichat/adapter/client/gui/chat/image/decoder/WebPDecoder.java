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

import javax.imageio.ImageReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public final class WebPDecoder extends AbstractDecoder {

    private static final int RIFF_HEADER_BYTES = 12;
    private static final int CHUNK_HEADER_BYTES = 8;
    private static final int ANMF_DURATION_OFFSET = 12;

    private final int[] durations;

    /* ====================================================================== */

    public WebPDecoder(ImageReader reader, Path file) throws IOException {
        super(reader);
        this.durations = durations(Files.readAllBytes(file));
    }

    @Override
    protected Frame frame(int index) throws IOException {
        return new Frame(reader.read(index), index < durations.length ? durations[index] : DEFAULT_DELAY_MILLIS);
    }

    /* ====================================================================== */

    private static int[] durations(byte[] data) {
        int[] found = new int[MAX_FRAMES];
        int count = 0;
        int offset = RIFF_HEADER_BYTES;

        while (offset + CHUNK_HEADER_BYTES <= data.length && count < found.length) {
            int size = uint32(data, offset + 4);
            int payload = offset + CHUNK_HEADER_BYTES;
            if (size < 0 || payload + size > data.length) {
                break;
            }

            if (isAnimationFrame(data, offset) && size >= ANMF_DURATION_OFFSET + 3) {
                found[count++] = uint24(data, payload + ANMF_DURATION_OFFSET);
            }

            offset = payload + size + (size & 1);
        }

        return Arrays.copyOf(found, count);
    }

    private static boolean isAnimationFrame(byte[] data, int offset) {
        return data[offset] == 'A' && data[offset + 1] == 'N' && data[offset + 2] == 'M' && data[offset + 3] == 'F';
    }

    private static int uint24(byte[] data, int offset) {
        return (data[offset] & 0xFF) | (data[offset + 1] & 0xFF) << 8 | (data[offset + 2] & 0xFF) << 16;
    }

    private static int uint32(byte[] data, int offset) {
        return uint24(data, offset) | (data[offset + 3] & 0xFF) << 24;
    }

}
