package net.ungespielt.lobby.spigot.feature.gadgets.pets.menu;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import de.jackwhite20.base.api.spigot.util.ItemBuilder;
import net.ungespielt.lobby.spigot.api.shop.PurchaseContext;
import net.ungespielt.lobby.spigot.api.shop.ShopItem;
import net.ungespielt.lobby.spigot.api.shop.ShopManager;
import net.ungespielt.lobby.spigot.data.PlayerCoinsDataManager;
import net.ungespielt.lobby.spigot.data.PlayerInventoryDataManager;
import net.ungespielt.lobby.spigot.feature.gadgets.GadgetsBaseMenu;
import net.ungespielt.lobby.spigot.feature.gadgets.GadgetsViewModel;
import net.ungespielt.lobby.spigot.shop.ShopItemFactory;
import net.ungespielt.lobby.spigot.util.SpawnEggFactory;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.inject.Named;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * The pets menu.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class PetsMenu extends GadgetsBaseMenu {

    /**
     * The content of the morphs menu.
     */
    private static final Map<Integer, ShopItem> CONTENT = Maps.newHashMap();

    /**
     * The view model.
     */
    private final GadgetsViewModel viewModel;

    /**
     * The shop item factory.
     */
    private final ShopItemFactory shopItemFactory;

    /**
     * The factory for the spawn eggs.
     */
    private final SpawnEggFactory spawnEggFactory;

    /**
     * The item stack used to remove pets.
     */
    private final ItemStack removePetItemStack;

    /**
     * The data manager for the players inventory.
     */
    private final PlayerInventoryDataManager playerInventoryDataManager;

    /**
     * The shop manager.
     */
    private final ShopManager shopManager;

    /**
     * Create a new pets menu.
     *
     * @param player                     The player.
     * @param viewModel                  The view model.
     * @param backItemStack              The back item.
     * @param shopItemFactory            The shop item factory.
     * @param spawnEggFactory            The spawn egg factory.
     * @param removePetItemStack         The remove pet item stack.
     * @param playerInventoryDataManager The player inventory data manager.
     * @param shopManager                The shop manager.
     */
    @Inject
    public PetsMenu(@Assisted Player player, GadgetsViewModel viewModel, @Named("backItemStack") ItemStack backItemStack, ShopItemFactory shopItemFactory, SpawnEggFactory spawnEggFactory, @Named("removePetItemStack") ItemStack removePetItemStack, PlayerInventoryDataManager playerInventoryDataManager, ShopManager shopManager, PlayerCoinsDataManager playerCoinsDataManager, @Named("balanceItemStack") ItemStack balanceItemStack) {
        super(player, "§6Gadgets §9/ §6Haustiere", 54, viewModel, backItemStack, playerCoinsDataManager, balanceItemStack);
        this.viewModel = viewModel;
        this.shopItemFactory = shopItemFactory;
        this.spawnEggFactory = spawnEggFactory;
        this.removePetItemStack = removePetItemStack;
        this.playerInventoryDataManager = playerInventoryDataManager;
        this.shopManager = shopManager;

        checkContent();

        setupItems();
    }

    /**
     * Setup all items in the menu.
     */
    private void setupItems() {
        CONTENT.forEach((integer, shopItem) -> playerInventoryDataManager.getPlayerInventory(player.getUniqueId()).take(1)
                .subscribe(inventory -> {
                    ItemBuilder itemBuilder = new ItemBuilder(spawnEggFactory.getItemStack(shopItem.getContent())).lore("");
                    ItemStack itemStack = inventory.contains(shopItem.getUniqueId()) ? itemBuilder.glow().lore(ChatColor.GREEN + "Du besitzt dieses Item.").build() : itemBuilder.lore(ChatColor.RED + "Du besitzt dieses Item nicht. Preis: " + shopItem.getPrice()).build();

                    setItem(integer, itemStack, menuClickEvent -> handlePurchase(shopItem, viewModel));
                    sendItem(integer);
                }));

        setItem(49, removePetItemStack, menuClickEvent -> {
            viewModel.handleRemovePetClicked(player);
            close();
        });
    }

    /**
     * Handle the click on the given items.
     *
     * @param shopItem  The shop item.
     * @param viewModel The view model.
     */
    private void handlePurchase(ShopItem shopItem, GadgetsViewModel viewModel) {
        playerInventoryDataManager.getPlayerInventory(player.getUniqueId())
                .take(1)
                .subscribe(uuids -> handlePurchase(uuids, shopItem, viewModel));
    }

    /**
     * Handle the possible purchase of the given item.
     *
     * @param uuids             The players inventory.
     * @param itemStackShopItem The shop item.
     * @param viewModel         The view model.
     */
    private void handlePurchase(List<UUID> uuids, ShopItem itemStackShopItem, GadgetsViewModel viewModel) {
        if (uuids.contains(itemStackShopItem.getUniqueId())) {
            close();
            viewModel.handleCreatePetClicked(player, itemStackShopItem.getContent());
            return;
        }

        PurchaseContext purchaseContext = shopManager.openPurchaseContext(player, itemStackShopItem);
        purchaseContext.getAbortion().take(1).subscribe(player1 -> open());
        purchaseContext.getResult().take(1).subscribe(aBoolean -> {
            if (aBoolean) {
                close();
                viewModel.handleCreatePetClicked(player, itemStackShopItem.getContent());
                return;
            }
            open();
        }, throwable -> open());
    }

    /**
     * Check for the content and init it if it is not present yet.
     */
    private void checkContent() {
        if (CONTENT.size() != 0) {
            return;
        }

        CONTENT.put(19, shopItemFactory.createShopItem(UUID.fromString("8b41b015-1d68-4f2d-b874-36ac5527d51f"), ChatColor.BLUE + "Zombie", EntityType.ZOMBIE, 3000));
        CONTENT.put(20, shopItemFactory.createShopItem(UUID.fromString("1ae7ddf1-53b5-4f3d-9d6b-c4c4dd118a28"), ChatColor.BLUE + "Skelett", EntityType.SKELETON, 3000));
        CONTENT.put(21, shopItemFactory.createShopItem(UUID.fromString("27d709f4-7d8f-48c1-b0fe-5ebc009b2ac9"), ChatColor.BLUE + "Hühnchen", EntityType.CHICKEN, 3000));
        CONTENT.put(22, shopItemFactory.createShopItem(UUID.fromString("3ebb1170-a332-4c09-b081-422b22fb6f17"), ChatColor.BLUE + "Kuh", EntityType.COW, 4000));
        CONTENT.put(23, shopItemFactory.createShopItem(UUID.fromString("492d3942-419b-40c8-b14f-7d25b6352891"), ChatColor.BLUE + "Creeper", EntityType.CREEPER, 5000));
        CONTENT.put(24, shopItemFactory.createShopItem(UUID.fromString("58d70546-5acb-4d26-a2ab-f0b247622141"), ChatColor.BLUE + "Enderman", EntityType.ENDERMAN, 7500));
        CONTENT.put(25, shopItemFactory.createShopItem(UUID.fromString("6c97d2d7-6034-4b89-bc22-471afcc42f67"), ChatColor.BLUE + "Schaf", EntityType.SHEEP, 4000));

        CONTENT.put(28, shopItemFactory.createShopItem(UUID.fromString("7047a8b8-c0b6-4b8f-acee-37fbda2b181e"), ChatColor.BLUE + "Ocelot", EntityType.OCELOT, 6000));
        CONTENT.put(29, shopItemFactory.createShopItem(UUID.fromString("51cd13e5-8963-420d-8825-e00d8c18b78e"), ChatColor.BLUE + "Schwein", EntityType.PIG, 4000));
        CONTENT.put(30, shopItemFactory.createShopItem(UUID.fromString("2379efd0-224f-480b-89fa-1a6bfea383d0"), ChatColor.BLUE + "Schweinischer Zombie", EntityType.PIG_ZOMBIE, 6000));
        CONTENT.put(31, shopItemFactory.createShopItem(UUID.fromString("70b533ed-3da3-482e-ade8-c8c03c3fd7d6"), ChatColor.BLUE + "Pilzkuh", EntityType.MUSHROOM_COW, 5000));
        CONTENT.put(32, shopItemFactory.createShopItem(UUID.fromString("537a94da-099d-41d7-a1a2-ac3fc00970de"), ChatColor.BLUE + "Kante", EntityType.IRON_GOLEM, 10000));
        CONTENT.put(33, shopItemFactory.createShopItem(UUID.fromString("2d26da40-8a8c-4632-babb-7129fd4dbfd6"), ChatColor.BLUE + "Schneemann", EntityType.SNOWMAN, 7500));
        CONTENT.put(34, shopItemFactory.createShopItem(UUID.fromString("1210d862-c983-4b96-b6cc-7e1c791c446c"), ChatColor.BLUE + "Blaze", EntityType.BLAZE, 8500));
    }
}
