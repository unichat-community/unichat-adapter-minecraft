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

import me.voguh.unichat.adapter.client.ServerStateHolder;
import me.voguh.unichat.adapter.gui.component.CustomButton;
import me.voguh.unichat.adapter.gui.component.CustomSwitch;
import me.voguh.unichat.adapter.util.IdentifierUtils;
import me.voguh.unichat.adapter.worker.loader.RawWorker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ScrollableLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public final class UniChatWorkersSettingsScreen extends Screen {

    private static final Logger LOGGER = LoggerFactory.getLogger(UniChatWorkersSettingsScreen.class);
    private static final Identifier PANEL = IdentifierUtils.getIdentifier("panel/background");

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
        super(IdentifierUtils.translatable("screen_workers_settings"));

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

        int viewportWidth = PANEL_WIDTH - (MARGIN * 2 + 20); // 20 is the scrollbar reserve
        LinearLayout content = LinearLayout.vertical().spacing(SPACING / 2);

        List<RawWorker> workers = ServerStateHolder.INSTANCE.workers();
        for (int i = 0; i < workers.size(); i++) {
            RawWorker worker = workers.get(i);
            content.addChild(
                new CustomButton(font,
                    0, 0,
                    viewportWidth,
                    Component.literal(Optional.ofNullable(worker.name()).orElse("Unnamed Worker")),
                    (btn) -> onWorkerClick(btn, worker)
                )
            );
        }

        ScrollableLayout scroll = new ScrollableLayout(minecraft, content, viewportWidth);
        scroll.setMinWidth(INNER_WIDTH);
        scroll.setMaxHeight(PANEL_HEIGHT - (MARGIN * 2 + titleSpacingY + CustomButton.HEIGHT + SPACING));
        scroll.setX(xPos);
        scroll.setY(yPos);
        scroll.arrangeElements();
        scroll.visitWidgets(this::addRenderableWidget);

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
//        minecraft.setScreen(new UniChatWorkerSettingsScreen(this, worker));
    }

}
