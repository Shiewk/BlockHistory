package de.shiewk.blockhistory.listener;

import de.shiewk.blockhistory.BlockHistoryPlugin;
import de.shiewk.blockhistory.history.HistoryElement;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR) public void onBlockBreak(BlockBreakEvent event){
        final Block block = event.getBlock();
        addBlockHistoryEntry(HistoryElement.Type.BREAK, event.getPlayer(), block);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR) public void onBlockPlace(BlockPlaceEvent event){
        final Block block = event.getBlock();
        addBlockHistoryEntry(HistoryElement.Type.PLACE, event.getPlayer(), block);
    }

    private void addBlockHistoryEntry(HistoryElement.Type type, Player player, Block block) {
        BlockHistoryPlugin.getHistoryManager().add(new HistoryElement(
                type,
                player.getUniqueId(),
                block.getWorld().getUID(),
                block.getX(),
                block.getY(),
                block.getZ(),
                System.currentTimeMillis(),
                block.getType()
        ));
    }

}
