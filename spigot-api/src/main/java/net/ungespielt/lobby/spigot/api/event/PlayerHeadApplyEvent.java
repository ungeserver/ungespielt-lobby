package net.ungespielt.lobby.spigot.api.event;

import net.ungespielt.lobby.spigot.api.feature.gadgets.heads.Head;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * The event that announces that a player applies a head to himself.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class PlayerHeadApplyEvent extends PlayerEvent implements Cancellable {

    /**
     * The handler list for all event handlers.
     */
    private static final HandlerList HANDLER_LIST = new HandlerList();

    /**
     * The head the player wants to apply.
     */
    private final Head head;

    /**
     * If the event should be cancelled.
     */
    private boolean cancelled;

    /**
     * Create a new player head apply event.
     *
     * @param player The player.
     * @param head   The head.
     */
    public PlayerHeadApplyEvent(Player player, Head head) {
        super(player);
        this.head = head;
    }

    /**
     * Get a list of all handlers.
     *
     * @return The handler list.
     */
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    /**
     * Get the head the player wants to apply.
     *
     * @return The head.
     */
    public Head getHead() {
        return head;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
