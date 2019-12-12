package net.ungespielt.lobby.spigot.shop;

import net.ungespielt.lobby.spigot.api.shop.PurchaseContext;
import net.ungespielt.lobby.spigot.api.shop.ShopItem;
import org.bukkit.entity.Player;

/**
 * The factory for the {@link PurchaseContext}.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
public interface PurchaseContextFactory {

    /**
     * Create a new purchase context.
     *
     * @param player        The player.
     * @param shopItem      The shop item.
     * @return The purchase context.
     */
    PurchaseContext createPurchaseContext(Player player, ShopItem shopItem);
}
