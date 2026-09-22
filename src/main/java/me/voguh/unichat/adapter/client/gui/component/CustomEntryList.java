/*!******************************************************************************
 * Copyright (c) 2026 Voguh
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

package me.voguh.unichat.adapter.client.gui.component;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.function.IntConsumer;

public final class CustomEntryList extends ObjectSelectionList<CustomEntryList.ListEntry> {

    private static final int TEXT_COLOR = 0xFFFFFFFF;
    private static final int HOVER_TEXT_COLOR = 0xFFFFFF55;
    private static final int HOVER_COLOR = 0x33FFFFFF;
    private static final int HOVER_PADDING = 2;
    private static final int ROW_HEIGHT = 20;
    private static final int ROW_TOP_INSET = 4;
    private static final int ICON_SIZE = 16;
    private static final int ICON_GAP = 4;
    private static final int GUTTER = SCROLLBAR_WIDTH + Button.DEFAULT_SPACING;

    private final Component emptyMessage;
    private final IntConsumer onSelect;
    private final List<RowAction> actions;

    /* ====================================================================== */

    public CustomEntryList(Minecraft minecraft, int width, int height, Component emptyMessage, IntConsumer onSelect, List<RowAction> actions) {
        super(minecraft, width, height, 0, ROW_HEIGHT);
        this.emptyMessage = emptyMessage;
        this.onSelect = onSelect;
        this.actions = List.copyOf(actions);
    }

    /* ====================================================================== */

    public void refresh(List<Component> labels) {
        clearEntries();
        for (int index = 0; index < labels.size(); index++) {
            addEntry(new ListEntry(elide(labels.get(index)), index));
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
        if (getItemCount() == 0) {
            Font font = minecraft.font;
            graphics.drawCenteredString(font, emptyMessage, getX() + width / 2, getY() + (height - font.lineHeight) / 2, TEXT_COLOR);

            return;
        }

        ListEntry hovered = getHovered();
        if (hovered == null) {
            return;
        }

        int hit = hovered.iconAt(mouseX);
        if (hit < 0) {
            return;
        }

        minecraft.screen.setTooltipForNextRenderPass(actions.get(hit).label());
    }

    /* ====================================================================== */

    private int iconsBlockWidth() {
        int count = actions.size();
        if (count == 0) {
            return 0;
        }

        return count * ICON_SIZE + (count - 1) * ICON_GAP;
    }

    private int reservedWidth() {
        return actions.isEmpty() ? 0 : iconsBlockWidth() + ICON_GAP * 2;
    }

    private int iconsLeft() {
        return getRowLeft() + getRowWidth() - iconsBlockWidth() - ICON_GAP;
    }

    private Component elide(Component label) {
        Font font = minecraft.font;
        int labelWidth = getRowWidth() - reservedWidth();
        if (font.width(label) <= labelWidth) {
            return label;
        }

        String truncated = font.plainSubstrByWidth(label.getString(), labelWidth - font.width(CommonComponents.ELLIPSIS));

        return Component.literal(truncated).append(CommonComponents.ELLIPSIS);
    }

    /* ====================================================================== */

    public static int heightFor(int rows) {
        return ROW_HEIGHT * rows + ROW_TOP_INSET;
    }

    /* ====================================================================== */

    public record RowAction(Component label, ResourceLocation icon, IntConsumer action) {

    }

    /* ====================================================================== */

    public final class ListEntry extends ObjectSelectionList.Entry<ListEntry> {

        private final Component label;
        private final int index;

        private ListEntry(Component label, int index) {
            this.label = label;
            this.index = index;
        }

        @Override
        public void render(GuiGraphics graphics, int rowIndex, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
            Font font = minecraft.font;
            int cellTop = top - HOVER_PADDING;
            int cellBottom = top + height + HOVER_PADDING;

            if (hovering) {
                int rowLeft = left - HOVER_PADDING;
                graphics.fill(rowLeft, cellTop, rowLeft + width, cellBottom, HOVER_COLOR);
            }

            int textY = (cellTop + cellBottom - font.lineHeight) / 2 + 1;
            graphics.drawString(font, label, left, textY, hovering ? HOVER_TEXT_COLOR : TEXT_COLOR);

            renderIcons(graphics, top, height, mouseX, hovering);
        }

        private void renderIcons(GuiGraphics graphics, int top, int height, int mouseX, boolean hovering) {
            int iconY = top + (height - ICON_SIZE) / 2;
            int iconX = iconsLeft();

            for (RowAction action : actions) {
                if (hovering && mouseX >= iconX && mouseX < iconX + ICON_SIZE) {
                    graphics.fill(iconX, iconY, iconX + ICON_SIZE, iconY + ICON_SIZE, HOVER_COLOR);
                }

                graphics.blitSprite(action.icon(), iconX, iconY, ICON_SIZE, ICON_SIZE);
                iconX += ICON_SIZE + ICON_GAP;
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            int hit = iconAt(mouseX);
            if (hit < 0) {
                onSelect.accept(index);
            } else {
                actions.get(hit).action().accept(index);
            }

            return true;
        }

        private int iconAt(double mouseX) {
            int iconX = iconsLeft();
            for (int position = 0; position < actions.size(); position++) {
                if (mouseX >= iconX && mouseX < iconX + ICON_SIZE) {
                    return position;
                }

                iconX += ICON_SIZE + ICON_GAP;
            }

            return -1;
        }

        @Override
        public Component getNarration() {
            return label;
        }

    }

}
