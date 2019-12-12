package net.ungespielt.lobby.spigot.feature.gadgets.morphs.menu;

import com.google.common.collect.Maps;
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
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * The morphs menu.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class MorphsMenu extends GadgetsBaseMenu {

    /**
     * The content of the morphs menu.
     */
    private static final Map<Integer, ShopItem> CONTENT = Maps.newHashMap();

    /**
     * The view model.
     */
    private final GadgetsViewModel viewModel;

    /**
     * The unmorph item stack.
     */
    private final ItemStack unmorphItemStack;

    /**
     * The shop manager.
     */
    private final ShopManager shopManager;

    /**
     * The shop item factory.
     */
    private final ShopItemFactory shopItemFactory;

    /**
     * The data manager for the players inventory.
     */
    private final PlayerInventoryDataManager playerInventoryDataManager;

    /**
     * Create a new morphs menu.
     * @param player The player.
     * @param viewModel The view model.
     * @param shopManager The shop manager.
     * @param shopItemFactory The shop item factory.
     * @param playerInventoryDataManager The player inventory data manager.
     */
    @Inject
    public MorphsMenu(@Assisted Player player, GadgetsViewModel viewModel, @Named("backItemStack") ItemStack backItemStack, @Named("unmorphItemStack") ItemStack unmorphItemStack, ShopManager shopManager, ShopItemFactory shopItemFactory, PlayerInventoryDataManager playerInventoryDataManager, PlayerCoinsDataManager playerCoinsDataManager, @Named("balanceItemStack") ItemStack balanceItemStack) {
        super(player, "§6Gadgets §9/ §6Morphs", 54, viewModel, backItemStack, playerCoinsDataManager, balanceItemStack);
        this.viewModel = viewModel;
        this.unmorphItemStack = unmorphItemStack;
        this.shopManager = shopManager;
        this.shopItemFactory = shopItemFactory;
        this.playerInventoryDataManager = playerInventoryDataManager;

        checkContent();

        setupItems();
    }

    /**
     * Setup all items.
     */
    private void setupItems() {
        playerInventoryDataManager.getPlayerInventory(player.getUniqueId()).take(1)
                .subscribe(inventory -> CONTENT.forEach((slot, shopItem) -> {
                    EntityType content = shopItem.getContent();

                    ItemBuilder itemBuilder = new ItemBuilder(Material.SKULL_ITEM).durability((short) 3).name(shopItem.getDescription());
                    ItemStack itemStack = inventory.contains(shopItem.getUniqueId()) ? itemBuilder.glow().lore("").lore(ChatColor.GREEN + "Du besitzt dieses Item.").build() :
                            itemBuilder.lore("").lore(ChatColor.RED + "Du besitzt dieses Item nicht. Preis: " + shopItem.getPrice()).build();

                    SkullMeta itemMeta = (SkullMeta) itemStack.getItemMeta();
                    itemMeta.setOwner("MHF_" + content.name());
                    itemStack.setItemMeta(itemMeta);

                    setItem(slot, itemStack, menuClickEvent -> {
                        handlePurchase(shopItem, viewModel);
                    });

                    sendItem(slot);
                }));

        setItem(49, unmorphItemStack, menuClickEvent -> {
            viewModel.handleUnmorphPlayerClicked(menuClickEvent.getPlayer());
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
        this.playerInventoryDataManager.getPlayerInventory(player.getUniqueId())
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
            viewModel.handleMorphPlayerClicked(player, itemStackShopItem.getContent());
            close();
            return;
        }

        PurchaseContext purchaseContext = shopManager.openPurchaseContext(player, itemStackShopItem);
        purchaseContext.getAbortion().take(1).subscribe(player1 -> open());
        purchaseContext.getResult().take(1).subscribe(aBoolean -> {
            if (aBoolean) {
                close();
                viewModel.handleMorphPlayerClicked(player, itemStackShopItem.getContent());
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

        CONTENT.put(19, shopItemFactory.createShopItem(UUID.fromString("9b41b015-1d68-4f2d-b874-36ac5527d51f"), ChatColor.BLUE + "Zombie", EntityType.ZOMBIE, 3000));
        CONTENT.put(20, shopItemFactory.createShopItem(UUID.fromString("cae7ddf1-53b5-4f3d-9d6b-c4c4dd118a28"), ChatColor.BLUE + "Skelett", EntityType.SKELETON, 3000));
        CONTENT.put(21, shopItemFactory.createShopItem(UUID.fromString("47d709f4-7d8f-48c1-b0fe-5ebc009b2ac9"), ChatColor.BLUE + "Hühnchen", EntityType.CHICKEN, 3500));
        CONTENT.put(22, shopItemFactory.createShopItem(UUID.fromString("cebb1170-a332-4c09-b081-422b22fb6f17"), ChatColor.BLUE + "Kuh", EntityType.COW, 2000));
        CONTENT.put(23, shopItemFactory.createShopItem(UUID.fromString("292d3942-419b-40c8-b14f-7d25b6352891"), ChatColor.BLUE + "Creeper", EntityType.CREEPER, 3500));
        CONTENT.put(24, shopItemFactory.createShopItem(UUID.fromString("08d70546-5acb-4d26-a2ab-f0b247622141"), ChatColor.BLUE + "Enderman", EntityType.ENDERMAN, 4000));
        CONTENT.put(25, shopItemFactory.createShopItem(UUID.fromString("ac97d2d7-6034-4b89-bc22-471afcc42f67"), ChatColor.BLUE + "Schaf", EntityType.SHEEP, 2500));

        CONTENT.put(28, shopItemFactory.createShopItem(UUID.fromString("9047a8b8-c0b6-4b8f-acee-37fbda2b181e"), ChatColor.BLUE + "Ocelot", EntityType.OCELOT, 5000));
        CONTENT.put(29, shopItemFactory.createShopItem(UUID.fromString("e1cd13e5-8963-420d-8825-e00d8c18b78e"), ChatColor.BLUE + "Schwein", EntityType.PIG, 2000));
        CONTENT.put(30, shopItemFactory.createShopItem(UUID.fromString("e379efd0-224f-480b-89fa-1a6bfea383d0"), ChatColor.BLUE + "Schweinischer Zombie", EntityType.PIG_ZOMBIE, 3000));
        CONTENT.put(31, shopItemFactory.createShopItem(UUID.fromString("00b533ed-3da3-482e-ade8-c8c03c3fd7d6"), ChatColor.BLUE + "Pilzkuh", EntityType.MUSHROOM_COW, 5000));
        CONTENT.put(32, shopItemFactory.createShopItem(UUID.fromString("f37a94da-099d-41d7-a1a2-ac3fc00970de"), ChatColor.BLUE + "Kante", EntityType.IRON_GOLEM, 12500));
        CONTENT.put(33, shopItemFactory.createShopItem(UUID.fromString("6d26da40-8a8c-4632-babb-7129fd4dbfd6"), ChatColor.BLUE + "Schneemann", EntityType.SNOWMAN, 4000));
        CONTENT.put(34, shopItemFactory.createShopItem(UUID.fromString("3210d862-c983-4b96-b6cc-7e1c791c446c"), ChatColor.BLUE + "Blaze", EntityType.BLAZE, 10000));
    }
}
