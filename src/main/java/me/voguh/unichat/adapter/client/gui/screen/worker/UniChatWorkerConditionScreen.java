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

import me.voguh.unichat.adapter.client.gui.screen.UniChatPanelScreen;
import me.voguh.unichat.adapter.event.UniChatEventUtils;
import me.voguh.unichat.adapter.util.IdentifierUtils;
import me.voguh.unichat.adapter.util.Kind;
import me.voguh.unichat.adapter.util.Property;
import me.voguh.unichat.adapter.worker.RawCondition;
import me.voguh.unichat.adapter.worker.WorkerOperator;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public final class UniChatWorkerConditionScreen extends UniChatPanelScreen {

    private static final Component TITLE = IdentifierUtils.translatable("screen_worker_condition");
    private static final Component PROPERTY_LABEL = IdentifierUtils.translatable("screen_worker_condition.property");
    private static final Component OPERATOR_LABEL = IdentifierUtils.translatable("screen_worker_condition.operator");

    private static final Pattern NUMBER_DRAFT = Pattern.compile("-?\\d*\\.?\\d*");
    private static final Pattern NUMBER = Pattern.compile("-?\\d+(\\.\\d+)?");
    private static final int VALUE_MAX_LENGTH = 256;
    private static final int NEW_CONDITION = -1;

    private Property property;
    private WorkerOperator operator;
    private String value;
    private boolean touched;

    private Button propertyButton;
    private Button operatorButton;
    private EditBox valueBox;
    private Button okButton;

    private final List<RawCondition> conditions;
    private final List<Property> properties;
    private final Runnable onSave;
    private final int index;

    /* ====================================================================== */

    public UniChatWorkerConditionScreen(Screen parent, String eventType, List<RawCondition> conditions, Runnable onSave) {
        this(parent, eventType, conditions, onSave, NEW_CONDITION, null);
    }

    public UniChatWorkerConditionScreen(Screen parent, String eventType, List<RawCondition> conditions, Runnable onSave, int index) {
        this(parent, eventType, conditions, onSave, index, conditions.get(index));
    }

    private UniChatWorkerConditionScreen(Screen parent, String eventType, List<RawCondition> conditions, Runnable onSave, int index, @Nullable RawCondition condition) {
        super(TITLE, parent);
        this.conditions = conditions;
        this.properties = UniChatEventUtils.getEventProperties(eventType);
        this.onSave = onSave;
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
        propertyButton = Button.builder(PROPERTY_LABEL, this::cycleProperty).width(CONTENT_WIDTH).build();
        layout.addChild(propertyButton);

        operatorButton = Button.builder(OPERATOR_LABEL, this::cycleOperator).width(CONTENT_WIDTH).build();
        layout.addChild(operatorButton);

        /* ================================================================== */

        Component valueLabel = IdentifierUtils.translatable("screen_worker_condition.value");
        layout.addChild(new StringWidget(CONTENT_WIDTH, font.lineHeight, valueLabel, font).alignLeft());

        valueBox = new EditBox(font, 0, 0, CONTENT_WIDTH, Button.DEFAULT_HEIGHT, valueLabel);
        valueBox.setMaxLength(VALUE_MAX_LENGTH);
        valueBox.setFilter(filter(property.kind()));
        valueBox.setValue(value);
        valueBox.setResponder(this::onValueChange);
        layout.addChild(valueBox);

        /* ================================================================== */

        okButton = Button.builder(CommonComponents.GUI_OK, this::apply).width(HALF_WIDTH).build();

        LinearLayout actions = LinearLayout.horizontal().spacing(SPACING);
        actions.addChild(okButton);
        actions.addChild(Button.builder(CommonComponents.GUI_BACK, this::cancel).width(HALF_WIDTH).build());

        layout.addChild(actions, (settings) -> settings.paddingTop(SPACING));
    }

    @Override
    protected void repositionElements() {
        refreshWidgets();
        super.repositionElements();
    }

    /* ====================================================================== */

    private void refreshWidgets() {
        propertyButton.setMessage(CommonComponents.optionNameValue(PROPERTY_LABEL, Component.literal(property.name())));
        operatorButton.setMessage(CommonComponents.optionNameValue(OPERATOR_LABEL, operatorName(operator)));
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

    /* ====================================================================== */

    private void cycleProperty(Button button) {
        int delta = Screen.hasShiftDown() ? -1 : 1;
        property = properties.get(Mth.positiveModulo(properties.indexOf(property) + delta, properties.size()));
        if (!operator.isValidFor(property.kind())) {
            operator = firstOperator();
        }

        touched = true;
        refreshWidgets();
    }

    private void cycleOperator(Button button) {
        List<WorkerOperator> operators = operators();
        int delta = Screen.hasShiftDown() ? -1 : 1;
        operator = operators.get(Mth.positiveModulo(operators.indexOf(operator) + delta, operators.size()));
        touched = true;
        refreshWidgets();
    }

    private void onValueChange(String newValue) {
        value = newValue;
        touched = true;
        okButton.active = touched && isValueValid();
    }

    /* ====================================================================== */

    private void cancel(Button button) {
        onClose();
    }

    private void apply(Button button) {
        RawCondition condition = new RawCondition(property.name(), operator.name(), parseValue(property.kind(), value));
        if (index == NEW_CONDITION) {
            conditions.add(condition);
        } else {
            conditions.set(index, condition);
        }

        onSave.run();
        onClose();
    }

    /* ====================================================================== */

    private static String value(@Nullable RawCondition condition) {
        Object value = condition == null ? null : condition.value();

        return value == null ? "" : String.valueOf(value);
    }

    private static Component operatorName(WorkerOperator operator) {
        return IdentifierUtils.translatable("operator." + operator.name().toLowerCase(Locale.ROOT));
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
