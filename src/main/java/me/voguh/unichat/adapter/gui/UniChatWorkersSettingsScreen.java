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
import me.voguh.unichat.adapter.gui.component.CustomSwitch;
import me.voguh.unichat.adapter.worker.loader.RawWorker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Optional;

public final class UniChatWorkersSettingsScreen extends Screen {

    private static final Identifier PANEL = Identifier.fromNamespaceAndPath(UniChatAdapter.MODID, "panel/background");

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

    public UniChatWorkersSettingsScreen(Screen parent) {
        super(Component.translatable("gui." + UniChatAdapter.MODID + ".screen_workers_settings"));

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

        List<RawWorker> workers = ServerStateHolder.INSTANCE.workers();
        for (int i = 0; i < workers.size(); i++) {
            RawWorker worker = workers.get(i);
            addRenderableWidget(
                new CustomButton(font,
                    xPos, yPos,
                    INNER_WIDTH,
                    Component.literal(Optional.ofNullable(worker.name()).orElse("Unnamed Worker")),
                    (btn) -> onWorkerClick(btn, worker)
                )
            );
            yPos += CustomButton.HEIGHT + SPACING;
        }

        /* ================================================================== */

        yPos = top + PANEL_HEIGHT - (CustomSwitch.HEIGHT + MARGIN);

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

    private void onWorkerClick(CustomButton button, RawWorker worker) {
    }

}
