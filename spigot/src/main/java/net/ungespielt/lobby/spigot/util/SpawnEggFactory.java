package net.ungespielt.lobby.spigot.util;

import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.SpawnEgg;

/**
 * The factory for all spawn eggs.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class SpawnEggFactory {

    /**
     * Create a spawn egg item stack.
     *
     * @param entityType The type of the entity.
     * @return The item stack.
     */
    public ItemStack getItemStack(EntityType entityType) {
        return new SpawnEgg(entityType).toItemStack(1);
    }
}
