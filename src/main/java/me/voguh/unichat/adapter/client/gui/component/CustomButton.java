package me.voguh.unichat.adapter.client.gui.component;

import me.voguh.unichat.adapter.util.IdentifierUtils;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public final class CustomButton extends AbstractButton {

    public static final int HEIGHT = 20;
    private static final WidgetSprites NORMAL = new WidgetSprites(IdentifierUtils.getIdentifier("button/normal"), IdentifierUtils.getIdentifier("button/disabled"), IdentifierUtils.getIdentifier("button/normal_pressed"));
    private static final WidgetSprites SUCCESS = new WidgetSprites(IdentifierUtils.getIdentifier("button/success"), IdentifierUtils.getIdentifier("button/disabled"), IdentifierUtils.getIdentifier("button/success_pressed"));
    private static final WidgetSprites DANGER = new WidgetSprites(IdentifierUtils.getIdentifier("button/danger"), IdentifierUtils.getIdentifier("button/disabled"), IdentifierUtils.getIdentifier("button/danger_pressed"));
    private static final int PADDING = 8;

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

    public CustomButton(Font font, int width, Component message, Consumer<CustomButton> onPress) {
        this(font, 0, 0, width, message, onPress);
    }

    public CustomButton(Font font, int x, int y, int width, Component message, Consumer<CustomButton> onPress) {
        this(font, x, y, width, message, Variant.NORMAL, onPress);
    }

    public CustomButton(Font font, int width, Component message, Variant variant, Consumer<CustomButton> onPress) {
        this(font, 0, 0, width, message, variant, onPress);
    }

    public CustomButton(Font font, int x, int y, int width, Component message, Variant variant, Consumer<CustomButton> onPress) {
        super(x, y, width, HEIGHT, message);
        this.font = font;
        this.variant = variant;
        this.onPress = onPress;
    }

    /* ====================================================================== */

    @Override
    public void onPress() {
        onPress.accept(this);
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int x = getX();
        int y = getY();
        int width = getWidth();
        int height = getHeight();
        int glyph = font.lineHeight - 1;

        ResourceLocation sprite = spriteFor(variant).get(isActive(), isHovered());
        graphics.blitSprite(sprite, x, y, width, height);

        boolean hovered = isActive() && isHovered();
        FormattedText formattedText = font.ellipsize(getMessage(), width - PADDING * 2);
        int xText = x + (width - font.width(formattedText)) / 2;
        int yText = y + (height - glyph) / 2 + (hovered ? 2 : 0);

        graphics.drawString(font, Language.getInstance().getVisualOrder(formattedText), xText, yText, colorFor(variant), false);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput out) {
        out.add(NarratedElementType.TITLE, createNarrationMessage());
    }

    /* ====================================================================== */

    private static WidgetSprites spriteFor(Variant variant) {
        return switch (variant) {
            case NORMAL -> NORMAL;
            case SUCCESS -> SUCCESS;
            case DANGER -> DANGER;
        };
    }

    private static int colorFor(Variant variant) {
        return switch (variant) {
            case NORMAL -> 0xFF1C1C1D;
            case SUCCESS, DANGER -> 0xFFFFFFFF;
        };
    }

}
