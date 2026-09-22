/*!******************************************************************************
 * Copyright (c) 2026 Voguh
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

package me.voguh.unichat.adapter.client.gui.screen;

import me.voguh.unichat.adapter.util.IdentifierUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public abstract class UniChatPanelScreen extends Screen {

    protected static final int CONTENT_WIDTH = Button.BIG_WIDTH;
    protected static final int SPACING = Button.DEFAULT_SPACING;
    protected static final int HALF_WIDTH = (CONTENT_WIDTH - SPACING) / 2;

    private static final ResourceLocation PANEL = IdentifierUtils.getIdentifier("panel/background");
    private static final int MARGIN = 16;

    private final LinearLayout layout = LinearLayout.vertical().spacing(SPACING);
    private final Screen parent;

    /* ====================================================================== */

    protected UniChatPanelScreen(Component title, Screen parent) {
        super(title);
        this.parent = parent;
    }

    protected abstract void addContents(LinearLayout layout);

    /* ====================================================================== */

    @Override
    protected final void init() {
        layout.addChild(new StringWidget(
            CONTENT_WIDTH,
            font.lineHeight,
            getTitle(),
            font
        ));
        addContents(layout);

        layout.visitWidgets(this::addRenderableWidget);
        repositionElements();
    }

    @Override
    protected void repositionElements() {
        layout.arrangeElements();
        FrameLayout.centerInRectangle(layout, 0, 0, width, height);
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(graphics, mouseX, mouseY, partialTick);

        int xPos = layout.getX() - MARGIN;
        int yPos = layout.getY() - MARGIN;
        int panelWidth = layout.getWidth() + MARGIN * 2;
        int panelHeight = layout.getHeight() + MARGIN * 2;

        graphics.blitSprite(PANEL, xPos, yPos, panelWidth, panelHeight);
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parent);
    }

}
