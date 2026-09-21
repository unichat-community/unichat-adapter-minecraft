/*!******************************************************************************
 * Copyright (c) 2026 Voguh
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

package me.voguh.unichat.adapter.client.gui.chat;

public sealed interface MessageSegment permits MessageSegment.Text, MessageSegment.Image {

    record Text(String value) implements MessageSegment {

    }

    record Image(String code, String url) implements MessageSegment {

    }

}
