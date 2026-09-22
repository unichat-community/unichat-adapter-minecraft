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

import me.voguh.unichat.adapter.dto.RawWorker;
import me.voguh.unichat.adapter.util.IdentifierUtils;
import me.voguh.unichat.adapter.util.Strings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.List;

public final class UniChatWorkerList extends ObjectSelectionList<UniChatWorkerList.WorkerEntry> {

    private static final Component EMPTY_MESSAGE = IdentifierUtils.translatable("screen_workers_settings.empty");

    private static final int TEXT_COLOR = 0xFFFFFFFF;
    private static final int HOVER_TEXT_COLOR = 0xFFFFFF55;
    private static final int HOVER_COLOR = 0x33FFFFFF;
    private static final int HOVER_PADDING = 2;
    private static final int ROW_HEIGHT = 16;
    private static final int ROW_TOP_INSET = 4;
    private static final int GUTTER = SCROLLBAR_WIDTH + Button.DEFAULT_SPACING;

    /* ====================================================================== */

    public static int heightFor(int rows) {
        return ROW_HEIGHT * rows + ROW_TOP_INSET;
    }

    /* ====================================================================== */

    public UniChatWorkerList(Minecraft minecraft, int width, int height, List<RawWorker> workers) {
        super(minecraft, width, height, 0, ROW_HEIGHT);
        refresh(workers);
    }

    /* ====================================================================== */

    public void refresh(List<RawWorker> workers) {
        clearEntries();
        for (RawWorker worker : workers) {
            addEntry(new WorkerEntry(minecraft.font, elide(label(worker))));
        }

        clampScrollAmount();
    }

    /* ====================================================================== */

    @Override
    public int getRowWidth() {
        return width - GUTTER * 2;
    }

    @Override
    protected int getScrollbarPosition() {
        return getRight() - SCROLLBAR_WIDTH;
    }

    @Override
    protected void renderListBackground(GuiGraphics graphics) {
    }

    @Override
    protected void renderListSeparators(GuiGraphics graphics) {
    }

    @Override
    protected void renderDecorations(GuiGraphics graphics, int mouseX, int mouseY) {
        if (getItemCount() > 0) {
            return;
        }

        Font font = minecraft.font;
        graphics.drawCenteredString(font, EMPTY_MESSAGE, getX() + width / 2, getY() + (height - font.lineHeight) / 2, TEXT_COLOR);
    }

    /* ====================================================================== */

    private static Component label(RawWorker worker) {
        String name = worker.name();
        if (Strings.isNullOrEmpty(name)) {
            return IdentifierUtils.translatable("screen_workers_settings.unnamed");
        }

        return Component.literal(name);
    }

    private Component elide(Component label) {
        Font font = minecraft.font;
        int rowWidth = getRowWidth();
        if (font.width(label) <= rowWidth) {
            return label;
        }

        String truncated = font.plainSubstrByWidth(label.getString(), rowWidth - font.width(CommonComponents.ELLIPSIS));

        return Component.literal(truncated).append(CommonComponents.ELLIPSIS);
    }

    /* ====================================================================== */

    public static final class WorkerEntry extends ObjectSelectionList.Entry<WorkerEntry> {

        private final Font font;
        private final Component label;

        private WorkerEntry(Font font, Component label) {
            this.font = font;
            this.label = label;
        }

        @Override
        public void render(GuiGraphics graphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
            int cellTop = top - HOVER_PADDING;
            int cellBottom = top + height + HOVER_PADDING;

            if (hovering) {
                int rowLeft = left - HOVER_PADDING;
                graphics.fill(rowLeft, cellTop, rowLeft + width, cellBottom, HOVER_COLOR);
            }

            int textY = (cellTop + cellBottom - font.lineHeight) / 2 + 1;
            graphics.drawString(font, label, left, textY, hovering ? HOVER_TEXT_COLOR : TEXT_COLOR);
        }

        @Override
        public Component getNarration() {
            return label;
        }

    }

}
