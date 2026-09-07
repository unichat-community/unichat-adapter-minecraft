/*!******************************************************************************
 * Copyright (c) 2026 Voguh
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

package me.voguh.unichat.adapter.client;

import net.minecraftforge.common.ForgeConfigSpec;

public final class ClientConfig {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.BooleanValue RENDER_MESSAGES = BUILDER.define("renderMessages", true);
    private static final ForgeConfigSpec.IntValue SUPERSAMPLE = BUILDER.defineInRange("supersample", 4, 1, 4);

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    /* ====================================================================== */

    public static boolean renderMessages() {
        return RENDER_MESSAGES.get();
    }

    public static int supersample() {
        return SUPERSAMPLE.get();
    }

    public static void updateSettings(boolean renderMessages, int supersample) {
        RENDER_MESSAGES.set(renderMessages);
        SUPERSAMPLE.set(supersample);
    }

}
