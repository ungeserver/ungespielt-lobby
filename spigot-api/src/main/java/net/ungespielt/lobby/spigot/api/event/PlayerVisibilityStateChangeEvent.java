package net.ungespielt.lobby.spigot.api.event;

import net.ungespielt.lobby.shared.PlayerVisibilityState;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class PlayerVisibilityStateChangeEvent extends PlayerEvent implements Cancellable {

    /**
     * The handler list for all handlers.
     */
    private static final HandlerList HANDLER_LIST = new HandlerList();

    /**
     * The old visibility state.
     */
    private final PlayerVisibilityState oldVisibilityState;

    /**
     * The players new visibility state.
     */
    private final PlayerVisibilityState newVisibilityState;

    /**
     * If the event is cancelled.
     */
    private boolean cancelled;

    /**
     * Create a new player visibility change event.
     *
     * @param who                The player.
     * @param oldVisibilityState The old state.
     * @param newVisibilityState The new state.
     */
    public PlayerVisibilityStateChangeEvent(Player who, PlayerVisibilityState oldVisibilityState, PlayerVisibilityState newVisibilityState) {
        super(who);
        this.oldVisibilityState = oldVisibilityState;
        this.newVisibilityState = newVisibilityState;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    public PlayerVisibilityState getNewVisibilityState() {
        return newVisibilityState;
    }

    public PlayerVisibilityState getOldVisibilityState() {
        return oldVisibilityState;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
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
