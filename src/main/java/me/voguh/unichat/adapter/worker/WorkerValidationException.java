/*!******************************************************************************
 * Copyright (c) 2026 Voguh
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

package me.voguh.unichat.adapter.worker;

import me.voguh.unichat.adapter.util.IdentifierUtils;
import net.minecraft.network.chat.Component;

public final class WorkerValidationException extends RuntimeException {

    private final Object[] args;

    public Component message() {
        return IdentifierUtils.gui("validation.worker." + getMessage(), args);
    }

    /* ====================================================================== */

    public WorkerValidationException(String code, Object... args) {
        super(code);
        this.args = args;
    }

}
