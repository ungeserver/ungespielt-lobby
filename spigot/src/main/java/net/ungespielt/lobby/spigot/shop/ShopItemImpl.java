package net.ungespielt.lobby.spigot.shop;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import net.ungespielt.lobby.spigot.api.shop.ShopItem;

import java.util.UUID;

/**
 * Represents an item a player could buy.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class ShopItemImpl implements ShopItem {

    /**
     * The unique id of the item.
     */
    private final UUID uniqueId;

    /**
     * The description of the item.
     */
    private final String description;

    /**
     * What can you offer us?
     */
    private final Object content;

    /**
     * How much is the item.
     */
    private final int price;

    /**
     * Create a new Shop item.
     *
     * @param uniqueId    The uniqueId.
     * @param description The description of the item.
     * @param content     The content.
     * @param price       The price.
     */
    @AssistedInject
    public ShopItemImpl(@Assisted UUID uniqueId, @Assisted String description, @Assisted Object content, @Assisted int price) {
        this.uniqueId = uniqueId;
        this.description = description;
        this.content = content;
        this.price = price;
    }

    @Override
    public <ContentType> ContentType getContent() {
        return (ContentType) content;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public UUID getUniqueId() {
        return uniqueId;
    }

    @Override
    public int getPrice() {
        return price;
    }
}
