package me.voguh.unichat.adapter.server;

import net.minecraft.server.MinecraftServer;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public final class MinecraftServerHolder {

    private static volatile @Nullable MinecraftServer server;

    public static MinecraftServer getInstance() {
        MinecraftServer sv = server;
        if (sv == null) {
            throw new IllegalStateException("Server is not set");
        }

        return sv;
    }

    public static void execute(Consumer<MinecraftServer> consumer) {
        MinecraftServer sv = getInstance();
        sv.execute(() -> consumer.accept(sv));
    }

    static void set(@Nullable MinecraftServer server) {
        MinecraftServerHolder.server = server;
    }

    /* ====================================================================== */

    private MinecraftServerHolder() {
        throw new UnsupportedOperationException("Utility class");
    }

}
