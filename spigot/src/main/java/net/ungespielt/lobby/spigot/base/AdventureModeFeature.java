package net.ungespielt.lobby.spigot.base;

import de.jackwhite20.base.api.spigot.feature.Feature;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class AdventureModeFeature extends Feature implements Listener {

    public AdventureModeFeature(String name) {
        super(name);
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.setGameMode(GameMode.ADVENTURE);
    }
}
