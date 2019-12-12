package net.ungespielt.lobby.spigot.feature.gadgets.heads.menu;

import de.jackwhite20.base.api.spigot.menu.impl.Menu;
import org.bukkit.entity.Player;

/**
 * The factory for the {@link HeadsMenu}.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
public interface HeadsMenuFactory {

    /**
     * Create a new heads menu.
     *
     * @param player The player.
     *
     * @return The heads menu.
     */
    Menu createHeadsMenu(Player player);
}
