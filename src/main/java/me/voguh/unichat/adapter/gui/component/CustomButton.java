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

    private static final Identifier SUCCESS = Identifier.fromNamespaceAndPath(UniChatAdapter.MODID, "button/success");
    private static final Identifier SUCCESS_PRESSED = Identifier.fromNamespaceAndPath(UniChatAdapter.MODID, "button/success_pressed");
    private static final Identifier NORMAL = Identifier.fromNamespaceAndPath(UniChatAdapter.MODID, "button/normal");
    private static final Identifier NORMAL_PRESSED = Identifier.fromNamespaceAndPath(UniChatAdapter.MODID, "button/normal_pressed");
    private static final Identifier DANGER = Identifier.fromNamespaceAndPath(UniChatAdapter.MODID, "button/danger");
    private static final Identifier DANGER_PRESSED = Identifier.fromNamespaceAndPath(UniChatAdapter.MODID, "button/danger_pressed");
    public static final int HEIGHT = 20;

    private final Font font;
    private final Variant variant;
    private final Consumer<CustomButton> onPress;

    /* ====================================================================== */

    public enum Variant {
        NORMAL,
        SUCCESS,
        DANGER;
    }

    /* ====================================================================== */

    public CustomButton(Font font, int x, int y, int width, Component msg, Consumer<CustomButton> onPress) {
        super(x, y, width, HEIGHT, msg);
        this.font = font;
        this.variant = Variant.NORMAL;
        this.onPress = onPress;
    }

    public CustomButton(Font font, int x, int y, int width, Variant variant, Component msg, Consumer<CustomButton> onPress) {
        super(x, y, width, HEIGHT, msg);
        this.font = font;
        this.variant = variant;
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
        int glyph = font.lineHeight - 1;

        Identifier texture = getTextureForVariant();
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, texture, x, y, width, height);

        int color = getFontColor();
        int xText = x + (width - font.width(getMessage())) / 2;
        int yText = y + (height - glyph) / 2;
        graphics.drawString(font, getMessage(), xText, isHoveredOrFocused() ? yText + 2 : yText, color, false);
    }

    private Identifier getTextureForVariant() {
        boolean pressed = isHoveredOrFocused();

        return switch (variant) {
            case NORMAL -> pressed ? NORMAL_PRESSED : NORMAL;
            case SUCCESS -> pressed ? SUCCESS_PRESSED : SUCCESS;
            case DANGER -> pressed ? DANGER_PRESSED : DANGER;
        };
    }

    private int getFontColor() {
        return switch (variant) {
            case NORMAL -> 0xFF1C1C1D;
            case SUCCESS -> 0xFFFFFFFF;
            case DANGER -> 0xFFFFFFFF;
        };
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput out) {
        out.add(NarratedElementType.TITLE, createNarrationMessage());
    }

}
