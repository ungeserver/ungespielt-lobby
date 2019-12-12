package net.ungespielt.lobby.spigot.api.event;

import net.ungespielt.lobby.spigot.api.feature.gadgets.pets.Pet;
import net.ungespielt.lobby.spigot.api.feature.gadgets.pets.PetRemoveReason;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * The event that announces that a player removes his pet.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class PlayerPetRemoveEvent extends PlayerEvent {

    /**
     * The handler list with all handlers.
     */
    private static final HandlerList HANDLER_LIST = new HandlerList();

    /**
     * The pet of the player.
     */
    private final Pet pet;

    /**
     * Why the pet gets removed.
     */
    private final PetRemoveReason reason;

    /**
     * Create a new player pet remove event.
     *  @param who The player.
     * @param pet The pet.
     * @param reason The reason.
     */
    public PlayerPetRemoveEvent(Player who, Pet pet, PetRemoveReason reason) {
        super(who);
        this.pet = pet;
        this.reason = reason;
    }

    /**
     * Get the handler list with all handlers.
     *
     * @return The handler list.
     */
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    /**
     * Get the pet of the player.
     *
     * @return The pet.
     */
    public Pet getPet() {
        return pet;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    /**
     * Get the reason why the pet gets deleted.
     *
     * @return The reason.
     */
    public PetRemoveReason getReason() {
        return reason;
    }
}
