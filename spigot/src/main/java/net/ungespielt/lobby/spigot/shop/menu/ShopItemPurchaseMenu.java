package net.ungespielt.lobby.spigot.shop.menu;

import com.google.inject.assistedinject.Assisted;
import de.jackwhite20.base.api.spigot.menu.impl.Menu;
import de.jackwhite20.base.spigot.menu.MenuImplementation;
import net.ungespielt.lobby.spigot.api.shop.PurchaseContext;
import org.bukkit.inventory.ItemStack;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * The menu a player uses to decide if he purchases something.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class ShopItemPurchaseMenu extends MenuImplementation implements Menu {

    @Inject
    public ShopItemPurchaseMenu(@Assisted PurchaseContext purchaseContext, @Named("backItemStack") ItemStack backItemStack, @Named("purchaseItemStack") ItemStack purchaseItemStack, @Named("declineItemStack") ItemStack declineItemStack) {
        super(purchaseContext.getPlayer(), "Kauf: " + purchaseContext.getShopItem().getDescription(), 54);

        setItem(0, backItemStack, menuClickEvent -> purchaseContext.abort());
        setItem(11, purchaseItemStack, menuClickEvent -> purchaseContext.decide(true));
        setItem(15, declineItemStack, menuClickEvent -> purchaseContext.decide(false));
    }
}
