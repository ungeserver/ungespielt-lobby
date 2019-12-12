package net.ungespielt.lobby.spigot.feature.gadgets.pets;

import net.ungespielt.lobby.spigot.api.feature.gadgets.pets.Pet;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/**
 * The factory for the {@link PetImpl}.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
public interface PetFactory {

    /**
     * Create a new pet.
     *
     * @param player     The owner.
     * @param entityType The type of the pet.
     * @param adult      If the pet is an adult.
     * @return The pet.
     */
    Pet createPet(Player player, EntityType entityType, boolean adult);
}
