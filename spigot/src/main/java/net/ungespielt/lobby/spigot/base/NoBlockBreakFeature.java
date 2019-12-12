package net.ungespielt.lobby.spigot.base;

import de.jackwhite20.base.api.spigot.feature.Feature;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class NoBlockBreakFeature extends Feature implements Listener {

    public NoBlockBreakFeature(String name) {
        super(name);
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
            event.setDropItems(false);
        }
    }
}
