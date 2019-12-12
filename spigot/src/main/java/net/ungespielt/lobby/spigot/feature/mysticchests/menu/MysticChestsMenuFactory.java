package net.ungespielt.lobby.spigot.feature.mysticchests.menu;

import de.jackwhite20.base.api.spigot.menu.impl.Menu;
import org.bukkit.entity.Player;

/**
 * The factory for the {@link MysticChestsMenu}.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
public interface MysticChestsMenuFactory {

    /**
     * Create a new mystic chests menu for the given player.
     *
     * @param player                The player.
     * @return The mystic chests menu.
     */
    Menu createMysticChestsMenu(Player player);
}
