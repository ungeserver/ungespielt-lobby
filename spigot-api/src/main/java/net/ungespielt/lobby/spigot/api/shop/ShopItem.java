package net.ungespielt.lobby.spigot.api.shop;

import java.util.UUID;

/**
 * Represents an item a player could buy.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
public interface ShopItem {

    /**
     * Get the content of the shop item.
     *
     * @return The content of the shop item.
     */
    <ContentType> ContentType getContent();

    /**
     * Get the description of the item.
     *
     * @return The description.
     */
    String getDescription();

    /**
     * Get the unique id.
     *
     * @return The unique id.
     */
    UUID getUniqueId();

    /**
     * Get the price of the item.
     *
     * @return The price.
     */
    int getPrice();
}
