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
import me.voguh.unichat.adapter.gui.component.CustomButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.permissions.Permissions;

public final class UniChatSettingsMenuScreen extends Screen {

    private static final Identifier PANEL = Identifier.fromNamespaceAndPath(UniChatAdapter.MODID, "panel/background");

    private static final int SPACING = 8;
    private static final int MARGIN = 16;

    private static final int PANEL_WIDTH = 175;
    private static final int PANEL_OPERATOR_HEIGHT = (CustomButton.HEIGHT + SPACING) * 2 + (MARGIN * 2) + (CustomButton.HEIGHT + SPACING) * 2;
    private static final int PANEL_CLIENT_HEIGHT = (CustomButton.HEIGHT + SPACING) + (MARGIN * 2) + (CustomButton.HEIGHT + SPACING) * 2;

    private static final int INNER_WIDTH = PANEL_WIDTH - MARGIN * 2;

    private static final int TITLE_COLOR = 0xFFFFFFFF;

    private int panelWidth;
    private int panelHeight;
    private int left;
    private int top;

    /* ====================================================================== */

    public UniChatSettingsMenuScreen() {
        super(Component.translatable("gui." + UniChatAdapter.MODID + ".menu_title"));
        this.panelWidth = PANEL_WIDTH;
        this.panelHeight = PANEL_OPERATOR_HEIGHT;
    }

    private boolean isOperator() {
        LocalPlayer player = Minecraft.getInstance().player;
        return player != null && player.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER);
    }

    /* ====================================================================== */

    @Override
    protected void init() {
        boolean isOperator = isOperator();

        panelWidth = PANEL_WIDTH;
        panelHeight = isOperator ? PANEL_OPERATOR_HEIGHT : PANEL_CLIENT_HEIGHT;
        left = (width - panelWidth) / 2;
        top = (height - panelHeight) / 2;

        int glyph = font.lineHeight - 1;
        int titleSpacingY = glyph + SPACING;
        int xPos = left + MARGIN;
        int yPos = top + MARGIN + titleSpacingY;

        /* ================================================================== */

        addRenderableWidget(
            new CustomButton(font,
                xPos, yPos,
                INNER_WIDTH,
                Component.translatable("gui." + UniChatAdapter.MODID + ".btn_client"),
                this::onClientTabClick
            )
        );

        /* ================================================================== */

        if (isOperator) {
            yPos += CustomButton.HEIGHT + SPACING;

            addRenderableWidget(
                new CustomButton(font,
                    xPos, yPos,
                    INNER_WIDTH,
                    Component.translatable("gui." + UniChatAdapter.MODID + ".btn_server"),
                    this::onServerTabClick
                )
            );
        }

        /* ================================================================== */

        yPos = top + panelHeight - (CustomButton.HEIGHT + MARGIN);

        addRenderableWidget(
            new CustomButton(font,
                xPos, yPos,
                INNER_WIDTH,
                CommonComponents.GUI_BACK,
                (btn) -> onClose()
            )
        );
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(graphics, mouseX, mouseY, partialTick);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, PANEL, left, top, panelWidth, panelHeight);
        graphics.drawCenteredString(font, getTitle(), width / 2, top + MARGIN, TITLE_COLOR);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    private void onClientTabClick(CustomButton button) {
        minecraft.setScreen(new UniChatClientSettingsScreen(this));

    }

    private void onServerTabClick(CustomButton button) {
        minecraft.setScreen(new UniChatServerSettingsScreen(this));
    }

}
