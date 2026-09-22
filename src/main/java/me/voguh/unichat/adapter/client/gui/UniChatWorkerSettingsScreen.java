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
import me.voguh.unichat.adapter.event.UniChatEventUtils;
import me.voguh.unichat.adapter.network.UniChatNetwork;
import me.voguh.unichat.adapter.network.packet.client.SaveWorkersPayload;
import me.voguh.unichat.adapter.util.IdentifierUtils;
import me.voguh.unichat.adapter.util.Strings;
import me.voguh.unichat.adapter.worker.RawAction;
import me.voguh.unichat.adapter.worker.RawCondition;
import me.voguh.unichat.adapter.worker.RawWorker;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class UniChatWorkerSettingsScreen extends UniChatPanelScreen {

    private static final Component TITLE = IdentifierUtils.translatable("screen_worker_settings");
    private static final Component CONDITIONS_LABEL = IdentifierUtils.translatable("screen_worker_settings.conditions");
    private static final Component COMMANDS_LABEL = IdentifierUtils.translatable("screen_worker_settings.commands");

    private static final List<String> EVENT_TYPES = List.copyOf(UniChatEventUtils.getEventTypeMap());

    private static final int NAME_MAX_LENGTH = 128;
    private static final int NEW_WORKER = -1;

    private String name;
    private String eventType;

    private Button conditionsButton;
    private Button commandsButton;
    private Button saveButton;

    private final List<RawCondition> conditions;
    private final List<String> commands;
    private final int index;

    /* ====================================================================== */

    public UniChatWorkerSettingsScreen(Screen parent) {
        super(TITLE, parent);
        this.conditions = new ArrayList<>();
        this.commands = new ArrayList<>();
        this.index = NEW_WORKER;
        this.name = "";
        this.eventType = EVENT_TYPES.getFirst();
    }

    public UniChatWorkerSettingsScreen(Screen parent, RawWorker worker, int index) {
        super(TITLE, parent);
        this.conditions = conditions(worker);
        this.commands = commands(worker);
        this.index = index;
        this.name = name(worker);
        this.eventType = eventType(worker);
    }

    /* ====================================================================== */

    @Override
    protected void addContents(LinearLayout layout) {
        Component nameLabel = IdentifierUtils.translatable("screen_worker_settings.name");
        layout.addChild(new StringWidget(CONTENT_WIDTH, font.lineHeight, nameLabel, font).alignLeft());

        EditBox nameBox = new EditBox(font, 0, 0, CONTENT_WIDTH, Button.DEFAULT_HEIGHT, nameLabel);
        nameBox.setMaxLength(NAME_MAX_LENGTH);
        nameBox.setValue(name);
        nameBox.setResponder(this::onNameChange);
        layout.addChild(nameBox);

        /* ================================================================== */

        Component eventLabel = IdentifierUtils.translatable("screen_worker_settings.on_event");
        CycleButton<String> eventButton = CycleButton.<String>builder(UniChatWorkerSettingsScreen::eventName)
            .withValues(EVENT_TYPES)
            .withInitialValue(eventType)
            .create(0, 0, CONTENT_WIDTH, Button.DEFAULT_HEIGHT, eventLabel, this::onEventChange);
        layout.addChild(eventButton);

        /* ================================================================== */

        conditionsButton = Button.builder(CONDITIONS_LABEL, this::openConditions).width(CONTENT_WIDTH).build();
        layout.addChild(conditionsButton);

        commandsButton = Button.builder(COMMANDS_LABEL, this::openCommands).width(CONTENT_WIDTH).build();
        layout.addChild(commandsButton);

        /* ================================================================== */

        saveButton = Button.builder(CommonComponents.GUI_DONE, this::apply).width(HALF_WIDTH).build();

        LinearLayout actions = LinearLayout.horizontal().spacing(SPACING);
        actions.addChild(saveButton);
        actions.addChild(Button.builder(CommonComponents.GUI_BACK, this::cancel).width(HALF_WIDTH).build());

        layout.addChild(actions, (settings) -> settings.paddingTop(SPACING));
    }

    @Override
    protected void repositionElements() {
        refreshButtons();
        super.repositionElements();
    }

    /* ====================================================================== */

    private void refreshButtons() {
        conditionsButton.setMessage(counterLabel(CONDITIONS_LABEL, conditions.size()));
        commandsButton.setMessage(counterLabel(COMMANDS_LABEL, commands.size()));
        saveButton.active = !Strings.isNullOrEmpty(name);
    }

    private boolean unknownProperty(RawCondition condition) {
        String property = condition.property();

        return property == null || UniChatEventUtils.getEventProperty(eventType, property).isEmpty();
    }

    /* ====================================================================== */

    private void onNameChange(String value) {
        name = value;
        refreshButtons();
    }

    private void onEventChange(CycleButton<String> button, String newValue) {
        eventType = newValue;
        conditions.removeIf(this::unknownProperty);
        refreshButtons();
    }

    private void openConditions(Button button) {
        minecraft.setScreen(new UniChatWorkerConditionsScreen(this, eventType, conditions));
    }

    private void openCommands(Button button) {
        minecraft.setScreen(new UniChatWorkerCommandsScreen(this, commands));
    }

    /* ====================================================================== */

    private void cancel(Button button) {
        onClose();
    }

    private void apply(Button button) {
        RawWorker worker = new RawWorker(name, eventType, List.copyOf(conditions), new RawAction(List.copyOf(commands)));

        List<RawWorker> workers = new ArrayList<>(ServerStateHolder.INSTANCE.workers());
        if (index == NEW_WORKER || index >= workers.size()) {
            workers.add(worker);
        } else {
            workers.set(index, worker);
        }

        UniChatNetwork.sendToServer(new SaveWorkersPayload(workers));
        onClose();
    }

    /* ====================================================================== */

    private static List<RawCondition> conditions(RawWorker worker) {
        List<RawCondition> conditions = worker.conditions();
        if (conditions == null) {
            return new ArrayList<>();
        }

        return conditions.stream().filter(Objects::nonNull).collect(Collectors.toCollection(ArrayList::new));
    }

    private static List<String> commands(RawWorker worker) {
        RawAction actions = worker.actions();
        if (actions == null || actions.execCommands() == null) {
            return new ArrayList<>();
        }

        return actions.execCommands().stream().filter(Objects::nonNull).collect(Collectors.toCollection(ArrayList::new));
    }

    private static String name(RawWorker worker) {
        String name = worker.name();

        return name == null ? "" : name;
    }

    private static String eventType(RawWorker worker) {
        String onEvent = worker.onEvent();
        if (onEvent == null || !EVENT_TYPES.contains(onEvent)) {
            return EVENT_TYPES.getFirst();
        }

        return onEvent;
    }

    private static Component counterLabel(Component label, int count) {
        return CommonComponents.optionNameValue(label, Component.literal(String.valueOf(count)));
    }

    private static Component eventName(String eventType) {
        return IdentifierUtils.translatable("event." + eventType.substring(eventType.indexOf(':') + 1));
    }

}
