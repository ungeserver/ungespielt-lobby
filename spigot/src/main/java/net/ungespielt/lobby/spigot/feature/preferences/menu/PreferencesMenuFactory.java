package net.ungespielt.lobby.spigot.feature.preferences.menu;

import de.jackwhite20.base.api.spigot.menu.impl.Menu;
import org.bukkit.entity.Player;

/**
 * The factory for the {@link PreferencesMenu}.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
public interface PreferencesMenuFactory {

    /**
     * Create a new preferences menu.
     *
     * @param player               The player.
     * @return The menu.
     */
    Menu createPreferencesMenu(Player player);
}
