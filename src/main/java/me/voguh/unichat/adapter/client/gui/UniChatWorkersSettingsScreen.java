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
import me.voguh.unichat.adapter.dto.RawWorker;
import me.voguh.unichat.adapter.network.UniChatNetwork;
import me.voguh.unichat.adapter.network.packet.client.ReloadWorkersPayload;
import me.voguh.unichat.adapter.server.worker.loader.WorkerLoader;
import me.voguh.unichat.adapter.util.IdentifierUtils;
import net.minecraft.Util;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;

import java.util.List;

public final class UniChatWorkersSettingsScreen extends UniChatPanelScreen {

    private static final int VISIBLE_ROWS = 6;
    private static final int LIST_HEIGHT = UniChatWorkerList.heightFor(VISIBLE_ROWS);

    private List<RawWorker> displayed;
    private UniChatWorkerList list;

    /* ====================================================================== */

    public UniChatWorkersSettingsScreen(Screen parent) {
        super(IdentifierUtils.translatable("screen_workers_settings"), parent);
        this.displayed = ServerStateHolder.INSTANCE.workers();
    }

    /* ====================================================================== */

    @Override
    protected void addContents(LinearLayout layout) {
        list = new UniChatWorkerList(minecraft, CONTENT_WIDTH, LIST_HEIGHT, displayed);
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
            list.refresh(workers);
        }
    }

    /* ====================================================================== */

    private void cancel(Button button) {
        onClose();
    }

    private void createWorker(Button button) {
    }

    private void reload(Button button) {
        UniChatNetwork.sendToServer(new ReloadWorkersPayload());
    }

    private void openFile(Button button) {
        Util.getPlatform().openPath(WorkerLoader.file());
    }

}
