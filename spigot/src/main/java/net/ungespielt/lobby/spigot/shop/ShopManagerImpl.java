package net.ungespielt.lobby.spigot.shop;

import de.jackwhite20.base.api.spigot.menu.impl.Menu;
import net.ungespielt.lobby.spigot.api.shop.PurchaseContext;
import net.ungespielt.lobby.spigot.api.shop.ShopItem;
import net.ungespielt.lobby.spigot.api.shop.ShopManager;
import net.ungespielt.lobby.spigot.data.PlayerCoinsDataManager;
import net.ungespielt.lobby.spigot.data.PlayerInventoryDataManager;
import net.ungespielt.lobby.spigot.shop.menu.ShopItemPurchaseMenuFactory;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
@Singleton
public class ShopManagerImpl implements ShopManager {

    /**
     * The message prefix.
     */
    private final String prefix;

    /**
     * The factory for the purchase menu.
     */
    private final ShopItemPurchaseMenuFactory purchaseMenuFactory;

    /**
     * The factory for the purchase context.
     */
    private final PurchaseContextFactory purchaseContextFactory;

    /**
     * The inventory data manager.
     */
    private final PlayerInventoryDataManager playerInventoryDataManager;

    /**
     * The player coins data manager.
     */
    private final PlayerCoinsDataManager playerCoinsDataManager;

    /**
     * The logger for all shop actions.
     */
    private final Logger logger;

    @Inject
    public ShopManagerImpl(@Named("prefix") String prefix, Plugin plugin, ShopItemPurchaseMenuFactory purchaseMenuFactory, PurchaseContextFactory purchaseContextFactory, PlayerInventoryDataManager playerInventoryDataManager, PlayerCoinsDataManager playerCoinsDataManager) {
        this.prefix = prefix;
        this.purchaseMenuFactory = purchaseMenuFactory;
        this.purchaseContextFactory = purchaseContextFactory;
        this.playerInventoryDataManager = playerInventoryDataManager;
        this.playerCoinsDataManager = playerCoinsDataManager;
        this.logger = plugin.getLogger();
    }

    @Override
    public PurchaseContext openPurchaseContext(Player player, ShopItem shopItem) {
        logger.log(Level.FINE, "Launching shop flow for player " + player.getName() + " who wants to by item '" + shopItem.getDescription() + "' (" + shopItem.getPrice() + " Coins, " + shopItem.getUniqueId() + ").");

        PurchaseContext purchaseContext = purchaseContextFactory.createPurchaseContext(player, shopItem);

        Menu menu = purchaseMenuFactory.createShopItemPurchaseMenu(purchaseContext);

        purchaseContext.getResult()
                .filter(aBoolean -> aBoolean)
                .take(1)
                .subscribe(aBoolean -> {
                    playerCoinsDataManager.manipulateCoins(player.getUniqueId(), -shopItem.getPrice());
                    playerInventoryDataManager.addToInventory(player.getUniqueId(), shopItem.getUniqueId());
                    player.sendMessage(prefix + "Du hast '" + shopItem.getDescription() + ChatColor.GOLD + "' für " + shopItem.getPrice() + " gekauft.");
                }, throwable -> {
                    player.sendMessage(prefix + throwable.getMessage());
                });

        menu.open();

        return purchaseContext;
    }
}
