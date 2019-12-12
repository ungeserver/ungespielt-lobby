package net.ungespielt.lobby.spigot.feature.gadgets;

import de.jackwhite20.base.api.spigot.menu.impl.Menu;
import de.jackwhite20.base.api.spigot.util.ItemBuilder;
import de.jackwhite20.base.spigot.menu.MenuImplementation;
import net.ungespielt.lobby.spigot.data.PlayerCoinsDataManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.inject.Inject;

/**
 * The base for the overview over one gadget.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class GadgetsBaseMenu extends MenuImplementation implements Menu {

    /**
     * The view model.
     */
    private final GadgetsViewModel viewModel;

    /**
     * The item stack to jump back.
     */
    private final ItemStack backItemStack;

    /**
     * The data manager for the players coins.
     */
    private final PlayerCoinsDataManager playerCoinsDataManager;

    /**
     * The balance item stack.
     */
    private final ItemStack balanceItemStack;

    /**
     * Create a new gadgets base menu.
     *
     * @param player                 The player.
     * @param name                   The name.
     * @param size                   The size.
     * @param playerCoinsDataManager The coins of the player.
     * @param balanceItemStack       The balance item stack.
     */
    @Inject
    public GadgetsBaseMenu(Player player, String name, int size, GadgetsViewModel viewModel, ItemStack backItemStack, PlayerCoinsDataManager playerCoinsDataManager, ItemStack balanceItemStack) {
        super(player, name, size);
        this.viewModel = viewModel;
        this.backItemStack = backItemStack;
        this.playerCoinsDataManager = playerCoinsDataManager;
        this.balanceItemStack = balanceItemStack;
    }

    @Override
    public void open() {
        setupItems();
        super.open();
    }

    /**
     * Setup all items.
     */
    private void setupItems() {
        setItem(0, backItemStack, menuClickEvent -> viewModel.openGadgetsOverview(player));

        playerCoinsDataManager.getPlayerCoins(player.getUniqueId()).take(1)
                .subscribe(coins -> {
                    setItem(4, new ItemBuilder(balanceItemStack.clone()).name(balanceItemStack.getItemMeta().getDisplayName() + coins).build());
                    sendItem(4);
                });
    }
}
