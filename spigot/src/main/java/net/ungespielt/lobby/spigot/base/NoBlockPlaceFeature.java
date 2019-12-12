package net.ungespielt.lobby.spigot.base;

import de.jackwhite20.base.api.spigot.feature.Feature;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class NoBlockPlaceFeature extends Feature implements Listener {

    public NoBlockPlaceFeature(String name) {
        super(name);
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            event.setBuild(false);
            event.setCancelled(true);
        }
    }
}
