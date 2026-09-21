/*!******************************************************************************
 * Copyright (c) 2026 Voguh
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

package me.voguh.unichat.adapter.client.gui;

import me.voguh.unichat.adapter.client.ClientConfig;
import me.voguh.unichat.adapter.client.gui.chat.ChatMessages;
import me.voguh.unichat.adapter.util.IdentifierUtils;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public final class UniChatClientSettingsScreen extends UniChatPanelScreen {

    private boolean displayChatMessages;
    private int supersample;

    /* ====================================================================== */

    public UniChatClientSettingsScreen(Screen parent) {
        super(IdentifierUtils.translatable("screen_client_settings"), parent);
        this.displayChatMessages = ClientConfig.renderMessages();
        this.supersample = ClientConfig.supersample();
    }

    /* ====================================================================== */

    @Override
    protected void addContents(LinearLayout layout) {
        Component renderMessagesLabel = IdentifierUtils.translatable("screen_client_settings.display_chat_messages");
        Checkbox renderMessages = Checkbox.builder(renderMessagesLabel, font)
            .selected(displayChatMessages)
            .maxWidth(CONTENT_WIDTH)
            .onValueChange(this::onDisplayChatMessages)
            .build();
        layout.addChild(renderMessages);

        /* ================================================================== */

        Component supersampleLabel = IdentifierUtils.translatable("screen_client_settings.supersample");
        CycleButton<Integer> supersampleButton = CycleButton.<Integer>builder(value -> Component.literal(value + "x"))
            .withValues(1, 2, 3, 4)
            .withInitialValue(supersample)
            .create(0, 0, CONTENT_WIDTH, Button.DEFAULT_HEIGHT, supersampleLabel, this::onSupersampleChange);
        layout.addChild(supersampleButton);

        /* ================================================================== */

        Component clearCacheLabel = IdentifierUtils.translatable("screen_client_settings.clear_cache");
        Button clearCache = Button.builder(clearCacheLabel, this::onClearCacheClick).width(CONTENT_WIDTH).build();
        layout.addChild(clearCache);

        /* ================================================================== */

        LinearLayout actions = LinearLayout.horizontal().spacing(SPACING);
        actions.addChild(Button.builder(CommonComponents.GUI_DONE, this::apply).width(HALF_WIDTH).build());
        actions.addChild(Button.builder(CommonComponents.GUI_BACK, this::cancel).width(HALF_WIDTH).build());

        layout.addChild(actions, (settings) -> settings.paddingTop(SPACING));
    }

    /* ====================================================================== */

    private void onClearCacheClick(Button button) {
        minecraft.setScreen(new UniChatDeleteCacheScreen(this));
    }

    private void cancel(Button button) {
        onClose();
    }

    private void apply(Button button) {
        boolean rescaled = ClientConfig.supersample() != supersample;

        ClientConfig.updateSettings(displayChatMessages, supersample);
        if (rescaled) {
            ChatMessages.INSTANCE.reloadImages();
        }

        onClose();
    }

    /* ====================================================================== */

    private void onDisplayChatMessages(Checkbox checkbox, boolean newValue) {
        displayChatMessages = newValue;
    }

    private void onSupersampleChange(CycleButton<Integer> button, Integer newValue) {
        supersample = newValue;
    }

}
