/*!******************************************************************************
 * Copyright (c) 2026 Voguh
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

package me.voguh.unichat.adapter.client;

import me.voguh.unichat.adapter.gui.UniChatSettingsMenuScreen;
import me.voguh.unichat.adapter.gui.chat.ChatMessages;
import me.voguh.unichat.adapter.util.IdentifierUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;

public final class ClientBootstrap {

    private static final int MARGIN = 8;

    public static void onScreenInit(ScreenEvent.Init.Post event) {
        Screen screen = event.getScreen();
        if (!(screen instanceof PauseScreen pauseScreen) || !pauseScreen.showsPauseMenu()) {
            return;
        }

        Component label = IdentifierUtils.translatable("menu_title");
        int yPos = screen.height - Button.DEFAULT_HEIGHT - MARGIN;

        Button openScreen = Button.builder(label, (btn) -> openSettings(screen))
            .bounds(MARGIN, yPos, Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT)
            .build();

        event.addListener(openScreen);
    }

    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        ChatMessages.INSTANCE.clear();
    }

    /* ====================================================================== */

    private static void openSettings(Screen parent) {
        Minecraft.getInstance().setScreen(new UniChatSettingsMenuScreen(parent));
    }

    /* ====================================================================== */

    private ClientBootstrap() {
        throw new UnsupportedOperationException("Utility class");
    }

}
