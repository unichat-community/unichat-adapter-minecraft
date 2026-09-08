/*!******************************************************************************
 * Copyright (c) 2026 Voguh
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

package me.voguh.unichat.adapter.gui;

import me.voguh.unichat.adapter.UniChatAdapter;
import me.voguh.unichat.adapter.client.ServerStateHolder;
import me.voguh.unichat.adapter.gui.component.CustomButton;
import me.voguh.unichat.adapter.gui.component.CustomEditBox;
import me.voguh.unichat.adapter.gui.component.CustomSwitch;
import me.voguh.unichat.adapter.network.UniChatNetwork;
import me.voguh.unichat.adapter.network.packet.client.ToggleWebSocketConnectionPayload;
import me.voguh.unichat.adapter.network.packet.client.UpdateServerSettingsPayload;
import me.voguh.unichat.adapter.util.ConnectionStatus;
import me.voguh.unichat.adapter.util.IdentifierUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public final class UniChatServerSettingsScreen extends Screen {

    private static final Identifier PANEL = IdentifierUtils.getIdentifier("panel/background");

    private static final int SPACING = 8;
    private static final int MARGIN = 16;

    private static final int PANEL_WIDTH = 225;
    private static final int PANEL_HEIGHT = 200;

    private static final int INNER_WIDTH = PANEL_WIDTH - MARGIN * 2;

    private static final int TITLE_COLOR = 0xFFFFFFFF;

    /* ====================================================================== */

    private String websocketUrl;
    private Boolean autoConnect;
    private int left;
    private int top;

    private final Screen parent;

    /* ====================================================================== */

    public UniChatServerSettingsScreen(Screen parent) {
        super(Component.translatable("gui." + UniChatAdapter.MODID + ".screen_server_settings"));
        this.websocketUrl = ServerStateHolder.INSTANCE.websocketUrl();
        this.autoConnect = ServerStateHolder.INSTANCE.autoConnect();

        this.parent = parent;
    }

    /* ====================================================================== */

    @Override
    protected void init() {
        super.init();
        left = (width - PANEL_WIDTH) / 2;
        top = (height - PANEL_HEIGHT) / 2;

        int glyph = font.lineHeight - 1;
        int titleSpacingY = glyph + SPACING;
        int xPos = left + MARGIN;
        int yPos = top + MARGIN + titleSpacingY;

        /* ================================================================== */

        addRenderableOnly(new StringWidget(
            xPos, yPos,
            INNER_WIDTH, font.lineHeight,
            Component.translatable("gui." + UniChatAdapter.MODID + ".screen_server_settings.websocket_url"),
            font
        ));

        /* ================================================================== */

        yPos += font.lineHeight;
        addRenderableWidget(
            new CustomEditBox(font,
                xPos, yPos,
                INNER_WIDTH,
                Component.translatable("gui." + UniChatAdapter.MODID + ".screen_server_settings.websocket_url"),
                this::onWebsocketUrlChange,
                websocketUrl
            )
        );

        /* ================================================================== */

        yPos += CustomEditBox.HEIGHT + SPACING;
        addRenderableWidget(
            new CustomSwitch(font,
                xPos, yPos,
                INNER_WIDTH,
                Component.translatable("gui." + UniChatAdapter.MODID + ".screen_server_settings.auto_connect"),
                this::onAutoConnectChange,
                autoConnect
            )
        );

        /* ================================================================== */

        ConnectionStatus status = ServerStateHolder.INSTANCE.connectionStatus();
        yPos = top + PANEL_HEIGHT - (CustomButton.HEIGHT + SPACING) - (CustomSwitch.HEIGHT + MARGIN);

        if (status == ConnectionStatus.CONNECTED) {
            addRenderableWidget(
                new CustomButton(font,
                    xPos, yPos,
                    INNER_WIDTH,
                    CustomButton.Variant.DANGER,
                    Component.translatable("gui." + UniChatAdapter.MODID + ".screen_server_settings.disconnect"),
                    (btn) -> toggleConnection(btn, status)
                )
            );
        } else if (status == ConnectionStatus.DISCONNECTED) {
            addRenderableWidget(
                new CustomButton(font,
                    xPos, yPos,
                    INNER_WIDTH,
                    CustomButton.Variant.SUCCESS,
                    Component.translatable("gui." + UniChatAdapter.MODID + ".screen_server_settings.connect"),
                    (btn) -> toggleConnection(btn, status)
                )
            );
        }

        /* ================================================================== */

        int btnWidth = (INNER_WIDTH / 2) - (SPACING / 2);

        yPos = top + PANEL_HEIGHT - (CustomSwitch.HEIGHT + MARGIN);
        addRenderableWidget(
            new CustomButton(font,
                xPos, yPos,
                btnWidth,
                CustomButton.Variant.SUCCESS,
                CommonComponents.GUI_DONE,
                this::apply
            )
        );

        xPos += btnWidth + SPACING;
        addRenderableWidget(
            new CustomButton(font,
                xPos, yPos,
                btnWidth,
                CommonComponents.GUI_BACK,
                this::cancel
            )
        );
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(graphics, mouseX, mouseY, partialTick);

        /* ================================================================== */

        int xPos = left;
        int yPos = top;

        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, PANEL, xPos, yPos, PANEL_WIDTH, PANEL_HEIGHT);

        /* ================================================================== */

        int titleX = left + MARGIN + (INNER_WIDTH / 2);
        int titleY = top + MARGIN;
        graphics.drawCenteredString(font, getTitle(), titleX, titleY, TITLE_COLOR);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parent);
    }

    /* ====================================================================== */

    private void cancel(CustomButton button) {
        onClose();
    }

    private void apply(CustomButton button) {
        UniChatNetwork.INSTANCE.sendToServer(new UpdateServerSettingsPayload(websocketUrl, autoConnect));
        ServerStateHolder.INSTANCE.setSettings(websocketUrl, autoConnect);
        onClose();
    }

    /* ====================================================================== */

    private void onWebsocketUrlChange(CustomEditBox editBox, String newValue) {
        websocketUrl = newValue;
    }

    private void onAutoConnectChange(CustomSwitch checkbox, boolean newValue) {
        autoConnect = newValue;
    }

    private void toggleConnection(CustomButton button, ConnectionStatus currentStatus) {
        if (currentStatus == ConnectionStatus.CONNECTED) {
            UniChatNetwork.INSTANCE.sendToServer(new ToggleWebSocketConnectionPayload(false));
            ServerStateHolder.INSTANCE.setConnectionStatus(ConnectionStatus.DISCONNECTED);
        } else if (currentStatus == ConnectionStatus.DISCONNECTED) {
            UniChatNetwork.INSTANCE.sendToServer(new ToggleWebSocketConnectionPayload(true));
            ServerStateHolder.INSTANCE.setConnectionStatus(ConnectionStatus.CONNECTING);
        }

        onClose();
    }

}
