package de.shiewk.blockhistory.command;

import de.shiewk.blockhistory.BlockHistoryPlugin;
import de.shiewk.blockhistory.util.PlayerUtil;
import de.shiewk.blockhistory.util.TimeUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Entity;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

import static de.shiewk.blockhistory.BlockHistoryPlugin.*;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

public final class BlockHistoryCommand implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        World world = null;
        Integer x = null;
        Integer y = null;
        Integer z = null;

        if (sender instanceof Entity entity){
            world = entity.getWorld();
            x = entity.getLocation().getBlockX();
            y = entity.getLocation().getBlockY()-1;
            z = entity.getLocation().getBlockZ();
        }
        if (args.length >= 3){
            try {
                x = Integer.parseInt(args[0]);
                y = Integer.parseInt(args[1]);
                z = Integer.parseInt(args[2]);
            } catch (NumberFormatException e){
                sender.sendMessage(
                        CHAT_PREFIX.append(text("Invalid number")).color(FAIL_COLOR)
                );
                return true;
            }
        } else if (args.length > 0) {
            sender.sendMessage(
                    CHAT_PREFIX.append(text("Please provide 3 valid coordinates")).color(FAIL_COLOR)
            );
            return true;
        }
        if (args.length >= 4){
            final World findWorld = Bukkit.getWorld(args[3]);
            if (findWorld == null){
                sender.sendMessage(
                        CHAT_PREFIX.append(text("World '%s' does not exist".formatted(args[3]))).color(FAIL_COLOR)
                );
                return true;
            }
        }

        if (x != null && world != null){
            final UUID worldUUID = world.getUID();
            final int x_ = x;
            final int y_ = y;
            final int z_ = z;
            sender.sendMessage(CHAT_PREFIX.append(text("Searching in world %s at x=%s y=%s z=%s, please wait...".formatted(world.getName(), x_, y_, z_))));
            sender.sendMessage(Component.empty());
            long n = System.nanoTime();
            BlockHistoryPlugin.getHistoryManager().searchAsync(
                    element ->
                            element.worldUUID().equals(worldUUID) &&
                            element.x() == x_ &&
                            element.y() == y_ &&
                            element.z() == z_,
                    element -> {
                        TextComponent message = text("Block ")
                                .append(translatable(element.material()).color(SECONDARY_COLOR))
                                .append(text(" was "))
                                .append(text(element.type().name).color(SECONDARY_COLOR))
                                .append(text(" by "))
                                .append(PlayerUtil.playerName(element.playerUUID()).colorIfAbsent(SECONDARY_COLOR))
                                .append(text(" at "))
                                .append(text(TimeUtil.formatTimestamp(element.timestamp())).color(SECONDARY_COLOR));
                        if (element.additionalData() != null){
                            message = message.append(Component.text(": \"" + new String(element.additionalData()) + "\""));
                        }
                        sender.sendMessage(CHAT_PREFIX.append(message));
                    }
            ).thenAccept((count) -> {
                long time = System.nanoTime() - n;
                float timeMs = Math.round(time / 10000f) / 100f;
                sender.sendMessage(Component.empty());
                sender.sendMessage(CHAT_PREFIX.append(text("Search complete (%s ms), %s elements found.".formatted(timeMs, count))));
            });
        } else {
            sender.sendMessage(
                    CHAT_PREFIX.append(text("Please provide both coordinates and a world name")).color(FAIL_COLOR)
            );
        }
        return true;
    }

    @Override
    public @NotNull List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Entity entity){
            if (args.length < 2){
                return List.of(String.valueOf(entity.getLocation().getBlockX()));
            }
            if (args.length < 3){
                return List.of(String.valueOf(entity.getLocation().getBlockY()-1), String.valueOf(entity.getLocation().getBlockY()));
            }
            if (args.length < 4){
                return List.of(String.valueOf(entity.getLocation().getBlockZ()));
            }
            if (args.length < 5){
                return Bukkit.getWorlds().stream().map(WorldInfo::getName).toList();
            }
        }
        return List.of();
    }
}
