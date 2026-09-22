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
import me.voguh.unichat.adapter.worker.RawCondition;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Locale;

public final class UniChatWorkerConditionsScreen extends UniChatPanelScreen {

    private static final Component TITLE = IdentifierUtils.translatable("screen_worker_conditions");
    private static final Component EMPTY_MESSAGE = IdentifierUtils.translatable("screen_worker_conditions.empty");

    private static final Component DELETE_LABEL = IdentifierUtils.translatable("delete");
    private static final ResourceLocation DELETE_ICON = IdentifierUtils.getIdentifier("icon/delete");

    private static final int VISIBLE_ROWS = 6;
    private static final int LIST_HEIGHT = CustomEntryList.heightFor(VISIBLE_ROWS);

    private final List<RawCondition> conditions;
    private final String eventType;

    private CustomEntryList list;

    /* ====================================================================== */

    public UniChatWorkerConditionsScreen(Screen parent, String eventType, List<RawCondition> conditions) {
        super(TITLE, parent);
        this.conditions = conditions;
        this.eventType = eventType;
    }

    /* ====================================================================== */

    @Override
    protected void addContents(LinearLayout layout) {
        RowAction delete = new RowAction(DELETE_LABEL, DELETE_ICON, this::deleteCondition);

        list = new CustomEntryList(minecraft, CONTENT_WIDTH, LIST_HEIGHT, EMPTY_MESSAGE, this::editCondition, List.of(delete));
        layout.addChild(list);

        /* ================================================================== */

        LinearLayout actions = LinearLayout.horizontal().spacing(SPACING);
        actions.addChild(Button.builder(IdentifierUtils.translatable("new"), this::createCondition).width(HALF_WIDTH).build());
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
        return conditions.stream().map(UniChatWorkerConditionsScreen::label).toList();
    }

    private static Component label(RawCondition condition) {
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

    private void cancel(Button button) {
        onClose();
    }

    private void createCondition(Button button) {
        minecraft.setScreen(new UniChatWorkerConditionScreen(this, eventType, conditions));
    }

    private void editCondition(int index) {
        minecraft.setScreen(new UniChatWorkerConditionScreen(this, eventType, conditions, index));
    }

    private void deleteCondition(int index) {
        conditions.remove(index);
        list.refresh(labels());
    }

}
