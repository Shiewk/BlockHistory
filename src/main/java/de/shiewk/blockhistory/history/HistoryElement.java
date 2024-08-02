package de.shiewk.blockhistory.history;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public record HistoryElement(
        @NotNull Type type,
        UUID playerUUID,
        @NotNull UUID worldUUID,
        int x,
        int y,
        int z,
        long timestamp,
        @NotNull Material material
) {

    public enum Type {
        PLACE("PLACED"),
        BREAK("BROKEN");

        public final String name;

        Type (String name) {
            this.name = name;
        }
    }
}
