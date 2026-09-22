package me.voguh.unichat.adapter.client.gui.component;

import me.voguh.unichat.adapter.util.IdentifierUtils;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.List;
import java.util.function.BiConsumer;

public final class CustomLabeledCycleButton<T> extends LinearLayout {

    public static final int HEIGHT = 20;
    private static final WidgetSprites NORMAL = new WidgetSprites(IdentifierUtils.getIdentifier("button/normal"), IdentifierUtils.getIdentifier("button/disabled"), IdentifierUtils.getIdentifier("button/normal_pressed"));
    private static final int PADDING = 8;

    private int index;
    private List<State<T>> states;

    private final Font font;
    private final BiConsumer<CustomLabeledCycleButton<T>, State<T>> onChange;
    private final ValueButton valueButton;

    /* ====================================================================== */

    public CustomLabeledCycleButton(Font font, int width, Component message, State<T> initialState, BiConsumer<CustomLabeledCycleButton<T>, State<T>> onChange, List<State<T>> states) {
        this(font, 0, 0, width, message, initialState, onChange, states);
    }

    public CustomLabeledCycleButton(Font font, int x, int y, int width, Component message, State<T> initialState, BiConsumer<CustomLabeledCycleButton<T>, State<T>> onChange, List<State<T>> states) {
        super(0, 0, Orientation.VERTICAL);
        spacing(0);
        this.font = font;
        this.onChange = onChange;
        this.states = List.copyOf(states);
        this.index = Math.max(this.states.indexOf(initialState), 0);

        addChild(new StringWidget(width, font.lineHeight, message, font).alignLeft());

        valueButton = new ValueButton(width);
        addChild(valueButton);
    }

    /* ====================================================================== */

    public void setStates(State<T> initialState, List<State<T>> states) {
        this.states = List.copyOf(states);
        this.index = Math.max(this.states.indexOf(initialState), 0);
        valueButton.setMessage(this.states.get(index).message());
    }

    /* ====================================================================== */

    private void cycle(int delta) {
        index = Mth.positiveModulo(index + delta, states.size());
        State<T> state = states.get(index);
        valueButton.setMessage(state.message());
        onChange.accept(this, state);
    }

    /* ====================================================================== */

    private final class ValueButton extends AbstractButton {

        private ValueButton(int width) {
            super(0, 0, width, HEIGHT, states.get(index).message());
        }

        @Override
        public void onPress() {
            cycle(Screen.hasShiftDown() ? -1 : 1);
        }

        @Override
        protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            int x = getX();
            int y = getY();
            int width = getWidth();
            int height = getHeight();
            int glyph = font.lineHeight - 1;

            ResourceLocation sprite = NORMAL.get(isActive(), isHovered());
            graphics.blitSprite(sprite, x, y, width, height);

            boolean hovered = isActive() && isHovered();
            FormattedText formattedText = font.ellipsize(getMessage(), width - PADDING * 2);
            int xText = x + (width - font.width(formattedText)) / 2;
            int yText = y + (height - glyph) / 2 + (hovered ? 2 : 0);

            graphics.drawString(font, Language.getInstance().getVisualOrder(formattedText), xText, yText, 0xFF1C1C1D, false);
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput out) {
            out.add(NarratedElementType.TITLE, createNarrationMessage());
        }

    }

}
