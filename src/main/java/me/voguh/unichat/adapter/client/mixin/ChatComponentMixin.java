/*!******************************************************************************
 * Copyright (c) 2026 Voguh
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

package me.voguh.unichat.adapter.client.mixin;

import me.voguh.unichat.adapter.client.gui.chat.InlineImageRenderer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ChatComponent.class, remap = false)
public abstract class ChatComponentMixin {

    @Redirect(remap = false, method = "render", at = @At(value = "INVOKE", remap = false,
        target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/util/FormattedCharSequence;III)I"))
    private int unichat_adapter$drawInlineImages(GuiGraphics graphics, Font font, FormattedCharSequence content, int x, int y, int color) {
        int width = graphics.drawString(font, content, x, y, color);
        InlineImageRenderer.render(graphics, x, y, (color >>> 24) / 255.0F, content);

        return width;
    }

}
