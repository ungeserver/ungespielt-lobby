package net.ungespielt.lobby.spigot.feature.preferences.menu.scoreboard;

import de.jackwhite20.base.api.spigot.menu.impl.Menu;
import org.bukkit.entity.Player;

/**
 * The factory for the {@link ScoreboardPreferencesMenu}.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
public interface ScoreboardPreferencesMenuFactory {

    /**
     * Create a new menu for the scoreboard preferences.
     *
     * @param player               The player.
     * @return The menu.
     */
    Menu createScoreboardPreferencesMenu(Player player);
}
