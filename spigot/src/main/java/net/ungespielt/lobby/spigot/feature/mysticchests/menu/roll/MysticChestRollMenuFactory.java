package net.ungespielt.lobby.spigot.feature.mysticchests.menu.roll;

import de.jackwhite20.base.api.spigot.menu.impl.Menu;
import org.bukkit.entity.Player;

/**
 * The factory for the {@link MysticChestRollMenu}.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
public interface MysticChestRollMenuFactory {

    /**
     * Create a new mystic chest roll menu.
     *
     * @param player                The player.
     * @return The menu.
     */
    Menu createMysticChestRollMenu(Player player);
}
