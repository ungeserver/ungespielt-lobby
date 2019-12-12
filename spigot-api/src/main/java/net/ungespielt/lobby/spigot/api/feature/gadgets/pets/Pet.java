package net.ungespielt.lobby.spigot.api.feature.gadgets.pets;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
public interface Pet {

    /**
     * Initialize the pet.
     */
    void initialize();

    /**
     * Destroy the given pet.
     */
    void destroy();

    /**
     * Get the type of the pet.
     *
     * @return The type.
     */
    EntityType getEntityType();

    /**
     * Refresh the view on the pet for the given player.
     *
     * @param player The player.
     */
    void refresh(Player player);

    /**
     * Get the owner of the pet.
     *
     * @return The player.
     */
    Player getPlayer();
}
