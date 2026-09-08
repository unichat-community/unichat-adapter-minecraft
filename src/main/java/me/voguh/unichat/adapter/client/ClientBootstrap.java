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
import com.twelvemonkeys.imageio.plugins.webp.WebPImageReaderSpi;
import me.voguh.unichat.adapter.UniChatAdapter;
import me.voguh.unichat.adapter.gui.UniChatSettingsMenuScreen;
import me.voguh.unichat.adapter.gui.chat.ChatMessages;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import org.lwjgl.glfw.GLFW;

import javax.imageio.spi.IIORegistry;

public final class ClientBootstrap {

    private static final KeyMapping.Category UNICHAT_CATEGORY = new KeyMapping.Category(Identifier.fromNamespaceAndPath(UniChatAdapter.MODID, "unichat_adapter"));
    private static final KeyMapping OPEN_SCREEN = new KeyMapping("key." + UniChatAdapter.MODID + ".open_screen", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_U, UNICHAT_CATEGORY);

    public static void register() {
        IIORegistry.getDefaultInstance().registerServiceProvider(new WebPImageReaderSpi());
        RegisterKeyMappingsEvent.BUS.addListener(ClientBootstrap::onRegisterKeyMappings);
        TickEvent.ClientTickEvent.Post.BUS.addListener(ClientBootstrap::onClientTick);
        ClientPlayerNetworkEvent.LoggingOut.BUS.addListener(ClientBootstrap::onLoggingOut);
    }

    /* ====================================================================== */

    private static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(OPEN_SCREEN);
    }

    private static void onClientTick(TickEvent.ClientTickEvent.Post event) {
        if (!OPEN_SCREEN.consumeClick()) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        minecraft.setScreen(new UniChatSettingsMenuScreen());
    }

    private static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        ChatMessages.INSTANCE.clear();
    }

    /* ====================================================================== */

    private ClientBootstrap() {
        throw new UnsupportedOperationException("Utility class");
    }

}
