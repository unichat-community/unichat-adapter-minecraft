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

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

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

    /**
     * Normalizes a URL to ensure it uses the HTTPS scheme.
     * <ul>
     *     <li>If the URL starts with "//", it prepends "https:".</li>
     *     <li>If it starts with "http://", it replaces it with "https://".</li>
     *     <li>If it doesn't start with "https://", it prepends "https://".</li>
     * </ul>
     *
     * @param url the URL to normalize
     * @return the normalized URL with HTTPS scheme
     */
    public static String normalizeUrl(String url) {
        if (url.startsWith("//")) {
            return "https:" + url;
        }

        if (url.startsWith("http://")) {
            return url.replaceFirst("^http://", "https://");
        }

        if (!url.startsWith("https://")) {
            String[] parts = url.split("://");
            return "https://" + (parts.length > 1 ? parts[1] : parts[0]);
        }

        return url;
    }

    /**
     * Computes the SHA-1 hash of the given string and returns it as a byte array.
     *
     * @param str the input string to hash
     * @return the SHA-1 hash of the input string as a byte array
     */
    public static byte[] sha1(String str) {
        try {
            return MessageDigest.getInstance("SHA-1").digest(str.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-1 algorithm not available", e);
        }
    }

    public static Map<String, String> parseQueryString(String query) {
        Map<String, String> params = new HashMap<>();
        for (String par : query.split("&")) {
            int i = par.indexOf('=');
            String key = i < 0 ? par : par.substring(0, i);
            String value = i < 0 ? "" : par.substring(i + 1);
            params.put(URLDecoder.decode(key, StandardCharsets.UTF_8), URLDecoder.decode(value, StandardCharsets.UTF_8));
        }

        return params;
    }

    /* ====================================================================== */

    private Strings() {
        throw new UnsupportedOperationException("Utility class");
    }

}
