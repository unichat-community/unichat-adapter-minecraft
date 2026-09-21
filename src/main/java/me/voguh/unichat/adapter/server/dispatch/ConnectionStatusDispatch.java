/*!******************************************************************************
 * Copyright (c) 2026 Voguh
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

package me.voguh.unichat.adapter.server.dispatch;

import me.voguh.unichat.adapter.network.UniChatNetwork;
import me.voguh.unichat.adapter.network.packet.server.SendConnectionStatusPayload;
import me.voguh.unichat.adapter.util.ConnectionStatus;

public final class ConnectionStatusDispatch {

    public static void dispatch(ConnectionStatus status) {
        UniChatNetwork.sendToPlayers(new SendConnectionStatusPayload(status));
    }

    /* ====================================================================== */

    private ConnectionStatusDispatch() {
        throw new UnsupportedOperationException("Utility class");
    }

}
