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

import com.mojang.blaze3d.platform.InputConstants;
import me.voguh.unichat.adapter.UniChatAdapter;
import me.voguh.unichat.adapter.gui.UniChatSettingsMenuScreen;
import me.voguh.unichat.adapter.gui.chat.ChatMessages;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

public final class ClientBootstrap {

    private static final KeyMapping OPEN_SCREEN = new KeyMapping("key." + UniChatAdapter.MODID + ".open_screen", InputConstants.Type.KEYSYM, InputConstants.KEY_U, KeyMapping.CATEGORY_MISC);

    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(OPEN_SCREEN);
    }

    public static void onClientTick(ClientTickEvent.Post event) {
        if (!OPEN_SCREEN.consumeClick()) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        minecraft.setScreen(new UniChatSettingsMenuScreen());
    }

    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        ChatMessages.INSTANCE.clear();
    }

    /* ====================================================================== */

    private ClientBootstrap() {
        throw new UnsupportedOperationException("Utility class");
    }

}
