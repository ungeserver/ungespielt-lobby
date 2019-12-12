package net.ungespielt.lobby.spigot.api.feature.gadgets.morphs;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.Set;

/**
 * The manager for all morphs.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
public interface MorphsManager {

    /**
     * Morph a player and get the result or an error in an observable.
     *
     * @param player     The player.
     * @param entityType The type of the morph.
     * @return The observable of the resulting morph.
     */
    Morph morphPlayer(Player player, EntityType entityType);

    /**
     * Check if the given player is currently morphed.
     *
     * @param player The player.
     * @return If the player is morphed.
     */
    boolean isMorphed(Player player);

    /**
     * Unmorph the given player if he is currently morphed.
     *
     * @param player The player.
     *
     * @return If the player was unmorphed.
     */
    boolean unmorphPlayer(Player player);

    /**
     * Get all current active morphs.
     *
     * @return The morphs.
     */
    Set<Morph> getMorphs();

    /**
     * Refresh the view on the given player.
     *
     * @param player The player.
     */
    void refreshMorphs(Player player);
}
