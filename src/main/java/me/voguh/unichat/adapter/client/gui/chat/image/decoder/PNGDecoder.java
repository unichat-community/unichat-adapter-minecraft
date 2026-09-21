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

public final class PNGDecoder extends AbstractDecoder {

    public PNGDecoder(ImageReader reader) {
        super(reader);
    }

    @Override
    protected Frame frame(int index) throws IOException {
        return new Frame(reader.read(index), DEFAULT_DELAY_MILLIS);
    }

}
