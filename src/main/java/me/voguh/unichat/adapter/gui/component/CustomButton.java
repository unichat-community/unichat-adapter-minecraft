package me.voguh.unichat.adapter.gui.component;

import me.voguh.unichat.adapter.util.IdentifierUtils;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public final class CustomButton extends AbstractButton {

    private static final WidgetSprites NORMAL = new WidgetSprites(IdentifierUtils.getIdentifier("button/normal"), IdentifierUtils.getIdentifier("button/disabled"), IdentifierUtils.getIdentifier("button/normal_pressed"));
    private static final WidgetSprites SUCCESS = new WidgetSprites(IdentifierUtils.getIdentifier("button/success"), IdentifierUtils.getIdentifier("button/disabled"), IdentifierUtils.getIdentifier("button/success_pressed"));
    private static final WidgetSprites DANGER = new WidgetSprites(IdentifierUtils.getIdentifier("button/danger"), IdentifierUtils.getIdentifier("button/disabled"), IdentifierUtils.getIdentifier("button/danger_pressed"));
    private static final int PADDING = 8;
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
    public void onPress(@NotNull InputWithModifiers mod) {
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

        int innerWidth = width - PADDING * 2;

        boolean hovered = isHoveredOrFocused();
        int color = getFontColor();
        FormattedText formattedText = font.ellipsize(getMessage(), innerWidth);
        int xText = x + (width - font.width(formattedText)) / 2;
        int yText = y + (height - glyph) / 2 + (hovered ? 2 : 0);

        graphics.drawString(font, Language.getInstance().getVisualOrder(formattedText), xText, yText, color, false);
    }

    private Identifier getTextureForVariant() {
        boolean active = isActive();
        boolean pressed = isHoveredOrFocused();

        return switch (variant) {
            case NORMAL -> NORMAL.get(active, pressed);
            case SUCCESS -> SUCCESS.get(active, pressed);
            case DANGER -> DANGER.get(active, pressed);
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
