package me.voguh.unichat.adapter.client.gui.component;

import com.mojang.math.Divisor;
import me.voguh.unichat.adapter.util.IdentifierUtils;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.layouts.EqualSpacingLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.function.BiConsumer;

public final class CustomOptionGroup<T> extends LinearLayout {

    public static final int HEIGHT = 20;
    private static final int PADDING = 8;

    private State<T> currentState;

    private final Font font;
    private final BiConsumer<CustomOptionGroup<T>, State<T>> onChange;

    /* ====================================================================== */

    public CustomOptionGroup(Font font, int width, Component message, State<T> initialState, BiConsumer<CustomOptionGroup<T>, State<T>> onChange, List<State<T>> states) {
        this(font, 0, 0, width, message, initialState, onChange, states);
    }

    public CustomOptionGroup(Font font, int x, int y, int width, Component message, State<T> initialState, BiConsumer<CustomOptionGroup<T>, State<T>> onChange, List<State<T>> states) {
        super(0, 0, Orientation.VERTICAL);
        spacing(4);
        this.font = font;
        this.onChange = onChange;
        this.currentState = initialState;

        addChild(new StringWidget(width, font.lineHeight, message, font).alignLeft());

        EqualSpacingLayout row = new EqualSpacingLayout(x, y, width, HEIGHT, EqualSpacingLayout.Orientation.HORIZONTAL);
        Divisor widths = new Divisor(width, states.size());
        for (State<T> state : states) {
            row.addChild(new Option(state, widths.nextInt()));
        }

        addChild(row);
    }

    /* ====================================================================== */

    private void select(State<T> state) {
        currentState = state;
        onChange.accept(this, state);
    }

    /* ====================================================================== */

    private final class Option extends AbstractButton {

        private static final WidgetSprites SPRITES = new WidgetSprites(IdentifierUtils.getIdentifier("button/normal"), IdentifierUtils.getIdentifier("button/disabled"), IdentifierUtils.getIdentifier("button/success_pressed"));

        private final State<T> state;

        private Option(State<T> state, int width) {
            super(0, 0, width, HEIGHT, state.message());
            this.state = state;
        }

        @Override
        public void onPress() {
            select(state);
        }

        @Override
        protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            int x = getX();
            int y = getY();
            int width = getWidth();
            int height = getHeight();
            int glyph = font.lineHeight - 1;

            boolean selected = state.equals(currentState);
            ResourceLocation sprite = SPRITES.get(isActive(), selected);
            graphics.blitSprite(sprite, x, y, width, height);

            FormattedText formattedText = font.ellipsize(state.message(), width - PADDING * 2);
            int color = selected ? 0xFFFFFFFF : 0xFF1C1C1D;
            int xText = x + (width - font.width(formattedText)) / 2;
            int yText = y + (height - glyph) / 2 + (selected ? 2 : 0);

            graphics.drawString(font, Language.getInstance().getVisualOrder(formattedText), xText, yText, color, false);
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput out) {
            out.add(NarratedElementType.TITLE, createNarrationMessage());
        }

    }

}
