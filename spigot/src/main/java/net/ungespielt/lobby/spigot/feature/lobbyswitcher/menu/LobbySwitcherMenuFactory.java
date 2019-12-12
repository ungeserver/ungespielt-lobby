package net.ungespielt.lobby.spigot.feature.lobbyswitcher.menu;

import de.jackwhite20.base.api.spigot.menu.impl.Menu;
import org.bukkit.entity.Player;

/**
 * The factory for the {@link LobbySwitcherMenu}.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
public interface LobbySwitcherMenuFactory {

    /**
     * Create a new lobby switcher menu.
     *
     * @param player The player.
     * @return The menu.
     */
    Menu createLobbySwitcherMenu(Player player);
}
