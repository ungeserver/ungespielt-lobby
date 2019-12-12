package net.ungespielt.lobby.spigot.feature.preferences.menu.visibility;

import de.jackwhite20.base.api.spigot.menu.impl.Menu;
import org.bukkit.entity.Player;

/**
 * The factory for the {@link VisibilityMenu}.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
public interface VisibilityMenuFactory {

    /**
     * Create a new player visibility menu.
     *
     * @param player               The player.
     * @return The menu.
     */
    Menu createVisibilityMenu(Player player);
}
