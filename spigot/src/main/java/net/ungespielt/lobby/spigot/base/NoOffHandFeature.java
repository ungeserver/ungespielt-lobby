package net.ungespielt.lobby.spigot.base;

import de.jackwhite20.base.api.spigot.feature.Feature;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class NoOffHandFeature extends Feature implements Listener {

    public NoOffHandFeature(String name) {
        super(name);
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    @EventHandler
    public void onPlayerSwitchHandEvent(PlayerSwapHandItemsEvent playerSwapHandItemsEvent) {
        playerSwapHandItemsEvent.setCancelled(true);
    }
}
