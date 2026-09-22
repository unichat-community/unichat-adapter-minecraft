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

import me.voguh.unichat.adapter.client.gui.component.CustomEntryList;
import me.voguh.unichat.adapter.client.gui.component.CustomEntryList.RowAction;
import me.voguh.unichat.adapter.util.IdentifierUtils;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public final class UniChatWorkerCommandsScreen extends UniChatPanelScreen {

    private static final Component TITLE = IdentifierUtils.translatable("screen_worker_commands");
    private static final Component EMPTY_MESSAGE = IdentifierUtils.translatable("screen_worker_commands.empty");

    private static final Component DELETE_LABEL = IdentifierUtils.translatable("delete");
    private static final ResourceLocation DELETE_ICON = IdentifierUtils.getIdentifier("icon/delete");

    private static final int VISIBLE_ROWS = 6;
    private static final int LIST_HEIGHT = CustomEntryList.heightFor(VISIBLE_ROWS);

    private final List<String> commands;

    private CustomEntryList list;

    /* ====================================================================== */

    public UniChatWorkerCommandsScreen(Screen parent, List<String> commands) {
        super(TITLE, parent);
        this.commands = commands;
    }

    /* ====================================================================== */

    @Override
    protected void addContents(LinearLayout layout) {
        RowAction delete = new RowAction(DELETE_LABEL, DELETE_ICON, this::deleteCommand);

        list = new CustomEntryList(minecraft, CONTENT_WIDTH, LIST_HEIGHT, EMPTY_MESSAGE, this::editCommand, List.of(delete));
        layout.addChild(list);

        /* ================================================================== */

        LinearLayout actions = LinearLayout.horizontal().spacing(SPACING);
        actions.addChild(Button.builder(IdentifierUtils.translatable("new"), this::createCommand).width(HALF_WIDTH).build());
        actions.addChild(Button.builder(CommonComponents.GUI_BACK, this::cancel).width(HALF_WIDTH).build());

        layout.addChild(actions, (settings) -> settings.paddingTop(SPACING));
    }

    @Override
    protected void repositionElements() {
        list.refresh(labels());
        super.repositionElements();
    }

    /* ====================================================================== */

    private List<Component> labels() {
        return commands.stream().<Component>map(Component::literal).toList();
    }

    /* ====================================================================== */

    private void cancel(Button button) {
        onClose();
    }

    private void createCommand(Button button) {
        minecraft.setScreen(new UniChatWorkerCommandScreen(this, commands));
    }

    private void editCommand(int index) {
        minecraft.setScreen(new UniChatWorkerCommandScreen(this, commands, index));
    }

    private void deleteCommand(int index) {
        commands.remove(index);
        list.refresh(labels());
    }

}
