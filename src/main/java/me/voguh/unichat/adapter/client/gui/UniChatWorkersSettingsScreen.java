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

import me.voguh.unichat.adapter.client.ServerStateHolder;
import me.voguh.unichat.adapter.client.gui.component.CustomEntryList;
import me.voguh.unichat.adapter.client.gui.component.CustomEntryList.RowAction;
import me.voguh.unichat.adapter.network.UniChatNetwork;
import me.voguh.unichat.adapter.network.packet.client.ReloadWorkersPayload;
import me.voguh.unichat.adapter.network.packet.client.SaveWorkersPayload;
import me.voguh.unichat.adapter.util.IdentifierUtils;
import me.voguh.unichat.adapter.util.Strings;
import me.voguh.unichat.adapter.worker.RawWorker;
import me.voguh.unichat.adapter.worker.loader.WorkerLoader;
import net.minecraft.Util;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public final class UniChatWorkersSettingsScreen extends UniChatPanelScreen {

    private static final Component EMPTY_MESSAGE = IdentifierUtils.translatable("screen_workers_settings.empty");
    private static final Component UNNAMED = IdentifierUtils.translatable("screen_workers_settings.unnamed");

    private static final Component DUPLICATE_LABEL = IdentifierUtils.translatable("duplicate");
    private static final ResourceLocation DUPLICATE_ICON = IdentifierUtils.getIdentifier("icon/duplicate");
    private static final Component DELETE_LABEL = IdentifierUtils.translatable("delete");
    private static final ResourceLocation DELETE_ICON = IdentifierUtils.getIdentifier("icon/delete");

    private static final int VISIBLE_ROWS = 6;
    private static final int LIST_HEIGHT = CustomEntryList.heightFor(VISIBLE_ROWS);

    private List<RawWorker> displayed;
    private CustomEntryList list;

    /* ====================================================================== */

    public UniChatWorkersSettingsScreen(Screen parent) {
        super(IdentifierUtils.translatable("screen_workers_settings"), parent);
        this.displayed = ServerStateHolder.INSTANCE.workers();
    }

    /* ====================================================================== */

    @Override
    protected void addContents(LinearLayout layout) {
        RowAction duplicate = new RowAction(DUPLICATE_LABEL, DUPLICATE_ICON, this::duplicateWorker);
        RowAction delete = new RowAction(DELETE_LABEL, DELETE_ICON, this::deleteWorker);

        list = new CustomEntryList(minecraft, CONTENT_WIDTH, LIST_HEIGHT, EMPTY_MESSAGE, this::editWorker, List.of(duplicate, delete));
        list.refresh(labels(displayed));
        layout.addChild(list);

        /* ================================================================== */

        LinearLayout actions = LinearLayout.horizontal().spacing(SPACING);
        actions.addChild(Button.builder(IdentifierUtils.translatable("new"), this::createWorker).width(HALF_WIDTH).build());
        actions.addChild(Button.builder(IdentifierUtils.translatable("reload"), this::reload).width(HALF_WIDTH).build());

        layout.addChild(actions, (settings) -> settings.paddingTop(SPACING));

        /* ================================================================== */

        if (minecraft.hasSingleplayerServer()) {
            layout.addChild(Button.builder(IdentifierUtils.translatable("open_file"), this::openFile).width(CONTENT_WIDTH).build());
        }

        /* ================================================================== */

        layout.addChild(Button.builder(CommonComponents.GUI_BACK, this::cancel).width(CONTENT_WIDTH).build());
    }

    @Override
    public void tick() {
        List<RawWorker> workers = ServerStateHolder.INSTANCE.workers();
        if (!workers.equals(displayed)) {
            displayed = workers;
            list.refresh(labels(workers));
        }
    }

    /* ====================================================================== */

    private void cancel(Button button) {
        onClose();
    }

    private void createWorker(Button button) {
        minecraft.setScreen(new UniChatWorkerSettingsScreen(this));
    }

    private void editWorker(int index) {
        minecraft.setScreen(new UniChatWorkerSettingsScreen(this, displayed.get(index), index));
    }

    private void duplicateWorker(int index) {
        List<RawWorker> workers = new ArrayList<>(displayed);
        workers.add(index + 1, workers.get(index));

        UniChatNetwork.sendToServer(new SaveWorkersPayload(workers));
    }

    private void deleteWorker(int index) {
        List<RawWorker> workers = new ArrayList<>(displayed);
        workers.remove(index);

        UniChatNetwork.sendToServer(new SaveWorkersPayload(workers));
    }

    private void reload(Button button) {
        UniChatNetwork.sendToServer(new ReloadWorkersPayload());
    }

    private void openFile(Button button) {
        Util.getPlatform().openPath(WorkerLoader.file());
    }

    /* ====================================================================== */

    private static List<Component> labels(List<RawWorker> workers) {
        return workers.stream().map(UniChatWorkersSettingsScreen::label).toList();
    }

    private static Component label(RawWorker worker) {
        String name = worker.name();
        if (Strings.isNullOrEmpty(name)) {
            return UNNAMED;
        }

        return Component.literal(name);
    }

}
