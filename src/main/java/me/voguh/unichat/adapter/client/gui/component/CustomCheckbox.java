package me.voguh.unichat.adapter.client.gui.component;

import me.voguh.unichat.adapter.util.IdentifierUtils;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiConsumer;

public final class CustomCheckbox extends AbstractButton {

    public static final int HEIGHT = 20;
    private static final ResourceLocation UNCHECKED = IdentifierUtils.getIdentifier("switch/off");
    private static final ResourceLocation CHECKED = IdentifierUtils.getIdentifier("switch/on");
    private static final int TRACK_WIDTH = 32;
    private static final int TRACK_HEIGHT = 16;

    private boolean selected;

    private final Font font;
    private final BiConsumer<CustomCheckbox, Boolean> onChange;

    /* ====================================================================== */

    public CustomCheckbox(Font font, int width, Component message, Boolean initialValue, BiConsumer<CustomCheckbox, Boolean> onChange) {
        this(font, 0, 0, width, message, initialValue, onChange);
    }

    public CustomCheckbox(Font font, int x, int y, int width, Component message, Boolean initialValue, BiConsumer<CustomCheckbox, Boolean> onChange) {
        super(x, y, width, HEIGHT, message);
        this.font = font;
        this.selected = initialValue;
        this.onChange = onChange;
    }

    /* ====================================================================== */

    @Override
    public void onPress() {
        selected = !selected;
        onChange.accept(this, selected);
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int x = getX();
        int y = getY();
        int glyph = font.lineHeight - 1;

        int trackY = y + (getHeight() - TRACK_HEIGHT) / 2;
        ResourceLocation track = selected ? CHECKED : UNCHECKED;
        graphics.blitSprite(track, x, trackY, TRACK_WIDTH, TRACK_HEIGHT);

        int textX = x + TRACK_WIDTH + 4;
        int textY = y + (getHeight() - glyph) / 2;
        graphics.drawString(font, getMessage(), textX, textY, 0xFFFFFFFF);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput out) {
        out.add(NarratedElementType.TITLE, createNarrationMessage());

        String usage = selected ? "narration.checkbox.usage.checked" : "narration.checkbox.usage.unchecked";
        out.add(NarratedElementType.USAGE, Component.translatable(usage));
    }

}
