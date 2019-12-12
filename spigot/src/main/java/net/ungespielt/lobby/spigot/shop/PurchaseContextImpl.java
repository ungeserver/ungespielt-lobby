package net.ungespielt.lobby.spigot.shop;

import com.google.inject.assistedinject.Assisted;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import net.ungespielt.lobby.spigot.api.shop.PurchaseContext;
import net.ungespielt.lobby.spigot.api.shop.ShopItem;
import net.ungespielt.lobby.spigot.data.PlayerCoinsDataManager;
import org.bukkit.entity.Player;

import javax.inject.Inject;

/**
 * The default {@link PurchaseContext}.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class PurchaseContextImpl implements PurchaseContext {

    /**
     * The observable of the players decision.
     */
    private final PublishSubject<Boolean> decisionSubject = PublishSubject.create();

    /**
     * The observable of the players abortion.
     */
    private final PublishSubject<Player> abortionSubject = PublishSubject.create();

    /**
     * The player who wants to purchase the item.
     */
    private final Player player;

    /**
     * The item the player wants to purchase.
     */
    private final ShopItem shopItem;

    /**
     * The data manager for the players coins.
     */
    private final PlayerCoinsDataManager playerCoinsDataManager;

    @Inject
    public PurchaseContextImpl(@Assisted Player player, @Assisted ShopItem shopItem, PlayerCoinsDataManager playerCoinsDataManager) {
        this.player = player;
        this.shopItem = shopItem;
        this.playerCoinsDataManager = playerCoinsDataManager;
    }

    @Override
    public void abort() {
        abortionSubject.onNext(player);
    }

    @Override
    public void decide(boolean success) {
        if (success) {
            playerCoinsDataManager.getPlayerCoins(player.getUniqueId()).take(1)
                    .subscribe(coins -> {
                        if (coins < shopItem.getPrice()) {
                            decisionSubject.onError(new IllegalStateException("Du hast nicht genug Geld."));
                            return;
                        }

                        decisionSubject.onNext(true);
                    });
        }

        decisionSubject.onNext(false);
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public ShopItem getShopItem() {
        return shopItem;
    }

    @Override
    public Observable<Boolean> getResult() {
        return decisionSubject;
    }

    @Override
    public Observable<Player> getAbortion() {
        return abortionSubject;
    }
}
