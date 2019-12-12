package net.ungespielt.lobby.spigot.api.feature.gadgets.pets;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.Set;

/**
 * The manager for all pets.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
public interface PetsManager {

    /**
     * Create a new pet.
     *
     * @param player     The player.
     * @param entityType The type of the pet.
     * @param adult      If the pet should be an adult.
     * @return The observable of the pet.
     */
    Pet createPet(Player player, EntityType entityType, boolean adult);

    /**
     * Check if the given player has a pet.
     *
     * @param player The player.
     * @return If the player has pet.
     */
    boolean hasPet(Player player);

    /**
     * Remove the pet of the given player.
     *
     * @param player The player.
     * @return If the pet could be removed.
     */
    boolean removePet(Player player);

    /**
     * Remove the players pet with the given reason.
     *
     * @param player The player.
     * @param reason The reason.
     */
    boolean removePet(Player player, PetRemoveReason reason);

    /**
     * Get all current active pets.
     *
     * @return The pets.
     */
    Set<Pet> getCurrentPets();

    /**
     * Refresh all pet views for the given player.
     *
     * @param player The player.
     */
    void refreshPets(Player player);
}
