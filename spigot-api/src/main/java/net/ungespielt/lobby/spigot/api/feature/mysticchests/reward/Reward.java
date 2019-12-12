package net.ungespielt.lobby.spigot.api.feature.mysticchests.reward;

import org.bukkit.entity.Player;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
public interface Reward {

    /**
     * Apply the reward to the given player.
     *
     * @param player The player.
     */
    void applyTo(Player player);
}
