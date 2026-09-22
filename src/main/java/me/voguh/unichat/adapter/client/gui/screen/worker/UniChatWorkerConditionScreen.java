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
import me.voguh.unichat.adapter.client.gui.component.CustomLabeledCycleButton;
import me.voguh.unichat.adapter.client.gui.component.State;
import me.voguh.unichat.adapter.client.gui.screen.UniChatPanelScreen;
import me.voguh.unichat.adapter.event.UniChatEventUtils;
import me.voguh.unichat.adapter.util.IdentifierUtils;
import me.voguh.unichat.adapter.util.Kind;
import me.voguh.unichat.adapter.util.Property;
import me.voguh.unichat.adapter.worker.RawCondition;
import me.voguh.unichat.adapter.worker.WorkerOperator;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public final class UniChatWorkerConditionScreen extends UniChatPanelScreen {

    private static final Component TITLE = IdentifierUtils.gui("screen_worker_condition");
    private static final Component PROPERTY_LABEL = IdentifierUtils.gui("screen_worker_condition.property");
    private static final Component OPERATOR_LABEL = IdentifierUtils.gui("screen_worker_condition.operator");
    private static final Component VALUE_LABEL = IdentifierUtils.gui("screen_worker_condition.value");

    private static final Pattern NUMBER_DRAFT = Pattern.compile("-?\\d*\\.?\\d*");
    private static final Pattern NUMBER = Pattern.compile("-?\\d+(\\.\\d+)?");
    private static final int VALUE_MAX_LENGTH = 256;
    private static final int NEW_CONDITION = -1;

    private Property property;
    private WorkerOperator operator;
    private String value;
    private boolean touched;

    private CustomLabeledCycleButton<WorkerOperator> operatorButton;
    private CustomEditBox valueBox;
    private CustomButton okButton;

    private final List<RawCondition> conditions;
    private final List<Property> properties;
    private final int index;

    /* ====================================================================== */

    public UniChatWorkerConditionScreen(Screen parent, String eventType, List<RawCondition> conditions) {
        this(parent, eventType, conditions, NEW_CONDITION, null);
    }

    public UniChatWorkerConditionScreen(Screen parent, String eventType, List<RawCondition> conditions, int index) {
        this(parent, eventType, conditions, index, conditions.get(index));
    }

    private UniChatWorkerConditionScreen(Screen parent, String eventType, List<RawCondition> conditions, int index, @Nullable RawCondition condition) {
        super(TITLE, parent);
        this.conditions = conditions;
        this.properties = UniChatEventUtils.getEventProperties(eventType);
        this.index = index;
        this.property = property(condition);
        this.operator = operator(condition);
        this.value = value(condition);
        this.touched = false;
    }

    /* ====================================================================== */

    private Property property(@Nullable RawCondition condition) {
        if (condition == null) {
            return properties.getFirst();
        }

        return properties.stream().filter(entry -> entry.name().equals(condition.property())).findFirst().orElse(properties.getFirst());
    }

    private WorkerOperator operator(@Nullable RawCondition condition) {
        String raw = condition == null ? null : condition.operator();
        if (raw == null) {
            return firstOperator();
        }

        return WorkerOperator.fromString(raw).filter(entry -> entry.isValidFor(property.kind())).orElseGet(this::firstOperator);
    }

    /* ====================================================================== */

    @Override
    protected void addContents(LinearLayout layout) {
        List<State<Property>> propertyStates = properties.stream().map(UniChatWorkerConditionScreen::propertyState).toList();
        layout.addChild(new CustomLabeledCycleButton<>(
            font,
            CONTENT_WIDTH,
            PROPERTY_LABEL,
            propertyState(property),
            this::onPropertyChange,
            propertyStates
        ));

        List<State<WorkerOperator>> operatorStates = operatorStates();
        operatorButton = layout.addChild(new CustomLabeledCycleButton<>(
            font,
            CONTENT_WIDTH,
            OPERATOR_LABEL,
            operatorState(operator),
            this::onOperatorChange,
            operatorStates
        ));

        /* ================================================================== */

        valueBox = new CustomEditBox(
            font,
            CONTENT_WIDTH,
            VALUE_LABEL,
            value,
            this::onValueChange,
            VALUE_MAX_LENGTH
        );
        valueBox.setFilter(filter(property.kind()));
        layout.addChild(valueBox);

        /* ================================================================== */

        LinearLayout actions = LinearLayout.horizontal().spacing(SPACING);
        okButton = actions.addChild(new CustomButton(font, HALF_WIDTH, CommonComponents.GUI_OK, Variant.SUCCESS, this::apply));
        actions.addChild(new CustomButton(font, HALF_WIDTH, CommonComponents.GUI_BACK, this::cancel));
        layout.addChild(actions, (settings) -> settings.paddingTop(SPACING));
    }

    @Override
    protected void repositionElements() {
        refreshWidgets();
        super.repositionElements();
    }

    /* ====================================================================== */

    private void refreshWidgets() {
        valueBox.setFilter(filter(property.kind()));
        okButton.active = touched && isValueValid();
    }

    private boolean isValueValid() {
        return property.kind() != Kind.NUMBER || value.isEmpty() || NUMBER.matcher(value).matches();
    }

    private List<WorkerOperator> operators() {
        return Arrays.stream(WorkerOperator.values()).filter(entry -> entry.isValidFor(property.kind())).toList();
    }

    private WorkerOperator firstOperator() {
        return operators().getFirst();
    }

    private List<State<WorkerOperator>> operatorStates() {
        return operators().stream().map(UniChatWorkerConditionScreen::operatorState).toList();
    }

    /* ====================================================================== */

    private void onPropertyChange(CustomLabeledCycleButton<Property> button, State<Property> newState) {
        property = newState.value();
        if (!operator.isValidFor(property.kind())) {
            operator = firstOperator();
        }

        operatorButton.setStates(operatorState(operator), operatorStates());
        touched = true;
        refreshWidgets();
    }

    private void onOperatorChange(CustomLabeledCycleButton<WorkerOperator> button, State<WorkerOperator> newState) {
        operator = newState.value();
        touched = true;
        refreshWidgets();
    }

    private void onValueChange(CustomEditBox editBox, String newValue) {
        value = newValue;
        touched = true;
        okButton.active = touched && isValueValid();
    }

    /* ====================================================================== */

    private void cancel(CustomButton button) {
        onClose();
    }

    private void apply(CustomButton button) {
        RawCondition condition = new RawCondition(property.name(), operator.name(), parseValue(property.kind(), value));
        if (index == NEW_CONDITION) {
            conditions.add(condition);
        } else {
            conditions.set(index, condition);
        }

        onClose();
    }

    /* ====================================================================== */

    private static String value(@Nullable RawCondition condition) {
        Object value = condition == null ? null : condition.value();

        return value == null ? "" : String.valueOf(value);
    }

    private static Component operatorName(WorkerOperator operator) {
        return IdentifierUtils.gui("operator." + operator.name().toLowerCase(Locale.ROOT));
    }

    private static State<Property> propertyState(Property property) {
        return new State<>(Component.literal(property.name()), property);
    }

    private static State<WorkerOperator> operatorState(WorkerOperator operator) {
        return new State<>(operatorName(operator), operator);
    }

    private static Predicate<String> filter(Kind kind) {
        return switch (kind) {
            case NUMBER -> text -> NUMBER_DRAFT.matcher(text).matches();
            case STRING, BOOLEAN -> text -> true;
        };
    }

    private static @Nullable Object parseValue(Kind kind, String value) {
        if (value.isEmpty()) {
            return null;
        }

        return switch (kind) {
            case NUMBER -> Double.valueOf(value);
            case BOOLEAN -> Boolean.valueOf(value);
            case STRING -> value;
        };
    }

}
