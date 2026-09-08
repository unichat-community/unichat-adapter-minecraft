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
import me.voguh.unichat.adapter.util.IdentifierUtils;
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

    private static final Identifier PANEL = IdentifierUtils.getIdentifier("panel/background");

    private static final int SPACING = 8;
    private static final int MARGIN = 16;

    private static final int PANEL_WIDTH = 225;
    private static final int PANEL_HEIGHT = 200;

    private static final int INNER_WIDTH = PANEL_WIDTH - MARGIN * 2;

    private static final int TITLE_COLOR = 0xFFFFFFFF;

    private int left;
    private int top;

    /* ====================================================================== */

    public UniChatSettingsMenuScreen() {
        super(Component.translatable("gui." + UniChatAdapter.MODID + ".menu_title"));
    }

    private boolean isOperator() {
        LocalPlayer player = Minecraft.getInstance().player;
        return player != null && player.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER);
    }

    /* ====================================================================== */

    @Override
    protected void init() {
        boolean isOperator = isOperator();

        left = (width - PANEL_WIDTH) / 2;
        top = (height - PANEL_HEIGHT) / 2;

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

            yPos += CustomButton.HEIGHT + SPACING;

            addRenderableWidget(
                new CustomButton(font,
                    xPos, yPos,
                    INNER_WIDTH,
                    Component.translatable("gui." + UniChatAdapter.MODID + ".btn_workers"),
                    this::onWorkersTabClick
                )
            );
        }

        /* ================================================================== */

        yPos = top + PANEL_HEIGHT - (CustomButton.HEIGHT + MARGIN);

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

    private void onClientTabClick(CustomButton button) {
        minecraft.setScreen(new UniChatClientSettingsScreen(this));

    }

    private void onServerTabClick(CustomButton button) {
        minecraft.setScreen(new UniChatServerSettingsScreen(this));
    }

    private void onWorkersTabClick(CustomButton button) {
        minecraft.setScreen(new UniChatWorkersSettingsScreen(this));
    }

}
