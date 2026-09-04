/*!******************************************************************************
 * Copyright (c) 2026 Voguh
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

package me.voguh.unichat.adapter.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record ChatImage(String code, String url) {

    public static final StreamCodec<ByteBuf, ChatImage> CODEC = StreamCodec.composite(
        ByteBufCodecs.STRING_UTF8,
        ChatImage::code,
        ByteBufCodecs.STRING_UTF8,
        ChatImage::url,
        ChatImage::new
    );

}
