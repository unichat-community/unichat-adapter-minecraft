/*!******************************************************************************
 * Copyright (c) 2026 Voguh
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

package me.voguh.unichat.adapter.client.gui.screen.client;

import me.voguh.unichat.adapter.client.ClientConfig;
import me.voguh.unichat.adapter.client.gui.chat.ChatMessages;
import me.voguh.unichat.adapter.client.gui.component.CustomButton;
import me.voguh.unichat.adapter.client.gui.component.CustomButton.Variant;
import me.voguh.unichat.adapter.client.gui.component.CustomCheckbox;
import me.voguh.unichat.adapter.client.gui.component.CustomOptionGroup;
import me.voguh.unichat.adapter.client.gui.component.State;
import me.voguh.unichat.adapter.client.gui.screen.UniChatPanelScreen;
import me.voguh.unichat.adapter.util.IdentifierUtils;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.List;

public final class UniChatClientSettingsScreen extends UniChatPanelScreen {

    private static final Component TITLE = IdentifierUtils.gui("screen_client_settings");
    private static final Component RENDER_MESSAGES_LABEL = IdentifierUtils.gui("screen_client_settings.display_chat_messages");
    private static final Component IMAGES_SCALE_LABEL = IdentifierUtils.gui("screen_client_settings.images_scale");
    private static final Component CLEAR_CACHE_LABEL = IdentifierUtils.gui("screen_client_settings.clear_cache");
    private static final List<State<Integer>> IMAGES_SCALE = List.of(
        State.literal("1x", 1),
        State.literal("2x", 2),
        State.literal("3x", 3),
        State.literal("4x", 4)
    );

    private boolean displayChatMessages;
    private State<Integer> imagesScale;

    /* ====================================================================== */

    public UniChatClientSettingsScreen(Screen parent) {
        super(TITLE, parent);
        this.displayChatMessages = ClientConfig.renderMessages();
        this.imagesScale = State.literal(ClientConfig.imagesScale() + "x", ClientConfig.imagesScale());
    }

    /* ====================================================================== */

    @Override
    protected void addContents(LinearLayout layout) {
        layout.addChild(new CustomCheckbox(font, CONTENT_WIDTH, RENDER_MESSAGES_LABEL, displayChatMessages, this::onDisplayChatMessages));

        /* ================================================================== */

        layout.addChild(new CustomOptionGroup<>(font, CONTENT_WIDTH, IMAGES_SCALE_LABEL, imagesScale, this::onImagesScaleChange, IMAGES_SCALE));

        /* ================================================================== */

        layout.addChild(new CustomButton(font, CONTENT_WIDTH, CLEAR_CACHE_LABEL, Variant.DANGER, this::onClearCacheClick));

        /* ================================================================== */

        LinearLayout actions = LinearLayout.horizontal().spacing(SPACING);
        actions.addChild(new CustomButton(font, HALF_WIDTH, IdentifierUtils.GUI_SAVE, Variant.SUCCESS, this::apply));
        actions.addChild(new CustomButton(font, HALF_WIDTH, CommonComponents.GUI_BACK, this::cancel));

        layout.addChild(actions, (settings) -> settings.paddingTop(SPACING));
    }

    /* ====================================================================== */

    private void onClearCacheClick(CustomButton button) {
        minecraft.setScreen(new UniChatDeleteCacheScreen(this));
    }

    private void cancel(CustomButton button) {
        onClose();
    }

    private void apply(CustomButton button) {
        int newImagesScale = imagesScale.value();
        boolean rescaled = ClientConfig.imagesScale() != newImagesScale;

        ClientConfig.updateSettings(displayChatMessages, newImagesScale);
        if (rescaled) {
            ChatMessages.INSTANCE.reloadImages();
        }

        onClose();
    }

    /* ====================================================================== */

    private void onDisplayChatMessages(CustomCheckbox checkbox, boolean newValue) {
        displayChatMessages = newValue;
    }

    private void onImagesScaleChange(CustomOptionGroup<Integer> group, State<Integer> newValue) {
        imagesScale = newValue;
    }

}
