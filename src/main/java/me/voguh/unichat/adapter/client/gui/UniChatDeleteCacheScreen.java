/*!******************************************************************************
 * Copyright (c) 2026 Voguh
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

package me.voguh.unichat.adapter.client.gui;

import me.voguh.unichat.adapter.client.store.ImageStore;
import me.voguh.unichat.adapter.util.IdentifierUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public final class UniChatDeleteCacheScreen extends UniChatPanelScreen {

    private static final ResourceLocation WARNING = IdentifierUtils.getIdentifier("panel/warning");

    private static final Component WARNING_TITLE = IdentifierUtils.translatable("screen_client_settings.clear_cache_warning_title");
    private static final Component WARNING_MESSAGE = IdentifierUtils.translatable("screen_client_settings.clear_cache_warning_message");

    private static final int WARNING_PADDING = 10;
    private static final int WARNING_WIDTH = CONTENT_WIDTH - WARNING_PADDING * 2;

    private LinearLayout warning;

    /* ====================================================================== */

    public UniChatDeleteCacheScreen(Screen parent) {
        super(IdentifierUtils.translatable("screen_client_settings.clear_cache"), parent);
    }

    /* ====================================================================== */

    @Override
    protected void addContents(LinearLayout layout) {
        warning = LinearLayout.vertical().spacing(SPACING);
        warning.addChild(new StringWidget(WARNING_WIDTH, font.lineHeight, WARNING_TITLE, font));
        warning.addChild(new MultiLineTextWidget(WARNING_MESSAGE, font).setMaxWidth(WARNING_WIDTH));

        layout.addChild(warning, (settings) -> settings.padding(WARNING_PADDING));

        /* ================================================================== */

        LinearLayout actions = LinearLayout.horizontal().spacing(SPACING);
        actions.addChild(Button.builder(IdentifierUtils.translatable("clear"), this::apply).width(HALF_WIDTH).build());
        actions.addChild(Button.builder(CommonComponents.GUI_BACK, this::cancel).width(HALF_WIDTH).build());

        layout.addChild(actions, (settings) -> settings.paddingTop(SPACING));
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
        return CommonComponents.joinForNarration(super.getNarrationMessage(), WARNING_MESSAGE);
    }

    /* ====================================================================== */

    private void cancel(Button button) {
        onClose();
    }

    private void apply(Button button) {
        ImageStore.INSTANCE.clear();
        onClose();
    }

}
