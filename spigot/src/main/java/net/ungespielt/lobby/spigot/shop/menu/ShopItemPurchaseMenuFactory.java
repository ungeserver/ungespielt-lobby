package net.ungespielt.lobby.spigot.shop.menu;

import de.jackwhite20.base.api.spigot.menu.impl.Menu;
import net.ungespielt.lobby.spigot.api.shop.PurchaseContext;

/**
 * The factory for the {@link ShopItemPurchaseMenu}.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
public interface ShopItemPurchaseMenuFactory {

    /**
     * Create a new purchase menu.
     *
     * @param purchaseContext The context of the purchase.
     * @return The menu.
     */
    Menu createShopItemPurchaseMenu(PurchaseContext purchaseContext);
}
