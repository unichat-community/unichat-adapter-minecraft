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

import me.voguh.unichat.adapter.network.packet.ChatImage;
import net.minecraft.network.chat.Component;

import java.util.List;

public record ChatMessage(Component author, List<ChatImage> badges, List<MessageSegment> segments) {

}
