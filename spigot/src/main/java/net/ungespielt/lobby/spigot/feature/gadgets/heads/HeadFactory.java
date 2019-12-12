package net.ungespielt.lobby.spigot.feature.gadgets.heads;

import net.ungespielt.lobby.spigot.api.feature.gadgets.heads.Head;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * The factory for the {@link HeadImpl}.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
public interface HeadFactory {

    /**
     * Create a new head.
     *
     * @param player    The player.
     * @param itemStack The item stack for the head.
     * @return The head.
     */
    Head createHead(Player player, ItemStack itemStack);
}
