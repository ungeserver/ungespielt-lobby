package net.ungespielt.lobby.spigot.api.event;

import net.ungespielt.lobby.spigot.api.feature.gadgets.morphs.Morph;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * The event that announces that a players removes his morph.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class PlayerUnmorphEvent extends PlayerEvent {

    /**
     * The handler list with all event handlers.
     */
    private static final HandlerList HANDLER_LIST = new HandlerList();

    /**
     * The morph of the player.
     */
    private final Morph morph;

    /**
     * Create a new player unmorph event.
     *
     * @param who   The player.
     * @param morph The morph of the player.
     */
    public PlayerUnmorphEvent(Player who, Morph morph) {
        super(who);
        this.morph = morph;
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
     * Get the morph of the player.
     *
     * @return The morph.
     */
    public Morph getMorph() {
        return morph;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
