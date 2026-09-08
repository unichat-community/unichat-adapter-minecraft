package me.voguh.unichat.adapter.gui.component;

import me.voguh.unichat.adapter.util.IdentifierUtils;
import net.minecraft.network.chat.Component;

import java.util.Objects;

public record State<T>(Component message, T value) {

    public static <T> State<T> translatable(String key, T value) {
        return new State<>(IdentifierUtils.translatable(key), value);
    }

    public static <T> State<T> literal(String message, T value) {
        return new State<>(Component.literal(message), value);
    }

    /* ====================================================================== */

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        State<?> state = (State<?>) o;
        return Objects.equals(value, state.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

}
