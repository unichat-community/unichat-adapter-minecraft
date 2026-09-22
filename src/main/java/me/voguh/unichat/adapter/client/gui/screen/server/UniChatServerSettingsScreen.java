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
import me.voguh.unichat.adapter.client.gui.screen.UniChatPanelScreen;
import me.voguh.unichat.adapter.network.UniChatNetwork;
import me.voguh.unichat.adapter.network.packet.client.ToggleWebSocketConnectionPayload;
import me.voguh.unichat.adapter.network.packet.client.UpdateServerSettingsPayload;
import me.voguh.unichat.adapter.util.ConnectionStatus;
import me.voguh.unichat.adapter.util.IdentifierUtils;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public final class UniChatServerSettingsScreen extends UniChatPanelScreen {

    private static final int URL_MAX_LENGTH = 256;

    private String websocketUrl;
    private boolean autoConnect;
    private Button connectionButton;

    /* ====================================================================== */

    public UniChatServerSettingsScreen(Screen parent) {
        super(IdentifierUtils.translatable("screen_server_settings"), parent);
        this.websocketUrl = ServerStateHolder.INSTANCE.websocketUrl();
        this.autoConnect = ServerStateHolder.INSTANCE.autoConnect();
    }

    /* ====================================================================== */

    @Override
    protected void addContents(LinearLayout layout) {
        Component websocketUrlLabel = IdentifierUtils.translatable("screen_server_settings.websocket_url");
        layout.addChild(new StringWidget(CONTENT_WIDTH, font.lineHeight, websocketUrlLabel, font).alignLeft());

        /* ================================================================== */

        EditBox websocketUrlBox = new EditBox(font, 0, 0, CONTENT_WIDTH, Button.DEFAULT_HEIGHT, websocketUrlLabel);
        // setValue truncates to maxLength, which defaults to 32.
        websocketUrlBox.setMaxLength(URL_MAX_LENGTH);
        websocketUrlBox.setValue(websocketUrl);
        websocketUrlBox.setResponder(value -> websocketUrl = value);
        layout.addChild(websocketUrlBox);

        /* ================================================================== */

        Component autoConnectLabel = IdentifierUtils.translatable("screen_server_settings.auto_connect");
        Checkbox autoConnectBox = Checkbox.builder(autoConnectLabel, font)
            .selected(autoConnect)
            .maxWidth(CONTENT_WIDTH)
            .onValueChange(this::onAutoConnectChange)
            .build();
        layout.addChild(autoConnectBox);

        /* ================================================================== */

        connectionButton = Button.builder(CommonComponents.EMPTY, this::toggleConnection).width(CONTENT_WIDTH).build();
        refreshConnectionButton();
        layout.addChild(connectionButton);

        /* ================================================================== */

        LinearLayout actions = LinearLayout.horizontal().spacing(SPACING);
        actions.addChild(Button.builder(IdentifierUtils.translatable("save"), this::apply).width(HALF_WIDTH).build());
        actions.addChild(Button.builder(CommonComponents.GUI_BACK, this::cancel).width(HALF_WIDTH).build());

        layout.addChild(actions, (settings) -> settings.paddingTop(SPACING));
    }

    @Override
    public void tick() {
        refreshConnectionButton();
    }

    /* ====================================================================== */

    private void refreshConnectionButton() {
        ConnectionStatus status = ServerStateHolder.INSTANCE.connectionStatus();
        connectionButton.setMessage(connectionLabel(status));
        connectionButton.active = status != ConnectionStatus.CONNECTING;
    }

    private Component connectionLabel(ConnectionStatus status) {
        return switch (status) {
            case CONNECTED -> IdentifierUtils.translatable("screen_server_settings.disconnect");
            case CONNECTING -> IdentifierUtils.translatable("status_connecting");
            case DISCONNECTED -> IdentifierUtils.translatable("screen_server_settings.connect");
        };
    }

    /* ====================================================================== */

    private void cancel(Button button) {
        onClose();
    }

    private void apply(Button button) {
        UniChatNetwork.sendToServer(new UpdateServerSettingsPayload(websocketUrl, autoConnect));
        ServerStateHolder.INSTANCE.setSettings(websocketUrl, autoConnect);
        onClose();
    }

    /* ====================================================================== */

    private void onAutoConnectChange(Checkbox checkbox, boolean newValue) {
        autoConnect = newValue;
    }

    private void toggleConnection(Button button) {
        ConnectionStatus status = ServerStateHolder.INSTANCE.connectionStatus();
        if (status == ConnectionStatus.CONNECTED) {
            UniChatNetwork.sendToServer(new ToggleWebSocketConnectionPayload(false));
            ServerStateHolder.INSTANCE.setConnectionStatus(ConnectionStatus.DISCONNECTED);
        } else if (status == ConnectionStatus.DISCONNECTED) {
            UniChatNetwork.sendToServer(new ToggleWebSocketConnectionPayload(true));
            ServerStateHolder.INSTANCE.setConnectionStatus(ConnectionStatus.CONNECTING);
        }
    }

}
