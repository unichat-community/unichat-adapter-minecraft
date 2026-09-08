package me.voguh.unichat.adapter.gui.component;

import com.mojang.math.Divisor;
import me.voguh.unichat.adapter.util.IdentifierUtils;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.EqualSpacingLayout;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public final class CustomOptionGroup<T> extends AbstractContainerWidget {

    private static final int PADDING = 8;
    public static final int HEIGHT = 20;

    private State<T> currentState;

    private final Font font;
    private final List<Option> options;
    private final BiConsumer<CustomOptionGroup<T>, T> onPress;

    /* ====================================================================== */

    public CustomOptionGroup(
        Font font,
        int x, int y,
        int width,
        Component msg,
        BiConsumer<CustomOptionGroup<T>, T> onPress,
        State<T> initialState,
        List<State<T>> states
    ) {
        super(x, y, width, HEIGHT, msg);
        this.font = font;
        this.onPress = onPress;
        this.currentState = initialState;
        this.options = buildOptions(states);

        EqualSpacingLayout layout = new EqualSpacingLayout(x, y, width, HEIGHT, EqualSpacingLayout.Orientation.HORIZONTAL);
        options.forEach(layout::addChild);
        layout.arrangeElements();
    }

    private List<Option> buildOptions(List<State<T>> states) {
        Divisor widths = new Divisor(getWidth(), states.size());

        List<Option> options = new ArrayList<>();
        for (State<T> state : states) {
            options.add(new Option(state, widths.nextInt()));
        }

        return List.copyOf(options);
    }

    /* ====================================================================== */

    @Override
    public @NotNull List<? extends GuiEventListener> children() {
        return options;
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        for (Option option : options) {
            option.render(graphics, mouseX, mouseY, partialTick);
        }
    }

    @Override
    protected int contentHeight() {
        return getHeight();
    }

    @Override
    protected double scrollRate() {
        return 0.0;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput out) {
        out.add(NarratedElementType.TITLE, createNarrationMessage());
        out.add(NarratedElementType.USAGE, currentState.message());
    }

    private void select(State<T> state) {
        currentState = state;
        onPress.accept(this, state.value());
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
        public void onPress(@NotNull InputWithModifiers mod) {
            select(state);
        }

        @Override
        protected void renderContents(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            int x = getX();
            int y = getY();
            int width = getWidth();
            int height = getHeight();
            int innerWidth = width - PADDING * 2;
            int glyph = font.lineHeight - 1;

            /* ============================================================== */

            boolean selected = state.equals(currentState);
            Identifier texture = SPRITES.get(isActive(), selected);
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, texture, x, y, width, height);

            /* ============================================================== */

            FormattedText formattedText = font.ellipsize(state.message(), innerWidth);
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
