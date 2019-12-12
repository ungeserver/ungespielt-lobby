package net.ungespielt.lobby.spigot.util;

import de.jackwhite20.base.api.spigot.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class SkullFactory {

    /**
     * Get the skull of the given player.
     *
     * @param playerName The name of the player.
     * @return The skull of the player.
     */
    public ItemStack createSkull(String playerName) {
        ItemStack itemStack = new ItemBuilder(Material.SKULL_ITEM).durability((short) 3).name("§6Kopf von §9" + playerName).build();
        SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
        skullMeta.setDisplayName("§6Kopf von §9" + playerName);
        skullMeta.setOwner(playerName);
        itemStack.setItemMeta(skullMeta);
        return itemStack;
    }
}
