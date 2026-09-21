package me.voguh.unichat.adapter.gui;

import me.voguh.unichat.adapter.util.IdentifierUtils;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public final class UniChatToast implements Toast {

    private static final ResourceLocation BACKGROUND = IdentifierUtils.getIdentifier("toast/unichat_toast");
    private static final long DISPLAY_TIME = 5000L;

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
    public @NotNull Visibility render(GuiGraphics graphics, @NotNull ToastComponent toastComponent, long timeSinceLastVisible) {
        Font font = toastComponent.getMinecraft().font;

        int xPos = 0;
        int yPos = 0;
        int width = width();
        int height = height();

        graphics.blitSprite(BACKGROUND, xPos, yPos, width, height);

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

        return timeSinceLastVisible < DISPLAY_TIME ? Visibility.SHOW : Visibility.HIDE;
    }

}
