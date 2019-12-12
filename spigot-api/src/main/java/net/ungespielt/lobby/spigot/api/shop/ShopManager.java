package net.ungespielt.lobby.spigot.api.shop;

import org.bukkit.entity.Player;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
public interface ShopManager {

    /**
     * Open the purchase context for the given shop item.
     *
     * @param shopItem The shop item.
     * @return The context of the purchase.
     */
    PurchaseContext openPurchaseContext(Player player, ShopItem shopItem);
}
