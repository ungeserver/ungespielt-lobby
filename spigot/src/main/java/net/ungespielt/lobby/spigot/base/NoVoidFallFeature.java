package net.ungespielt.lobby.spigot.base;

import de.jackwhite20.base.api.spigot.feature.Feature;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * The feature that parts the player back if he is falling into the void.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class NoVoidFallFeature extends Feature implements Listener {

    public NoVoidFallFeature(String name) {
        super(name);
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent playerMoveEvent) {
        if (playerMoveEvent.getTo().getBlockY() < 0) {
            Player player = playerMoveEvent.getPlayer();
            player.teleport(player.getLocation().getWorld().getSpawnLocation());
        }
    }
}
