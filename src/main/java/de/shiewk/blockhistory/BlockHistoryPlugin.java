package de.shiewk.blockhistory;

import de.shiewk.blockhistory.history.HistoryManager;
import de.shiewk.blockhistory.listener.BlockListener;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class BlockHistoryPlugin extends JavaPlugin {

    private static HistoryManager manager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        manager = new HistoryManager();
        registerEvents(new BlockListener());
    }


    public static HistoryManager getHistoryManager() {
        return manager;
    }

    private void registerEvents(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
