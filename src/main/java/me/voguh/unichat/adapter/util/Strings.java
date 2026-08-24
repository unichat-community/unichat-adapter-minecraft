/*!******************************************************************************
 * Copyright (c) 2026 Voguh
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

package me.voguh.unichat.adapter.util;

import org.jspecify.annotations.Nullable;

public final class Strings {

    public static boolean isNullOrEmpty(@Nullable String str) {
        return str == null || str.isBlank();
    }

    /**
     * Checks that the specified string is not {@code null} or empty and
     * throws a customized {@link NullPointerException} if it is. This method
     * is designed primarily for doing parameter validation in methods and
     * constructors with multiple parameters, as demonstrated below:
     * <blockquote><pre>
     * public Foo(String bar, String baz) {
     *     this.bar = Strings.requiresNonNullOrEmpty(bar, "bar must not be null or empty");
     *     this.baz = Strings.requiresNonNullOrEmpty(baz, "baz must not be null or empty");
     * }
     * </pre></blockquote>
     *
     * @param str     the string reference to check for nullity or emptiness
     * @param message detail message to be used in the event that a {@code
     *                NullPointerException} is thrown
     * @return {@code str} if not {@code null} or empty
     * @throws NullPointerException if {@code str} is {@code null} or empty
     */
    public static String requiresNonNullOrEmpty(@Nullable String str, String message) {
        if (isNullOrEmpty(str)) {
            throw new NullPointerException(message);
        }

        return str;
    }

    /* ====================================================================== */

    private Strings() {
        throw new IllegalStateException("Utility class");
    }

}
