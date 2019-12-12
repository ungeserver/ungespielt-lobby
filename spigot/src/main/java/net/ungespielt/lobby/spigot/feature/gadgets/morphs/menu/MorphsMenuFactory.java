package net.ungespielt.lobby.spigot.feature.gadgets.morphs.menu;

import de.jackwhite20.base.api.spigot.menu.impl.Menu;
import org.bukkit.entity.Player;

/**
 * The factory for the {@link MorphsMenu}.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
public interface MorphsMenuFactory {

    /**
     * Create a new morphs menu.
     *
     * @param player The player.
     *
     * @return The menu.
     */
    Menu createMorphsMenu(Player player);
}
