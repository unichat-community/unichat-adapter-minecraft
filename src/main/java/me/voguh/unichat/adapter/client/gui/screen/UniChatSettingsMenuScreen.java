/*!******************************************************************************
 * Copyright (c) 2026 Voguh
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

package me.voguh.unichat.adapter.client.gui.screen;

import me.voguh.unichat.adapter.client.gui.screen.client.UniChatClientSettingsScreen;
import me.voguh.unichat.adapter.client.gui.screen.server.UniChatServerSettingsScreen;
import me.voguh.unichat.adapter.client.gui.screen.worker.UniChatWorkersSettingsScreen;
import me.voguh.unichat.adapter.util.IdentifierUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public final class UniChatSettingsMenuScreen extends UniChatPanelScreen {

    public UniChatSettingsMenuScreen(Screen parent) {
        super(IdentifierUtils.translatable("menu_title"), parent);
    }

    private boolean canManageServer() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.hasSingleplayerServer()) {
            return true;
        }

        LocalPlayer player = minecraft.player;
        return player != null && player.hasPermissions(Commands.LEVEL_ADMINS);
    }

    /* ====================================================================== */

    @Override
    protected void addContents(LinearLayout layout) {
        Button client = Button.builder(IdentifierUtils.translatable("btn_client"), this::onClientTabClick).width(CONTENT_WIDTH).build();
        layout.addChild(client);

        /* ================================================================== */

        if (canManageServer()) {
            Button server = Button.builder(IdentifierUtils.translatable("btn_server"), this::onServerTabClick).width(CONTENT_WIDTH).build();
            layout.addChild(server);

            Component workersLabel = IdentifierUtils.translatable("btn_workers");
            Button workers = Button.builder(workersLabel, this::onWorkersTabClick).width(CONTENT_WIDTH).build();
            layout.addChild(workers);
        }

        /* ================================================================== */

        Button back = Button.builder(CommonComponents.GUI_BACK, (btn) -> onClose()).width(CONTENT_WIDTH).build();
        layout.addChild(back, (settings) -> settings.paddingTop(SPACING));
    }

    /* ====================================================================== */

    private void onClientTabClick(Button button) {
        minecraft.setScreen(new UniChatClientSettingsScreen(this));
    }

    private void onServerTabClick(Button button) {
        minecraft.setScreen(new UniChatServerSettingsScreen(this));
    }

    private void onWorkersTabClick(Button button) {
        minecraft.setScreen(new UniChatWorkersSettingsScreen(this));
    }

}
