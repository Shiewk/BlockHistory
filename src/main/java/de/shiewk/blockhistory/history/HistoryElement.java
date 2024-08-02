package de.shiewk.blockhistory.history;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.io.*;
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

    public void saveTo(OutputStream out) throws IOException {
        final DataOutputStream dataOut = new DataOutputStream(out);
        dataOut.write(0); // version
        dataOut.writeInt(type.ordinal());
        dataOut.writeBoolean(playerUUID != null);
        if (playerUUID != null){
            dataOut.writeLong(playerUUID.getMostSignificantBits());
            dataOut.writeLong(playerUUID.getLeastSignificantBits());
        }
        dataOut.writeLong(worldUUID.getMostSignificantBits());
        dataOut.writeLong(worldUUID.getLeastSignificantBits());
        dataOut.writeInt(x);
        dataOut.writeInt(y);
        dataOut.writeInt(z);
        dataOut.writeLong(timestamp);
        dataOut.writeInt(material.ordinal());
    }

    public static HistoryElement load(InputStream in) throws IOException {
        final DataInputStream dataIn = new DataInputStream(in);
        int saveVersion = in.read();
        if (saveVersion == -1){
            // end of file reached
            throw new EOFException();
        }
        if (saveVersion == 0){
            final Type type = Type.values()[dataIn.readInt()];
            UUID playerUUID;
            if (dataIn.readBoolean()){
                playerUUID = new UUID(dataIn.readLong(), dataIn.readLong());
            } else {
                playerUUID = null;
            }
            final UUID worldUUID = new UUID(dataIn.readLong(), dataIn.readLong());
            final int x = dataIn.readInt();
            final int y = dataIn.readInt();
            final int z = dataIn.readInt();
            final long timestamp = dataIn.readLong();
            final Material material = Material.values()[dataIn.readInt()];
            return new HistoryElement(
                    type,
                    playerUUID,
                    worldUUID,
                    x,
                    y,
                    z,
                    timestamp,
                    material
            );
        } else {
            throw new IllegalArgumentException("Unsupported data version " + saveVersion);
        }
    }

    public enum Type {
        PLACE("PLACED"),
        BREAK("BROKEN");

        public final String name;

        Type (String name) {
            this.name = name;
        }
    }
}
