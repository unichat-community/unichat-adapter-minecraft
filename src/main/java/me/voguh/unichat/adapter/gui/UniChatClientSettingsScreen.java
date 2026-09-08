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

import me.voguh.unichat.adapter.client.ClientConfig;
import me.voguh.unichat.adapter.gui.chat.ChatMessages;
import me.voguh.unichat.adapter.gui.component.CustomButton;
import me.voguh.unichat.adapter.gui.component.CustomOptionGroup;
import me.voguh.unichat.adapter.gui.component.CustomSwitch;
import me.voguh.unichat.adapter.gui.component.State;
import me.voguh.unichat.adapter.util.IdentifierUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.Identifier;

import java.util.List;

public final class UniChatClientSettingsScreen extends Screen {

    private static final Identifier PANEL = IdentifierUtils.getIdentifier("panel/background");

    private static final State<Integer> SUPERSAMPLE_1 = State.literal("1x", 1);
    private static final State<Integer> SUPERSAMPLE_2 = State.literal("2x", 2);
    private static final State<Integer> SUPERSAMPLE_3 = State.literal("3x", 3);
    private static final State<Integer> SUPERSAMPLE_4 = State.literal("4x", 4);
    private static final List<State<Integer>> SUPERSAMPLE_OPTIONS = List.of(SUPERSAMPLE_1, SUPERSAMPLE_2, SUPERSAMPLE_3, SUPERSAMPLE_4);

    private static final int SPACING = 8;
    private static final int MARGIN = 16;

    private static final int PANEL_WIDTH = 225;
    private static final int PANEL_HEIGHT = 200;

    private static final int INNER_WIDTH = PANEL_WIDTH - MARGIN * 2;

    private static final int TITLE_COLOR = 0xFFFFFFFF;

    /* ====================================================================== */

    private Boolean displayChatMessages;
    private Integer supersample;
    private int left;
    private int top;

    private final Screen parent;

    /* ====================================================================== */

    public UniChatClientSettingsScreen(Screen parent) {
        super(IdentifierUtils.translatable("screen_client_settings"));
        this.displayChatMessages = ClientConfig.renderMessages();
        this.supersample = ClientConfig.supersample();

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

        addRenderableWidget(
            new CustomSwitch(font,
                xPos, yPos,
                INNER_WIDTH,
                IdentifierUtils.translatable("screen_client_settings.display_chat_messages"),
                this::onDisplayChatMessages,
                displayChatMessages
            )
        );

        /* ================================================================== */

        yPos += CustomSwitch.HEIGHT + SPACING;

        addRenderableOnly(
            new StringWidget(
                xPos, yPos,
                INNER_WIDTH, font.lineHeight,
                IdentifierUtils.translatable("screen_client_settings.supersample"),
                font
            )
        );

        /* ================================================================== */

        yPos += font.lineHeight;

        addRenderableWidget(
            new CustomOptionGroup<>(font,
                xPos, yPos,
                INNER_WIDTH,
                IdentifierUtils.translatable("screen_client_settings.supersample"),
                this::onSupersampleChange,
                SUPERSAMPLE_OPTIONS.stream().filter(state -> state.value().equals(supersample)).findFirst().orElse(SUPERSAMPLE_1),
                SUPERSAMPLE_OPTIONS
            )
        );

        /* ================================================================== */

        yPos += CustomOptionGroup.HEIGHT + SPACING;

        addRenderableWidget(
            new CustomButton(font,
                xPos, yPos,
                INNER_WIDTH,
                CustomButton.Variant.DANGER,
                IdentifierUtils.translatable("screen_client_settings.clear_cache"),
                this::onClearCacheClick
            )
        );

        /* ================================================================== */

        yPos = top + PANEL_HEIGHT - CustomSwitch.HEIGHT - MARGIN;
        int btnWidth = (INNER_WIDTH / 2) - (SPACING / 2);

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

    private void onClearCacheClick(CustomButton button) {
        minecraft.setScreen(new UniChatDeleteCacheScreen(this));
    }

    private void cancel(CustomButton button) {
        onClose();
    }

    private void apply(CustomButton button) {
        boolean rescaled = ClientConfig.supersample() != supersample;

        ClientConfig.updateSettings(displayChatMessages, supersample);
        if (rescaled) {
            ChatMessages.INSTANCE.reloadImages();
        }

        onClose();
    }

    /* ====================================================================== */

    private void onDisplayChatMessages(CustomSwitch checkbox, boolean newValue) {
        displayChatMessages = newValue;
    }

    private void onSupersampleChange(CustomOptionGroup<Integer> group, Integer newValue) {
        supersample = newValue;
    }

}
