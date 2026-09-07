package me.voguh.unichat.adapter.util;

import java.util.Base64;

public final class Base64Utils {

    private static final Base64.Encoder ENCODER = Base64.getUrlEncoder();
    private static final Base64.Decoder DECODER = Base64.getUrlDecoder();

    public static String encode(byte[] bytes) {
        return ENCODER.encodeToString(bytes);
    }

    public static byte[] decode(String base64) {
        return DECODER.decode(base64);
    }

    /* ====================================================================== */

    private Base64Utils() {
        throw new IllegalStateException("Utility class");
    }

}
