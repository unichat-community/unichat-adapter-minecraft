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

import me.voguh.unichat.adapter.util.IdentifierUtils;
import me.voguh.unichat.adapter.util.Strings;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.List;

public final class UniChatWorkerCommandScreen extends UniChatPanelScreen {

    private static final Component TITLE = IdentifierUtils.translatable("screen_worker_command");

    private static final int COMMAND_MAX_LENGTH = 1024;
    private static final int NEW_COMMAND = -1;

    private String command;
    private Button saveButton;

    private final List<String> commands;
    private final int index;

    /* ====================================================================== */

    public UniChatWorkerCommandScreen(Screen parent, List<String> commands) {
        this(parent, commands, NEW_COMMAND, "");
    }

    public UniChatWorkerCommandScreen(Screen parent, List<String> commands, int index) {
        this(parent, commands, index, commands.get(index));
    }

    private UniChatWorkerCommandScreen(Screen parent, List<String> commands, int index, String command) {
        super(TITLE, parent);
        this.commands = commands;
        this.index = index;
        this.command = command;
    }

    /* ====================================================================== */

    @Override
    protected void addContents(LinearLayout layout) {
        Component commandLabel = IdentifierUtils.translatable("screen_worker_command.command");
        layout.addChild(new StringWidget(CONTENT_WIDTH, font.lineHeight, commandLabel, font).alignLeft());

        EditBox commandBox = new EditBox(font, 0, 0, CONTENT_WIDTH, Button.DEFAULT_HEIGHT, commandLabel);
        commandBox.setMaxLength(COMMAND_MAX_LENGTH);
        commandBox.setValue(command);
        commandBox.setResponder(this::onCommandChange);
        layout.addChild(commandBox);

        /* ================================================================== */

        saveButton = Button.builder(IdentifierUtils.translatable("save"), this::apply).width(HALF_WIDTH).build();

        LinearLayout actions = LinearLayout.horizontal().spacing(SPACING);
        actions.addChild(saveButton);
        actions.addChild(Button.builder(CommonComponents.GUI_BACK, this::cancel).width(HALF_WIDTH).build());

        layout.addChild(actions, (settings) -> settings.paddingTop(SPACING));
    }

    @Override
    protected void repositionElements() {
        saveButton.active = !Strings.isNullOrEmpty(command);
        super.repositionElements();
    }

    /* ====================================================================== */

    private void onCommandChange(String newValue) {
        command = newValue;
        saveButton.active = !Strings.isNullOrEmpty(newValue);
    }

    /* ====================================================================== */

    private void cancel(Button button) {
        onClose();
    }

    private void apply(Button button) {
        if (index == NEW_COMMAND) {
            commands.add(command);
        } else {
            commands.set(index, command);
        }

        onClose();
    }

}
