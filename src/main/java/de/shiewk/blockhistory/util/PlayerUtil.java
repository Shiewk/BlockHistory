package de.shiewk.blockhistory.util;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

import static net.kyori.adventure.text.Component.text;

public class PlayerUtil {

    public static String offlinePlayerName(UUID uuid){
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        return player.getName() == null ? uuid.toString() : player.getName();
    }

    public static Component playerName(UUID uuid) {
        if (uuid == null) return text("Unknown Player");
        Player player;
        if ((player = Bukkit.getPlayer(uuid)) != null){
            return player.displayName();
        } else {
            return text(offlinePlayerName(uuid));
        }
    }
}
