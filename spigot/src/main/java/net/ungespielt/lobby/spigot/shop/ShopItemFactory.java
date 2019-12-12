package net.ungespielt.lobby.spigot.shop;

import net.ungespielt.lobby.spigot.api.shop.ShopItem;

import java.util.UUID;

/**
 * The factory for the {@link ShopItem}.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
public interface ShopItemFactory {

    /**
     * Create a new shop item.
     *
     * @param uniqueId      The unique id.
     * @param description   The description.
     * @param content       The content.
     * @param price         The price.
     * @return The sop item.
     */
    ShopItem createShopItem(UUID uniqueId, String description, Object content, int price);
}
