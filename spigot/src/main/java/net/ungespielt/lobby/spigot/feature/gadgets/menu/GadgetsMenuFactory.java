package net.ungespielt.lobby.spigot.feature.gadgets.menu;

import de.jackwhite20.base.api.spigot.menu.impl.Menu;
import org.bukkit.entity.Player;

/**
 * The factory for all gadget menus.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
public interface GadgetsMenuFactory {

    /**
     * Create a new gadgets menu.
     *
     * @param player           The player.
     * @return The menu.
     */
    Menu createGadgetMenu(Player player);
}
