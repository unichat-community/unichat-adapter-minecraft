package me.voguh.unichat.adapter.util;

import me.voguh.unichat.adapter.UniChatAdapter;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public final class IdentifierUtils {

    public static ResourceLocation getIdentifier(String path) {
        return ResourceLocation.fromNamespaceAndPath(UniChatAdapter.MODID, path);
    }

    public static Component translatable(String path) {
        return Component.translatable("gui." + UniChatAdapter.MODID + "." + path);
    }

    /* ====================================================================== */

    private IdentifierUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

}
