package net.ungespielt.lobby.spigot.feature.gadgets.pets;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import net.ungespielt.lobby.spigot.api.event.PlayerPetCreateEvent;
import net.ungespielt.lobby.spigot.api.event.PlayerPetRemoveEvent;
import net.ungespielt.lobby.spigot.api.feature.gadgets.pets.Pet;
import net.ungespielt.lobby.spigot.api.feature.gadgets.pets.PetRemoveReason;
import net.ungespielt.lobby.spigot.api.feature.gadgets.pets.PetsManager;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;
import java.util.Set;

/**
 * The default implementation of the {@link PetsManager}.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
@Singleton
public class PetsManagerImpl implements PetsManager {

    /**
     * The current known player pets.
     */
    private final Map<Player, Pet> playerPets = Maps.newHashMap();

    /**
     * The pet factory.
     */
    private final PetFactory petFactory;

    /**
     * The bukkit plugin manager.
     */
    private final PluginManager pluginManager;

    @Inject
    public PetsManagerImpl(PetFactory petFactory, PluginManager pluginManager) {
        this.petFactory = petFactory;
        this.pluginManager = pluginManager;
    }

    /**
     * Create a new pet.
     *
     * @param player     The player.
     * @param entityType The type of the pet.
     * @param adult      If the pet should be an adult.
     * @return The observable of the pet.
     */
    @Override
    public Pet createPet(Player player, EntityType entityType, boolean adult) {
        Pet pet = petFactory.createPet(player, entityType, adult);

        PlayerPetCreateEvent playerPetCreateEvent = new PlayerPetCreateEvent(player, pet);
        pluginManager.callEvent(playerPetCreateEvent);

        if (playerPetCreateEvent.isCancelled()) {
            return null;
        }

        playerPets.put(player, pet);

        pet.initialize();
        return pet;
    }

    @Override
    public boolean hasPet(Player player) {
        return playerPets.containsKey(player);
    }

    @Override
    public boolean removePet(Player player) {
        return removePet(player, PetRemoveReason.PURGE);
    }

    @Override
    public Set<Pet> getCurrentPets() {
        return ImmutableSet.copyOf(playerPets.values());
    }

    @Override
    public void refreshPets(Player player) {
        getCurrentPets().forEach(pet -> {
            if (pet.getPlayer() == player) {
                return;
            }

            pet.refresh(player);
        });
    }

    @Override
    public boolean removePet(Player player, PetRemoveReason reason) {
        Pet pet = playerPets.remove(player);

        if (pet != null) {
            PlayerPetRemoveEvent playerPetRemoveEvent = new PlayerPetRemoveEvent(player, pet, reason);
            pluginManager.callEvent(playerPetRemoveEvent);

            pet.destroy();
        }

        return pet != null;
    }
}
