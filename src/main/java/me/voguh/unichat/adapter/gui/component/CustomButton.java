package me.voguh.unichat.adapter.gui.component;

import me.voguh.unichat.adapter.UniChatAdapter;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.function.Consumer;

public final class CustomButton extends AbstractButton {

    private static final Identifier PRESSED = Identifier.fromNamespaceAndPath(UniChatAdapter.MODID, "button/pressed");
    private static final Identifier UNPRESSED = Identifier.fromNamespaceAndPath(UniChatAdapter.MODID, "button/normal");

    private final Font font;
    private final Consumer<CustomButton> onPress;

    /* ====================================================================== */

    public CustomButton(Font font, int x, int y, int width, int height, Component msg, Consumer<CustomButton> onPress) {
        super(x, y, width, height, msg);
        this.font = font;
        this.onPress = onPress;
    }

    /* ====================================================================== */

    @Override
    public void onPress(InputWithModifiers inputWithModifiers) {
        onPress.accept(this);
    }

    @Override
    protected void renderContents(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int x = getX();
        int y = getY();
        int width = getWidth();
        int height = getHeight();

        Identifier texture = isHoveredOrFocused() ? PRESSED : UNPRESSED;
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, texture, x, y, width, height);

        int color = isHoveredOrFocused() ? 0xFFFFFFFF : 0xFF1C1C1D;
        int xText = x + (width - font.width(getMessage())) / 2;
        int yText = y + (height - font.lineHeight) / 2;
        graphics.drawString(font, getMessage(), xText, isHoveredOrFocused() ? yText + 2 : yText, color, false);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput out) {
        out.add(NarratedElementType.TITLE, createNarrationMessage());
    }

}
