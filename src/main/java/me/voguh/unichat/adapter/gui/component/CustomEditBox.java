package me.voguh.unichat.adapter.gui.component;

import me.voguh.unichat.adapter.UniChatAdapter;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.function.BiConsumer;

public class CustomEditBox extends EditBox {

    private static final Identifier BOX = Identifier.fromNamespaceAndPath(UniChatAdapter.MODID, "editbox/background");
    public static final int HEIGHT = 20;
    private static final int PADDING_X = 6;
    private static final int PADDING_Y = 6;

    public CustomEditBox(Font font, int x, int y, int width, Component msg, BiConsumer<CustomEditBox, String> onPress, String initialState) {
        super(font, x + PADDING_X, y + PADDING_Y, width - PADDING_X * 2, HEIGHT - PADDING_Y * 2, msg);
        this.setValue(initialState);
        this.setResponder((text) -> onPress.accept(this, text));
        setBordered(false);
        setTextColor(0xFFF0F0F1);
    }

    /* ====================================================================== */

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (!visible) {
            return;
        }

        int x = getX() - PADDING_X;
        int y = getY() - PADDING_Y;
        int width = getWidth() + PADDING_X * 2;
        int height = getHeight() + PADDING_Y * 2;
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BOX, x, y, width, height);

        super.renderWidget(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (!isMouseOver(event.x(), event.y())) {
            return false;
        }

        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        int x = getX();
        int y = getY();

        return active && visible
            && mouseX >= x - PADDING_X && mouseX < x + getWidth() + PADDING_X
            && mouseY >= y - PADDING_Y && mouseY < y + getHeight() + PADDING_Y;
    }

}
