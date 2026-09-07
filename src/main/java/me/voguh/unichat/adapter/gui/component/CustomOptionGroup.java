package me.voguh.unichat.adapter.gui.component;

import me.voguh.unichat.adapter.UniChatAdapter;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

public final class CustomOptionGroup<T> extends AbstractButton {

    private static final Identifier SELECTED = Identifier.fromNamespaceAndPath(UniChatAdapter.MODID, "button/success_pressed");
    private static final Identifier REGULAR = Identifier.fromNamespaceAndPath(UniChatAdapter.MODID, "button/normal");
    public static final int HEIGHT = 20;

    private State<T> selectedState;

    private final Font font;
    private final List<State<T>> states;
    private final BiConsumer<CustomOptionGroup<T>, T> onPress;

    /* ====================================================================== */

    public record State<T>(Component msg, T value) {

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            State<?> state = (State<?>) o;
            return Objects.equals(value, state.value);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(value);
        }

    }

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
        this.selectedState = initialState;
        this.states = states;
    }

    /* ====================================================================== */

    @Override
    public void onPress(InputWithModifiers mod) {
        if (mod instanceof MouseButtonEvent event) {
            double mouseX = event.x();
            double mouseY = event.y();

            int x = getX();
            int y = getY();
            int width = getWidth();
            int height = getHeight();

            if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
                int n = states.size();
                int span = width - 1;
                for (int i = 0; i < n; i++) {
                    int x0 = i * span / n;
                    int x1 = (i + 1) * span / n;

                    if (mouseX >= x + x0 && mouseX <= x + x1) {
                        State<T> state = states.get(i);
                        selectedState = state;
                        onPress.accept(this, state.value());
                        break;
                    }
                }
            }
        }
    }

    @Override
    protected void renderContents(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int x = getX();
        int y = getY();
        int width = getWidth();
        int height = getHeight();
        int glyph = font.lineHeight - 1;

        int n = states.size();
        int span = width - 1;
        for (int i = 0; i < n; i++) {
            int x0 = i * span / n;
            int x1 = (i + 1) * span / n;
            int w = x1 - x0 + 1;

            /* ============================================================== */

            State<T> state = states.get(i);
            boolean isSelected = state.equals(selectedState);

            /* ============================================================== */

            int stateX = x + x0;
            Identifier texture = isSelected ? SELECTED : REGULAR;

            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, texture, stateX, y, w, height);

            /* ============================================================== */

            int textColor = isSelected ? 0xFFFFFFFF : 0xFF1C1C1D;
            int xText = stateX + (w - font.width(state.msg())) / 2;
            int yText = y + (height - glyph) / 2;

            graphics.drawString(font, state.msg(), xText, isSelected ? yText + 2 : yText, textColor, false);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput out) {
        out.add(NarratedElementType.TITLE, createNarrationMessage());
        out.add(NarratedElementType.USAGE, selectedState.msg);
    }

}
