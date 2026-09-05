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
import me.voguh.unichat.adapter.client.ClientConfig;
import me.voguh.unichat.adapter.gui.component.CustomButton;
import me.voguh.unichat.adapter.gui.component.CustomSwitch;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public final class UniChatClientSettingsScreen extends Screen {

    private static final Identifier PANEL = Identifier.fromNamespaceAndPath(UniChatAdapter.MODID, "panel/background");

    private static final int SPACING = 8;
    private static final int MARGIN = 16;

    private static final int PANEL_WIDTH = 225;
    private static final int PANEL_HEIGHT = 100;

    private static final int INNER_WIDTH = PANEL_WIDTH - MARGIN * 2;

    private static final int BUTTON_HEIGHT = 20;

    private static final int TITLE_COLOR = 0xFFFFFFFF;

    /* ====================================================================== */

    private Boolean displayChatMessages;
    private int left;
    private int top;

    private final Screen parent;

    /* ====================================================================== */

    public UniChatClientSettingsScreen(Screen parent) {
        super(Component.translatable("gui." + UniChatAdapter.MODID + ".screen_client_settings"));
        this.displayChatMessages = ClientConfig.renderMessages();

        this.parent = parent;
    }

    /* ====================================================================== */

    @Override
    protected void init() {
        super.init();
        left = (width - PANEL_WIDTH) / 2;
        top = (height - PANEL_HEIGHT) / 2;

        int titleSpacingY = font.lineHeight + SPACING;
        int xPos = left + MARGIN;
        int yPos = top + MARGIN + titleSpacingY;

        /* ================================================================== */

        addRenderableWidget(
            new CustomSwitch(font,
                xPos, yPos,
                INNER_WIDTH, BUTTON_HEIGHT,
                Component.translatable("gui." + UniChatAdapter.MODID + ".screen_client_settings.display_chat_messages"),
                this::onDisplayChatMessages,
                displayChatMessages
            )
        );

        /* ================================================================== */

        int btnWidth = (INNER_WIDTH / 2) - (SPACING / 2);

        yPos = top + PANEL_HEIGHT - BUTTON_HEIGHT - MARGIN;
        addRenderableWidget(
            new CustomButton(font,
                xPos, yPos,
                btnWidth, BUTTON_HEIGHT,
                CommonComponents.GUI_DONE,
                this::apply
            )
        );

        xPos += btnWidth + SPACING;
        addRenderableWidget(
            new CustomButton(font,
                xPos, yPos,
                btnWidth, BUTTON_HEIGHT,
                CommonComponents.GUI_BACK,
                this::cancel
            )
        );
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(graphics, mouseX, mouseY, partialTick);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, PANEL, left, top, PANEL_WIDTH, PANEL_HEIGHT);
        graphics.drawCenteredString(font, getTitle(), width / 2, top + MARGIN, TITLE_COLOR);
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
        ClientConfig.updateSettings(displayChatMessages);
        onClose();
    }

    /* ====================================================================== */

    private void onDisplayChatMessages(CustomSwitch checkbox, boolean newValue) {
        displayChatMessages = newValue;
    }

}
