/*!******************************************************************************
 * Copyright (c) 2026 Voguh
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

package me.voguh.unichat.adapter.client.gui.screen.worker;

import me.voguh.unichat.adapter.client.gui.component.CustomButton;
import me.voguh.unichat.adapter.client.gui.component.CustomButton.Variant;
import me.voguh.unichat.adapter.client.gui.component.CustomEditBox;
import me.voguh.unichat.adapter.client.gui.screen.UniChatPanelScreen;
import me.voguh.unichat.adapter.util.IdentifierUtils;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.List;

public final class UniChatWorkerCommandScreen extends UniChatPanelScreen {

    private static final Component TITLE = IdentifierUtils.gui("screen_worker_command");
    private static final Component COMMAND_LABEL = IdentifierUtils.gui("screen_worker_command.command");

    private static final int COMMAND_MAX_LENGTH = 1024;
    private static final int NEW_COMMAND = -1;

    private String command;

    private final List<String> commands;
    private final Runnable onApply;
    private final int index;

    /* ====================================================================== */

    public UniChatWorkerCommandScreen(Screen parent, List<String> commands, Runnable onApply) {
        this(parent, commands, onApply, NEW_COMMAND, "");
    }

    public UniChatWorkerCommandScreen(Screen parent, List<String> commands, Runnable onApply, int index) {
        this(parent, commands, onApply, index, commands.get(index));
    }

    private UniChatWorkerCommandScreen(Screen parent, List<String> commands, Runnable onApply, int index, String command) {
        super(TITLE, parent);
        this.commands = commands;
        this.onApply = onApply;
        this.index = index;
        this.command = command;
    }

    /* ====================================================================== */

    @Override
    protected void addContents(LinearLayout layout) {
        layout.addChild(new CustomEditBox(
            font,
            CONTENT_WIDTH,
            COMMAND_LABEL,
            command,
            this::onCommandChange,
            COMMAND_MAX_LENGTH
        ));

        /* ================================================================== */

        LinearLayout actions = LinearLayout.horizontal().spacing(SPACING);
        actions.addChild(new CustomButton(font, HALF_WIDTH, CommonComponents.GUI_OK, Variant.SUCCESS, this::apply));
        actions.addChild(new CustomButton(font, HALF_WIDTH, CommonComponents.GUI_BACK, this::cancel));
        layout.addChild(actions, (settings) -> settings.paddingTop(SPACING));
    }

    /* ====================================================================== */

    private void onCommandChange(CustomEditBox editBox, String newValue) {
        command = newValue;
    }

    /* ====================================================================== */

    private void cancel(CustomButton button) {
        onClose();
    }

    private void apply(CustomButton button) {
        if (index == NEW_COMMAND) {
            commands.add(command);
        } else {
            commands.set(index, command);
        }

        onApply.run();
        onClose();
    }

}
