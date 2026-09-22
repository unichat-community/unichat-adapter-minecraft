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

import me.voguh.unichat.adapter.client.ServerStateHolder;
import me.voguh.unichat.adapter.client.gui.component.CustomButton;
import me.voguh.unichat.adapter.client.gui.component.CustomButton.Variant;
import me.voguh.unichat.adapter.client.gui.component.CustomEditBox;
import me.voguh.unichat.adapter.client.gui.component.CustomEntryList;
import me.voguh.unichat.adapter.client.gui.component.CustomEntryList.RowAction;
import me.voguh.unichat.adapter.client.gui.component.CustomLabeledCycleButton;
import me.voguh.unichat.adapter.client.gui.component.State;
import me.voguh.unichat.adapter.client.gui.screen.UniChatPanelScreen;
import me.voguh.unichat.adapter.event.UniChatEventUtils;
import me.voguh.unichat.adapter.network.UniChatNetwork;
import me.voguh.unichat.adapter.network.packet.client.SaveWorkersPayload;
import me.voguh.unichat.adapter.util.IdentifierUtils;
import me.voguh.unichat.adapter.worker.RawAction;
import me.voguh.unichat.adapter.worker.RawCondition;
import me.voguh.unichat.adapter.worker.RawWorker;
import me.voguh.unichat.adapter.worker.WorkerValidationException;
import me.voguh.unichat.adapter.worker.loader.RawWorkerParser;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

public final class UniChatWorkerSettingsScreen extends UniChatPanelScreen {

    private static final Component TITLE = IdentifierUtils.gui("screen_worker_settings");
    private static final Component NAME_LABEL = IdentifierUtils.gui("screen_worker_settings.name");
    private static final Component EVENT_LABEL = IdentifierUtils.gui("screen_worker_settings.on_event");
    private static final Component CONDITIONS_LABEL = IdentifierUtils.gui("screen_worker_settings.conditions");
    private static final Component CONDITIONS_EMPTY = IdentifierUtils.gui("screen_worker_settings.conditions_empty");
    private static final Component ADD_CONDITION_LABEL = IdentifierUtils.gui("screen_worker_settings.add_condition");
    private static final Component ACTIONS_LABEL = IdentifierUtils.gui("screen_worker_settings.actions");
    private static final Component ACTIONS_EMPTY = IdentifierUtils.gui("screen_worker_settings.actions_empty");
    private static final Component ADD_ACTION_LABEL = IdentifierUtils.gui("screen_worker_settings.add_action");

    private static final ResourceLocation DELETE_ICON = IdentifierUtils.getIdentifier("icon/delete");

    private static final List<String> EVENT_TYPES = List.copyOf(UniChatEventUtils.getEventTypeMap());
    private static final List<State<String>> EVENT_STATES = EVENT_TYPES.stream().map(type -> State.literal(type, type)).toList();

    private static final int COLUMN_WIDTH = 180;
    private static final int ACTIONS_WIDTH = 200;
    private static final int VISIBLE_ROWS = 6;
    private static final int LIST_HEIGHT = CustomEntryList.heightFor(VISIBLE_ROWS);
    private static final int NAME_MAX_LENGTH = 128;
    private static final int NEW_WORKER = -1;

    private String name;
    private String eventType;
    private boolean touched;

    private CustomButton saveButton;

    private CustomEntryList conditionsList;
    private CustomEntryList actionsList;

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
        this.touched = false;
    }

    public UniChatWorkerSettingsScreen(Screen parent, RawWorker worker, int index) {
        super(TITLE, parent);
        this.conditions = conditions(worker);
        this.commands = commands(worker);
        this.index = index;
        this.name = name(worker);
        this.eventType = eventType(worker);
        this.touched = false;
    }

    /* ====================================================================== */

    @Override
    protected void addContents(LinearLayout layout) {
        LinearLayout generalConditionsColumn = buildGeneralConditionsColumn();
        generalConditionsColumn.arrangeElements();

        int actionsChrome = font.lineHeight + SPACING * 2 + Button.DEFAULT_HEIGHT;
        int actionsListHeight = Math.max(LIST_HEIGHT, generalConditionsColumn.getHeight() - actionsChrome);

        LinearLayout columns = LinearLayout.horizontal().spacing(SPACING);
        columns.addChild(generalConditionsColumn, LayoutSettings::alignVerticallyTop);
        columns.addChild(buildActionsColumn(actionsListHeight), LayoutSettings::alignVerticallyTop);
        layout.addChild(columns);

        /* ================================================================== */

        LinearLayout footer = LinearLayout.horizontal().spacing(SPACING);
        saveButton = footer.addChild(new CustomButton(font, HALF_WIDTH, IdentifierUtils.GUI_SAVE, Variant.SUCCESS, this::apply));
        footer.addChild(new CustomButton(font, HALF_WIDTH, CommonComponents.GUI_BACK, this::cancel));
        layout.addChild(footer, (settings) -> settings.paddingTop(SPACING).alignHorizontallyCenter());
    }

    @Override
    protected void repositionElements() {
        refreshChrome();
        super.repositionElements();
    }

    /* ====================================================================== */

    private LinearLayout buildGeneralConditionsColumn() {
        LinearLayout column = LinearLayout.vertical().spacing(SPACING);

        column.addChild(new CustomEditBox(
            font,
            COLUMN_WIDTH,
            NAME_LABEL,
            name,
            this::onNameChange,
            NAME_MAX_LENGTH
        ));

        column.addChild(new CustomLabeledCycleButton<>(
            font,
            COLUMN_WIDTH,
            EVENT_LABEL,
            State.literal(eventType, eventType),
            this::onEventChange,
            EVENT_STATES
        ));

        /* ================================================================== */

        column.addChild(new StringWidget(
            COLUMN_WIDTH,
            font.lineHeight,
            CONDITIONS_LABEL,
            font
        ).alignLeft());

        conditionsList = column.addChild(new CustomEntryList(
            minecraft,
            COLUMN_WIDTH,
            LIST_HEIGHT,
            CONDITIONS_EMPTY,
            this::editCondition,
            List.of(new RowAction(IdentifierUtils.GUI_DELETE, DELETE_ICON, this::deleteCondition))
        ));

        column.addChild(new CustomButton(
            font,
            COLUMN_WIDTH,
            ADD_CONDITION_LABEL,
            this::createCondition
        ));

        return column;
    }

    private LinearLayout buildActionsColumn(int listHeight) {
        LinearLayout column = LinearLayout.vertical().spacing(SPACING);
        column.addChild(new StringWidget(
            ACTIONS_WIDTH,
            font.lineHeight,
            ACTIONS_LABEL,
            font
        ));

        actionsList = column.addChild(new CustomEntryList(
            minecraft,
            ACTIONS_WIDTH,
            listHeight,
            ACTIONS_EMPTY,
            this::editCommand,
            List.of(new RowAction(IdentifierUtils.GUI_DELETE, DELETE_ICON, this::deleteCommand))
        ));

        column.addChild(new CustomButton(
            font,
            ACTIONS_WIDTH,
            ADD_ACTION_LABEL,
            this::createCommand
        ));

        return column;
    }

    /* ====================================================================== */

    private void refreshChrome() {
        saveButton.active = touched;
        conditionsList.refresh(conditionLabels());
        actionsList.refresh(actionLabels());
    }

    private List<Component> conditionLabels() {
        return conditions.stream().map(UniChatWorkerSettingsScreen::conditionLabel).toList();
    }

    private List<Component> actionLabels() {
        return commands.stream().<Component>map(Component::literal).toList();
    }

    private boolean unknownProperty(RawCondition condition) {
        String property = condition.property();

        return property == null || UniChatEventUtils.getEventProperty(eventType, property).isEmpty();
    }

    /* ====================================================================== */

    private void onNameChange(CustomEditBox editBox, String value) {
        name = value;
        touched = true;
        refreshChrome();
    }

    private void onEventChange(CustomLabeledCycleButton<String> button, State<String> newState) {
        eventType = newState.value();
        conditions.removeIf(this::unknownProperty);
        touched = true;
        refreshChrome();
    }

    private void createCondition(CustomButton button) {
        minecraft.setScreen(new UniChatWorkerConditionScreen(this, eventType, conditions));
    }

    private void editCondition(int index) {
        minecraft.setScreen(new UniChatWorkerConditionScreen(this, eventType, conditions, index));
    }

    private void deleteCondition(int index) {
        conditions.remove(index);
        touched = true;
        refreshChrome();
    }

    private void createCommand(CustomButton button) {
        minecraft.setScreen(new UniChatWorkerCommandScreen(this, commands));
    }

    private void editCommand(int index) {
        minecraft.setScreen(new UniChatWorkerCommandScreen(this, commands, index));
    }

    private void deleteCommand(int index) {
        commands.remove(index);
        touched = true;
        refreshChrome();
    }

    /* ====================================================================== */

    private void cancel(CustomButton button) {
        onClose();
    }

    private void apply(CustomButton button) {
        RawWorker worker = new RawWorker(name, eventType, List.copyOf(conditions), new RawAction(List.copyOf(commands)));

        try {
            RawWorkerParser.parse(worker);
        } catch (WorkerValidationException e) {
            minecraft.setScreen(new UniChatWorkerValidationScreen(this, e.message()));
            return;
        }

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

    private static Component eventName(String eventType) {
        return IdentifierUtils.gui("event." + eventType.substring(eventType.indexOf(':') + 1));
    }

    private static Component conditionLabel(RawCondition condition) {
        Object value = condition.value();
        String suffix = value == null ? "" : " " + value;

        return Component.literal(condition.property() + " ").append(operatorName(condition.operator())).append(suffix);
    }

    private static Component operatorName(@Nullable String operator) {
        if (operator == null) {
            return CommonComponents.EMPTY;
        }

        return IdentifierUtils.gui("operator." + operator.toLowerCase(Locale.ROOT));
    }

}
