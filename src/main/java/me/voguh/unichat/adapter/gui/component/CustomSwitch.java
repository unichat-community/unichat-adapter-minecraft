package me.voguh.unichat.adapter.gui.component;

import me.voguh.unichat.adapter.util.IdentifierUtils;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

public final class CustomSwitch extends AbstractButton {

    private static final Identifier UNCKECKED = IdentifierUtils.getIdentifier("switch/off");
    private static final Identifier CHECKED = IdentifierUtils.getIdentifier("switch/on");
    public static final int HEIGHT = 20;

    private boolean selected;

    private final Font font;
    private final BiConsumer<CustomSwitch, Boolean> onPress;

    /* ====================================================================== */

    public CustomSwitch(Font font, int x, int y, int width, Component msg, BiConsumer<CustomSwitch, Boolean> onPress, boolean initialState) {
        super(x, y, width, HEIGHT, msg);
        this.selected = initialState;
        this.font = font;
        this.onPress = onPress;
    }

    /* ====================================================================== */

    @Override
    public void onPress(@NotNull InputWithModifiers mod) {
        selected = !selected;
        onPress.accept(this, selected);
    }

    @Override
    protected void renderContents(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int x = getX();
        int y = getY();
        int width = getWidth();
        int height = getHeight();
        int glyph = font.lineHeight - 1;

        /* ================================================================== */

        int wSwitch = 32;
        int hSwitch = 16;
        int xSwitch = x;
        int ySwitch = y + (height - hSwitch) / 2;

        Identifier texture = selected ? CHECKED : UNCKECKED;
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, texture, xSwitch, ySwitch, wSwitch, hSwitch);

        /* ================================================================== */

        int xText = x + wSwitch + 4;
        int yText = y + (height - glyph) / 2;
        graphics.drawString(font, getMessage(), xText, yText, 0xFFFFFFFF);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput out) {
        out.add(NarratedElementType.TITLE, createNarrationMessage());

        String usage = selected ? "narration.checkbox.usage.checked" : "narration.checkbox.usage.unchecked";
        out.add(NarratedElementType.USAGE, Component.translatable(usage));
    }

}
