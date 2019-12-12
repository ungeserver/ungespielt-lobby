package net.ungespielt.lobby.spigot.feature.vanish;

import com.google.common.collect.Maps;
import de.jackwhite20.base.api.spigot.chat.ChatService;
import net.ungespielt.lobby.spigot.api.feature.FeatureViewModel;
import net.ungespielt.lobby.spigot.api.rx.CommandExecution;
import net.ungespielt.lobby.spigot.data.PlayerVanishDataManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The view model for {@link VanishController}.
 * <p>
 * TODO: Attach callback location to network.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
@Singleton
public class VanishViewModel extends FeatureViewModel {

    /**
     * The prefix for all messages.
     */
    private final String prefix;

    /**
     * The logger we are using.
     */
    private final Logger logger;

    /**
     * The locations the players vanished at.
     */
    private final Map<Player, Location> playerVanishLocations = Maps.newHashMap();

    /**
     * The player vanish data manager.
     */
    private final PlayerVanishDataManager vanishDataManager;

    /**
     * Create a new view model.
     *
     * @param prefix                       The prefix.
     * @param vanishDataManager            The player vanish data manager.
     */
    @Inject
    public VanishViewModel(@Named("prefix") String prefix, Plugin plugin, PlayerVanishDataManager vanishDataManager) {
        this.prefix = prefix;
        this.logger = plugin.getLogger();
        this.vanishDataManager = vanishDataManager;
    }

    /**
     * Handle execution of the vanish command.
     *
     * @param commandExecution The execution.
     */
    public void handleCommandVanish(CommandExecution commandExecution) {
        Player player = (Player) commandExecution.getCommandSender();

        if (!player.hasPermission("lobby.command.vanish")) {
            player.sendMessage(ChatService.NO_PERMISSIONS);
            return;
        }

        if (playerVanishLocations.containsKey(player)) {
            logger.log(Level.FINE, "The player " + player.getName() + " will unvanish.");

            unvanishPlayer(player);

            player.sendMessage(prefix + "Du wurdest wieder sichtbar gemacht und zu den Koordinaten teleportiert, an denen du warst als du dich unsichtbar gemacht hast.");

            playerVanishLocations.remove(player);
            return;
        }

        vanishPlayer(player);

        logger.log(Level.FINE, "The player " + player.getName() + " vanished.");
        player.sendMessage(prefix + "Du bist nun unsichtbar. Wenn du dich wieder sichtbar machst wirst du zu diesen Koordinaten teleportiert.");
    }

    /**
     * Vanish the given player.
     *
     * @param player The player.
     */
    private void vanishPlayer(Player player) {
        playerVanishLocations.put(player, player.getLocation());

        Bukkit.getOnlinePlayers().forEach(currentPlayer -> currentPlayer.hidePlayer(player));

        vanishDataManager.setPlayerVanishState(player, true);
    }

    /**
     * Unvanish the given player.
     *
     * @param player The player.
     */
    private void unvanishPlayer(Player player) {
        Location location = playerVanishLocations.remove(player);

        if (location != null) {
            player.teleport(location);
        }

        Bukkit.getOnlinePlayers().forEach(currentPlayer -> currentPlayer.showPlayer(player));

        vanishDataManager.setPlayerVanishState(player, false);
    }

    /**
     * Handle the join of a player.
     *
     * @param player The player.
     */
    public void handlePlayerJoin(Player player) {
        subscribe(vanishDataManager.getPlayerVanishState(player)
                .take(1)
                .filter(vanished -> vanished)
                .map(vanished -> player)
                .subscribe(this::vanishPlayer));

        for (Player currentPlayer : playerVanishLocations.keySet()) {
            player.hidePlayer(currentPlayer);
        }
    }

    /**
     * Handle the quit of a player.
     *
     * @param player The player.
     */
    public void handlePlayerQuit(Player player) {
        logger.log(Level.FINE, "The player " + player.getName() + " left the server. Removing location data...");

        playerVanishLocations.remove(player);
    }
}
