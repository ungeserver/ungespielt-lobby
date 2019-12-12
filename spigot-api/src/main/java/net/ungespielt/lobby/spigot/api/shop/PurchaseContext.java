package net.ungespielt.lobby.spigot.api.shop;

import io.reactivex.Observable;
import org.bukkit.entity.Player;

/**
 * The context of a purchase.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
public interface PurchaseContext {

    /**
     * Abort the purchase.
     */
    void abort();

    /**
     * The player decided!
     *
     * @param success The success state.
     */
    void decide(boolean success);

    /**
     * Get the purchasing player.
     *
     * @return The player.
     */
    Player getPlayer();

    /**
     * Get the shop item to purchase.
     *
     * @return The shop item.
     */
    ShopItem getShopItem();

    /**
     * Get the observable of the result of the purchase.
     *
     * @return The observable.
     */
    Observable<Boolean> getResult();

    /**
     * Get the observable of a possible abortion.
     *
     * @return The observable.
     */
    Observable<Player> getAbortion();
}
