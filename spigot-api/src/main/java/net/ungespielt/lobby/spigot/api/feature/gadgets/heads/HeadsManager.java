package net.ungespielt.lobby.spigot.api.feature.gadgets.heads;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * The manager for all heads.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
public interface HeadsManager {

    /**
     * Apply the given item stack as head to the given player.
     *
     * @param player    The player.
     * @param itemStack The item stack.
     * @return the observable of the head.
     */
    Head applyHead(Player player, ItemStack itemStack);

    /**
     * Check if there is a head for a given player.
     *
     * @param player The player.
     * @return If the player has a head.
     */
    boolean hasHead(Player player);

    /**
     * Remove the head of the given player.
     *
     * @param player The player.
     * @return If the player could hat off.
     */
    boolean removeHead(Player player);
}
