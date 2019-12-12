package net.ungespielt.lobby.spigot.feature.gadgets.pets.menu;

import de.jackwhite20.base.api.spigot.menu.impl.Menu;
import org.bukkit.entity.Player;

/**
 * The factory for the {@link PetsMenu}.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
public interface PetsMenuFactory {

    /**
     * Create a new pets menu.
     *
     * @param player The player.
     *
     * @return The menu.
     */
    Menu createPetsMenu(Player player);
}
