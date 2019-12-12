package net.ungespielt.lobby.spigot.api.event;

import net.ungespielt.lobby.spigot.api.feature.gadgets.pets.Pet;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * The event that announces that a player creates a new pet.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class PlayerPetCreateEvent extends PlayerEvent implements Cancellable {

    /**
     * The handler list with all handlers.
     */
    private static final HandlerList HANDLER_LIST = new HandlerList();

    /**
     * The pet of the player.
     */
    private final Pet pet;

    /**
     * If the event is cancelled.
     */
    private boolean cancelled;

    /**
     * Create a new player pet create event.
     *
     * @param who The player.
     * @param pet The pet.
     */
    public PlayerPetCreateEvent(Player who, Pet pet) {
        super(who);
        this.pet = pet;
    }

    /**
     * Get the handler list with all handlers.
     *
     * @return The handler list.
     */
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public HandlerList getHandlers() {
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
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }
}
