package me.voguh.unichat.adapter.util;

import me.voguh.unichat.adapter.UniChatAdapter;
import net.minecraft.resources.Identifier;

public class IdentifierUtils {

    public static Identifier getIdentifier(String path) {
        return Identifier.fromNamespaceAndPath(UniChatAdapter.MODID, path);
    }

    /* ====================================================================== */

    private IdentifierUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

}
