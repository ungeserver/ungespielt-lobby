package net.ungespielt.lobby.spigot.api.feature.gadgets.heads;

import org.bukkit.inventory.ItemStack;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
public interface Head {

    /**
     * Lets the player hat on.
     */
    void hatOn();

    /**
     * Let the player hat off.
     */
    void hatOff();

    /**
     * Get the item stack of the head.
     *
     * @return The item stack.
     */
    ItemStack getHead();

    /**
     * Get the dude the head belongs to.
     *
     * @return The owner.
     */
    String getOwner();
}
