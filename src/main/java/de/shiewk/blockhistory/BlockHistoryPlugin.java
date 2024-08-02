package de.shiewk.blockhistory;

import de.shiewk.blockhistory.command.BlockHistoryCommand;
import de.shiewk.blockhistory.history.HistoryManager;
import de.shiewk.blockhistory.listener.BlockListener;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class BlockHistoryPlugin extends JavaPlugin {

    private static BlockHistoryPlugin instance;
    private static ComponentLogger logger;
    private static HistoryManager manager;

    public static final TextColor
            PRIMARY_COLOR = TextColor.color(0xB4FF),
            SECONDARY_COLOR = TextColor.color(0xFF00),
            FAIL_COLOR = TextColor.color(0xCF0000);
    public static final Component CHAT_PREFIX = Component.text("BlockHistory \u00BB ").color(PRIMARY_COLOR);

    public static void logThrowable(String label, Throwable e) {
        final ComponentLogger LOGGER = logger();
        LOGGER.warn(label);
        LOGGER.warn(String.valueOf(e));
        for (StackTraceElement element : e.getStackTrace()) LOGGER.warn(String.valueOf(element));
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        logger = (instance = this).getComponentLogger();
        manager = new HistoryManager();

        registerEvents(new BlockListener());

        registerCommand("blockhistory", new BlockHistoryCommand());
    }

    private void registerCommand(@NotNull String label, @NotNull TabExecutor executor) {
        final PluginCommand command = getCommand(label);
        if (command == null){
            logger().warn("Could not register command {}: Command not found", label);
            return;
        }
        command.setExecutor(executor);
        command.setTabCompleter(executor);
    }


    public static HistoryManager getHistoryManager() {
        return manager;
    }

    public static ComponentLogger logger(){
        return logger;
    }

    public static BlockHistoryPlugin getInstance() {
        return instance;
    }

    private void registerEvents(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
