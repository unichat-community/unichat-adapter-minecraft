/*!******************************************************************************
 * Copyright (c) 2026 Voguh
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

package me.voguh.unichat.adapter.client.gui.screen.worker;

import me.voguh.unichat.adapter.client.gui.component.CustomButton;
import me.voguh.unichat.adapter.client.gui.screen.UniChatPanelScreen;
import me.voguh.unichat.adapter.util.IdentifierUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public final class UniChatWorkerValidationScreen extends UniChatPanelScreen {

    private static final ResourceLocation WARNING = IdentifierUtils.getIdentifier("panel/warning");

    private static final Component TITLE = IdentifierUtils.gui("screen_worker_validation");
    private static final Component WARNING_TITLE = IdentifierUtils.gui("screen_worker_validation.title");

    private static final int WARNING_PADDING = 10;
    private static final int WARNING_WIDTH = CONTENT_WIDTH - WARNING_PADDING * 2;

    private LinearLayout warning;

    private final Component message;

    /* ====================================================================== */

    public UniChatWorkerValidationScreen(Screen parent, Component message) {
        super(TITLE, parent);
        this.message = message;
    }

    /* ====================================================================== */

    @Override
    protected void addContents(LinearLayout layout) {
        warning = LinearLayout.vertical().spacing(SPACING);
        warning.addChild(new StringWidget(
            WARNING_WIDTH,
            font.lineHeight,
            WARNING_TITLE,
            font
        ));

        warning.addChild(new MultiLineTextWidget(
            message,
            font
        ).setMaxWidth(WARNING_WIDTH));

        layout.addChild(warning, (settings) -> settings.padding(WARNING_PADDING));

        /* ================================================================== */

        CustomButton back = new CustomButton(font, CONTENT_WIDTH, CommonComponents.GUI_BACK, this::cancel);
        layout.addChild(back, (settings) -> settings.paddingTop(SPACING));
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(graphics, mouseX, mouseY, partialTick);

        int xPos = warning.getX() - WARNING_PADDING;
        int yPos = warning.getY() - WARNING_PADDING;
        int warningWidth = warning.getWidth() + WARNING_PADDING * 2;
        int warningHeight = warning.getHeight() + WARNING_PADDING * 2;

        graphics.blitSprite(WARNING, xPos, yPos, warningWidth, warningHeight);
    }

    @Override
    public Component getNarrationMessage() {
        return CommonComponents.joinForNarration(super.getNarrationMessage(), message);
    }

    /* ====================================================================== */

    private void cancel(CustomButton button) {
        onClose();
    }

}
