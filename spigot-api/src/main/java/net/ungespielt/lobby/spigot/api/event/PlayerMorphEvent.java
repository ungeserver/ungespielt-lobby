package net.ungespielt.lobby.spigot.api.event;

import net.ungespielt.lobby.spigot.api.feature.gadgets.morphs.Morph;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * The event that announces that a player is morphing himself.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class PlayerMorphEvent extends PlayerEvent implements Cancellable {

    /**
     * The handler list with all handlers.
     */
    private static final HandlerList HANDLER_LIST = new HandlerList();

    /**
     * The morph of the player.
     */
    private final Morph morph;

    /**
     * If the event is cancelled.
     */
    private boolean cancelled;

    /**
     * Create a new player morph event.
     *
     * @param who   The player who morphed.
     * @param morph The morph.
     */
    public PlayerMorphEvent(Player who, Morph morph) {
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

    @Override
    public HandlerList getHandlers() {
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
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }
}
