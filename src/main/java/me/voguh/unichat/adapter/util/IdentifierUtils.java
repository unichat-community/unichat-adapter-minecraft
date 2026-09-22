package me.voguh.unichat.adapter.util;

import me.voguh.unichat.adapter.UniChatAdapter;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public final class IdentifierUtils {

    public static final Component GUI_CLEAR = gui("clear");
    public static final Component GUI_DELETE = gui("delete");
    public static final Component GUI_DUPLICATE = gui("duplicate");
    public static final Component GUI_NEW = gui("new");
    public static final Component GUI_OPEN_FILE = gui("open_file");
    public static final Component GUI_RELOAD = gui("reload");
    public static final Component GUI_SAVE = gui("save");

    /* ====================================================================== */

    public static ResourceLocation getIdentifier(String path) {
        return ResourceLocation.fromNamespaceAndPath(UniChatAdapter.MODID, path);
    }

    public static Component gui(String path) {
        return Component.translatable("gui." + UniChatAdapter.MODID + "." + path);
    }

    /* ====================================================================== */

    private IdentifierUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

}
