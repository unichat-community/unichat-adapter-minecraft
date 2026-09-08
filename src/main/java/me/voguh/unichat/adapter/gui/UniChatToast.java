package me.voguh.unichat.adapter.gui;

import me.voguh.unichat.adapter.util.IdentifierUtils;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public final class UniChatToast implements Toast {

    private static final Identifier BACKGROUND = IdentifierUtils.getIdentifier("toast/unichat_toast");

    private long lastTime;

    private final Component title;
    private final Component message;

    /* ====================================================================== */

    public UniChatToast(Component title, Component message) {
        this.title = title;
        this.message = message;
    }

    /* ====================================================================== */

    @Override
    public int width() {
        return 180;
    }

    @Override
    public int height() {
        return 36;
    }

    /* ====================================================================== */

    @Override
    public @NotNull Object getToken() {
        return "unichat_toast";
    }

    @Override
    public void update(@NotNull ToastManager toastManager, long time) {
        this.lastTime = time;
    }

    @Override
    public @NotNull Visibility getWantedVisibility() {
        return lastTime < 5000L ? Visibility.SHOW : Visibility.HIDE;
    }

    @Override
    public void render(GuiGraphics graphics, @NotNull Font font, long time) {
        int xPos = 0;
        int yPos = 0;
        int width = width();
        int height = height();

        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND, xPos, yPos, width, height);

        xPos = 36;
        int margin = 2;
        int inner = height - margin * 2;
        int glyph = font.lineHeight - 1;
        int block = glyph * 2;
        int gap = (inner - block) / 3;

        yPos = margin + gap;
        graphics.drawString(font, title, xPos, yPos, 0xFFB02A37, false);

        yPos += 8 + gap;
        graphics.drawString(font, message, xPos, yPos, 0xFFFFFFFF, false);
    }

}
