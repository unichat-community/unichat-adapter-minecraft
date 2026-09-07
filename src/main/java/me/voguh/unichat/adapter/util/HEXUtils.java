package me.voguh.unichat.adapter.util;

import java.util.HexFormat;

public final class HEXUtils {

    private static final HexFormat HEX_FORMAT = HexFormat.of();

    public static String encode(byte[] bytes) {
        return HEX_FORMAT.formatHex(bytes);
    }

    public static byte[] decode(String hex) {
        return HEX_FORMAT.parseHex(hex);
    }

    /* ====================================================================== */

    private HEXUtils() {
        throw new IllegalStateException("Utility class");
    }

}
