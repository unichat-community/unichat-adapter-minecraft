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
import me.voguh.unichat.adapter.event.UniChatEventUtils;
import me.voguh.unichat.adapter.network.UniChatNetwork;
import me.voguh.unichat.adapter.network.packet.client.SaveWorkersPayload;
import me.voguh.unichat.adapter.util.IdentifierUtils;
import me.voguh.unichat.adapter.util.Strings;
import me.voguh.unichat.adapter.worker.RawAction;
import me.voguh.unichat.adapter.worker.RawCondition;
import me.voguh.unichat.adapter.worker.RawWorker;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.LayoutElement;
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
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class UniChatWorkerSettingsScreen extends UniChatPanelScreen {

    private static final Component TITLE = IdentifierUtils.translatable("screen_worker_settings");
    private static final Component GENERAL_LABEL = IdentifierUtils.translatable("screen_worker_settings.general");
    private static final Component CONDITIONS_LABEL = IdentifierUtils.translatable("screen_worker_settings.conditions");
    private static final Component ACTIONS_LABEL = IdentifierUtils.translatable("screen_worker_settings.actions");
    private static final Component CONDITIONS_EMPTY = IdentifierUtils.translatable("screen_worker_settings.conditions_empty");
    private static final Component ACTIONS_EMPTY = IdentifierUtils.translatable("screen_worker_settings.actions_empty");

    private static final Component DELETE_LABEL = IdentifierUtils.translatable("delete");
    private static final ResourceLocation DELETE_ICON = IdentifierUtils.getIdentifier("icon/delete");

    private static final List<String> EVENT_TYPES = List.copyOf(UniChatEventUtils.getEventTypeMap());

    private static final int NAME_MAX_LENGTH = 128;
    private static final int TAB_WIDTH = (CONTENT_WIDTH - SPACING * 2) / 3;
    private static final int VISIBLE_ROWS = 6;
    private static final int LIST_HEIGHT = CustomEntryList.heightFor(VISIBLE_ROWS);
    private static final int NEW_WORKER = -1;

    private String name;
    private String eventType;

    private LinearLayout generalTab;
    private LinearLayout conditionsTab;
    private LinearLayout actionsTab;
    private LinearLayout currentTab;

    private Button generalButton;
    private Button conditionsButton;
    private Button actionsButton;
    private Button saveButton;

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
        generalTab = buildGeneralTab();
        conditionsTab = buildConditionsTab();
        actionsTab = buildActionsTab();
        currentTab = generalTab;

        generalButton = Button.builder(GENERAL_LABEL, button -> selectTab(generalTab)).width(TAB_WIDTH).build();
        conditionsButton = Button.builder(CONDITIONS_LABEL, button -> selectTab(conditionsTab)).width(TAB_WIDTH).build();
        actionsButton = Button.builder(ACTIONS_LABEL, button -> selectTab(actionsTab)).width(TAB_WIDTH).build();

        LinearLayout tabs = LinearLayout.horizontal().spacing(SPACING);
        tabs.addChild(generalButton);
        tabs.addChild(conditionsButton);
        tabs.addChild(actionsButton);
        layout.addChild(tabs);

        layout.addChild(new TabSlot());

        /* ================================================================== */

        saveButton = Button.builder(IdentifierUtils.translatable("save"), this::apply).width(HALF_WIDTH).build();

        LinearLayout footer = LinearLayout.horizontal().spacing(SPACING);
        footer.addChild(saveButton);
        footer.addChild(Button.builder(CommonComponents.GUI_BACK, this::cancel).width(HALF_WIDTH).build());

        layout.addChild(footer, (settings) -> settings.paddingTop(SPACING));
    }

    @Override
    protected void repositionElements() {
        currentTab.arrangeElements();
        refreshChrome();
        super.repositionElements();
    }

    /* ====================================================================== */

    private LinearLayout buildGeneralTab() {
        LinearLayout tab = LinearLayout.vertical().spacing(SPACING);

        Component nameLabel = IdentifierUtils.translatable("screen_worker_settings.name");
        tab.addChild(new StringWidget(CONTENT_WIDTH, font.lineHeight, nameLabel, font).alignLeft());

        EditBox nameBox = new EditBox(font, 0, 0, CONTENT_WIDTH, Button.DEFAULT_HEIGHT, nameLabel);
        nameBox.setMaxLength(NAME_MAX_LENGTH);
        nameBox.setValue(name);
        nameBox.setResponder(this::onNameChange);
        tab.addChild(nameBox);

        /* ================================================================== */

        Component eventLabel = IdentifierUtils.translatable("screen_worker_settings.on_event");
        CycleButton<String> eventButton = CycleButton.<String>builder(UniChatWorkerSettingsScreen::eventName)
            .withValues(EVENT_TYPES)
            .withInitialValue(eventType)
            .create(0, 0, CONTENT_WIDTH, Button.DEFAULT_HEIGHT, eventLabel, this::onEventChange);
        tab.addChild(eventButton);

        return tab;
    }

    private LinearLayout buildConditionsTab() {
        LinearLayout tab = LinearLayout.vertical().spacing(SPACING);

        RowAction delete = new RowAction(DELETE_LABEL, DELETE_ICON, this::deleteCondition);
        conditionsList = new CustomEntryList(minecraft, CONTENT_WIDTH, LIST_HEIGHT, CONDITIONS_EMPTY, this::editCondition, List.of(delete));
        tab.addChild(conditionsList);
        tab.addChild(Button.builder(IdentifierUtils.translatable("new"), this::createCondition).width(CONTENT_WIDTH).build());

        return tab;
    }

    private LinearLayout buildActionsTab() {
        LinearLayout tab = LinearLayout.vertical().spacing(SPACING);

        RowAction delete = new RowAction(DELETE_LABEL, DELETE_ICON, this::deleteCommand);
        actionsList = new CustomEntryList(minecraft, CONTENT_WIDTH, LIST_HEIGHT, ACTIONS_EMPTY, this::editCommand, List.of(delete));
        tab.addChild(actionsList);
        tab.addChild(Button.builder(IdentifierUtils.translatable("new"), this::createCommand).width(CONTENT_WIDTH).build());

        return tab;
    }

    /* ====================================================================== */

    private void selectTab(LinearLayout tab) {
        if (tab == currentTab) {
            return;
        }

        currentTab.visitWidgets(this::removeWidget);
        currentTab = tab;
        currentTab.visitWidgets(this::addRenderableWidget);
        repositionElements();
    }

    private void refreshChrome() {
        generalButton.active = currentTab != generalTab;
        conditionsButton.active = currentTab != conditionsTab;
        actionsButton.active = currentTab != actionsTab;
        saveButton.active = !Strings.isNullOrEmpty(name);
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

    private void onNameChange(String value) {
        name = value;
        refreshChrome();
    }

    private void onEventChange(CycleButton<String> button, String newValue) {
        eventType = newValue;
        conditions.removeIf(this::unknownProperty);
        refreshChrome();
    }

    private void createCondition(Button button) {
        minecraft.setScreen(new UniChatWorkerConditionScreen(this, eventType, conditions));
    }

    private void editCondition(int index) {
        minecraft.setScreen(new UniChatWorkerConditionScreen(this, eventType, conditions, index));
    }

    private void deleteCondition(int index) {
        conditions.remove(index);
        conditionsList.refresh(conditionLabels());
    }

    private void createCommand(Button button) {
        minecraft.setScreen(new UniChatWorkerCommandScreen(this, commands));
    }

    private void editCommand(int index) {
        minecraft.setScreen(new UniChatWorkerCommandScreen(this, commands, index));
    }

    private void deleteCommand(int index) {
        commands.remove(index);
        actionsList.refresh(actionLabels());
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

    private static Component eventName(String eventType) {
        return IdentifierUtils.translatable("event." + eventType.substring(eventType.indexOf(':') + 1));
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

        return IdentifierUtils.translatable("operator." + operator.toLowerCase(Locale.ROOT));
    }

    /* ====================================================================== */

    private final class TabSlot implements LayoutElement {

        @Override
        public void setX(int x) {
            currentTab.setX(x);
        }

        @Override
        public void setY(int y) {
            currentTab.setY(y);
        }

        @Override
        public int getX() {
            return currentTab.getX();
        }

        @Override
        public int getY() {
            return currentTab.getY();
        }

        @Override
        public int getWidth() {
            return currentTab.getWidth();
        }

        @Override
        public int getHeight() {
            return currentTab.getHeight();
        }

        @Override
        public void visitWidgets(Consumer<AbstractWidget> consumer) {
            currentTab.visitWidgets(consumer);
        }

    }

}
