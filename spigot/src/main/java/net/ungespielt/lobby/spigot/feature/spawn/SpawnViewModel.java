package net.ungespielt.lobby.spigot.feature.spawn;

import net.ungespielt.lobby.spigot.api.feature.FeatureViewModel;
import org.bukkit.World;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
@Singleton
public class SpawnViewModel extends FeatureViewModel {

    /**
     * The global message prefix.
     */
    private final String prefix;

    /**
     * Create a new spawn view model.
     *
     * @param prefix The message prefix.
     */
    @Inject
    public SpawnViewModel(@Named("prefix") String prefix) {
        this.prefix = prefix;
    }

    /**
     * Handle that the given player performed the spawn command.
     *
     * @param player The player.
     */
    public void handleSpawnCommand(Player player) {
        World world = player.getWorld();

        player.teleport(world.getSpawnLocation());

        player.sendMessage(prefix + "Du wurdest zum Spawn teleportiert.");
    }
}
