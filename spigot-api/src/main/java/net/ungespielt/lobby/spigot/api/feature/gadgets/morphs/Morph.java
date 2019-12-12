package net.ungespielt.lobby.spigot.api.feature.gadgets.morphs;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
public interface Morph {

    /**
     * Initialize the morph.
     */
    void initialize();

    /**
     * Destroy the morph.
     */
    void destroy();

    /**
     * Get the type of the morph.
     *
     * @return The type of the entity the player is morphed as.
     */
    EntityType getEntityType();

    /**
     * Refresh the morph for the given player.
     *
     * @param player The player.
     */
    void refresh(Player player);

    /**
     * Get the morphed player.
     *
     * @return The player.
     */
    Player getPlayer();
}
