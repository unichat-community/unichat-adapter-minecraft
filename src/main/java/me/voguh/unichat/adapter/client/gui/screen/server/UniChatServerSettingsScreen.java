/*!******************************************************************************
 * Copyright (c) 2026 Voguh
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

package me.voguh.unichat.adapter.client.gui.screen.server;

import me.voguh.unichat.adapter.client.ServerStateHolder;
import me.voguh.unichat.adapter.client.gui.component.CustomButton;
import me.voguh.unichat.adapter.client.gui.component.CustomButton.Variant;
import me.voguh.unichat.adapter.client.gui.component.CustomCheckbox;
import me.voguh.unichat.adapter.client.gui.component.CustomEditBox;
import me.voguh.unichat.adapter.client.gui.screen.UniChatPanelScreen;
import me.voguh.unichat.adapter.network.UniChatNetwork;
import me.voguh.unichat.adapter.network.packet.client.ToggleWebSocketConnectionPayload;
import me.voguh.unichat.adapter.network.packet.client.UpdateServerSettingsPayload;
import me.voguh.unichat.adapter.util.ConnectionStatus;
import me.voguh.unichat.adapter.util.IdentifierUtils;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public final class UniChatServerSettingsScreen extends UniChatPanelScreen {

    private static final Component TITLE = IdentifierUtils.gui("screen_server_settings");
    private static final Component WEBSOCKET_URL_LABEL = IdentifierUtils.gui("screen_server_settings.websocket_url");
    private static final Component AUTO_CONNECT_LABEL = IdentifierUtils.gui("screen_server_settings.auto_connect");
    private static final Component CONNECT_LABEL = IdentifierUtils.gui("screen_server_settings.connect");
    private static final Component DISCONNECT_LABEL = IdentifierUtils.gui("screen_server_settings.disconnect");

    private static final int URL_MAX_LENGTH = 256;

    private String websocketUrl;
    private boolean autoConnect;

    /* ====================================================================== */

    public UniChatServerSettingsScreen(Screen parent) {
        super(TITLE, parent);
        this.websocketUrl = ServerStateHolder.INSTANCE.websocketUrl();
        this.autoConnect = ServerStateHolder.INSTANCE.autoConnect();
    }

    /* ====================================================================== */

    @Override
    protected void addContents(LinearLayout layout) {
        layout.addChild(new CustomEditBox(font, CONTENT_WIDTH, WEBSOCKET_URL_LABEL, websocketUrl, this::onWebsocketUrlChange, URL_MAX_LENGTH));

        /* ================================================================== */

        layout.addChild(new CustomCheckbox(font, CONTENT_WIDTH, AUTO_CONNECT_LABEL, autoConnect, this::onAutoConnectChange));

        /* ================================================================== */

        ConnectionStatus status = ServerStateHolder.INSTANCE.connectionStatus();
        if (status == ConnectionStatus.CONNECTED) {
            layout.addChild(new CustomButton(font, CONTENT_WIDTH, DISCONNECT_LABEL, Variant.DANGER, (btn) -> toggleConnection(status)));
        } else if (status == ConnectionStatus.DISCONNECTED) {
            layout.addChild(new CustomButton(font, CONTENT_WIDTH, CONNECT_LABEL, Variant.SUCCESS, (btn) -> toggleConnection(status)));
        }

        /* ================================================================== */

        LinearLayout actions = LinearLayout.horizontal().spacing(SPACING);
        actions.addChild(new CustomButton(font, HALF_WIDTH, IdentifierUtils.GUI_SAVE, Variant.SUCCESS, this::apply));
        actions.addChild(new CustomButton(font, HALF_WIDTH, CommonComponents.GUI_BACK, this::cancel));

        layout.addChild(actions, (settings) -> settings.paddingTop(SPACING));
    }

    /* ====================================================================== */

    private void cancel(CustomButton button) {
        onClose();
    }

    private void apply(CustomButton button) {
        UniChatNetwork.sendToServer(new UpdateServerSettingsPayload(websocketUrl, autoConnect));
        ServerStateHolder.INSTANCE.setSettings(websocketUrl, autoConnect);
        onClose();
    }

    /* ====================================================================== */

    private void onWebsocketUrlChange(CustomEditBox editBox, String newValue) {
        websocketUrl = newValue;
    }

    private void onAutoConnectChange(CustomCheckbox checkbox, boolean newValue) {
        autoConnect = newValue;
    }

    private void toggleConnection(ConnectionStatus currentStatus) {
        if (currentStatus == ConnectionStatus.CONNECTED) {
            UniChatNetwork.sendToServer(new ToggleWebSocketConnectionPayload(false));
            ServerStateHolder.INSTANCE.setConnectionStatus(ConnectionStatus.DISCONNECTED);
        } else if (currentStatus == ConnectionStatus.DISCONNECTED) {
            UniChatNetwork.sendToServer(new ToggleWebSocketConnectionPayload(true));
            ServerStateHolder.INSTANCE.setConnectionStatus(ConnectionStatus.CONNECTING);
        }
    }

}
