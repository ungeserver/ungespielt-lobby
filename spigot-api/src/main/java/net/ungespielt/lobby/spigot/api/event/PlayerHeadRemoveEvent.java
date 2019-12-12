package net.ungespielt.lobby.spigot.api.event;

import net.ungespielt.lobby.spigot.api.feature.gadgets.heads.Head;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * The event that announces that a players head gets removed.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class PlayerHeadRemoveEvent extends PlayerEvent {

    /**
     * The handler list with all event handlers.
     */
    private static final HandlerList HANDLER_LIST = new HandlerList();

    /**
     * The head the player wants to hat off.
     */
    private final Head head;

    /**
     * Create a new player head remove event.
     *
     * @param player The player.
     * @param head   The head.
     */
    public PlayerHeadRemoveEvent(Player player, Head head) {
        super(player);
        this.head = head;
    }

    /**
     * Get the handler list.
     *
     * @return The handler list with all handlers.
     */
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    /**
     * Get the head of the player.
     *
     * @return The head.
     */
    public Head getHead() {
        return head;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
