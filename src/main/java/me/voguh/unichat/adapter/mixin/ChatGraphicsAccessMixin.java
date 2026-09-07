/*!******************************************************************************
 * Copyright (c) 2026 Voguh
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

package me.voguh.unichat.adapter.mixin;

import me.voguh.unichat.adapter.gui.chat.InlineImageRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(remap = false, targets = {
    "net.minecraft.client.gui.components.ChatComponent$DrawingBackgroundGraphicsAccess",
    "net.minecraft.client.gui.components.ChatComponent$DrawingFocusedGraphicsAccess"
})
public abstract class ChatGraphicsAccessMixin {

    @Final
    @Shadow(remap = false)
    private GuiGraphics graphics;

    @Inject(remap = false, method = "handleMessage", at = @At(value = "RETURN", remap = false))
    private void unichat_adapter$drawInlineImages(int y, float alpha, FormattedCharSequence content, CallbackInfoReturnable<Boolean> callback) {
        InlineImageRenderer.render(graphics, y, alpha, content);
    }

}
