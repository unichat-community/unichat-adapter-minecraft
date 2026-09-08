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
import me.voguh.unichat.adapter.gui.component.CustomSwitch;
import me.voguh.unichat.adapter.store.ImageStore;
import me.voguh.unichat.adapter.util.IdentifierUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;

public final class UniChatDeleteCacheScreen extends Screen {

    private static final Identifier PANEL = IdentifierUtils.getIdentifier("panel/background");
    private static final Identifier WARNING = IdentifierUtils.getIdentifier("panel/warning");

    private static final int SPACING = 8;
    private static final int MARGIN = 16;

    private static final int PANEL_WIDTH = 225;
    private static final int PANEL_HEIGHT = 200;

    private static final int INNER_WIDTH = PANEL_WIDTH - MARGIN * 2;

    private static final int TITLE_COLOR = 0xFFFFFFFF;

    /* ====================================================================== */

    private int left;
    private int top;

    private final Screen parent;

    /* ====================================================================== */

    public UniChatDeleteCacheScreen(Screen parent) {
        super(Component.translatable("gui." + UniChatAdapter.MODID + ".screen_client_settings.clear_cache"));
        this.parent = parent;
    }

    /* ====================================================================== */

    @Override
    protected void init() {
        super.init();
        left = (width - PANEL_WIDTH) / 2;
        top = (height - PANEL_HEIGHT) / 2;

        int xPos = left + MARGIN;
        int yPos = top + PANEL_HEIGHT - CustomSwitch.HEIGHT - MARGIN;
        int btnWidth = (INNER_WIDTH / 2) - (SPACING / 2);

        addRenderableWidget(
            new CustomButton(font,
                xPos, yPos,
                btnWidth,
                CustomButton.Variant.DANGER,
                Component.translatable("gui." + UniChatAdapter.MODID + ".clear"),
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

        int glyph = font.lineHeight - 1;
        int xPos = left;
        int yPos = top;

        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, PANEL, xPos, yPos, PANEL_WIDTH, PANEL_HEIGHT);

        /* ================================================================== */

        int titleX = left + MARGIN + (INNER_WIDTH / 2);
        int titleY = top + MARGIN;
        graphics.drawCenteredString(font, getTitle(), titleX, titleY, TITLE_COLOR);

        /* ====================================================================================== */

        yPos += glyph + SPACING;

        int warningBoxWidth = INNER_WIDTH;
        int warningBoxInnerWidth = warningBoxWidth - (2 + SPACING * 2 + 2); // 2px border + SPACING left and right + 2px border

        MutableComponent warningTitle = Component.translatable("gui." + UniChatAdapter.MODID + ".screen_client_settings.clear_cache_warning_title");
        MutableComponent warningMessage = Component.translatable("gui." + UniChatAdapter.MODID + ".screen_client_settings.clear_cache_warning_message");
        int height = 2 + SPACING + glyph + SPACING + font.wordWrapHeight(warningMessage, warningBoxInnerWidth) + SPACING + 2;

        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, WARNING, xPos, yPos, warningBoxWidth, height);

        /* ================================================================== */

        xPos += 2 + SPACING;
        yPos += 2 + SPACING;

        int warningBoxTitleX = xPos + (warningBoxInnerWidth / 2);
        int warningBoxTitleY = yPos;
        graphics.drawCenteredString(font, warningTitle, warningBoxTitleX, warningBoxTitleY, TITLE_COLOR);

        /* ================================================================== */

        yPos += glyph + SPACING;

        graphics.drawWordWrap(font, warningMessage, xPos, yPos, warningBoxInnerWidth, TITLE_COLOR);
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
        ImageStore.INSTANCE.clear();
        onClose();
    }

}
