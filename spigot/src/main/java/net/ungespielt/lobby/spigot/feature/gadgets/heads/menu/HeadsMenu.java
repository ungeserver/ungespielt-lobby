package net.ungespielt.lobby.spigot.feature.gadgets.heads.menu;

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
import net.ungespielt.lobby.spigot.util.SkullFactory;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * The heads menu.
 * <p>
 * Note: Content has to be static in order to let the item stacks load their textures.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class HeadsMenu extends GadgetsBaseMenu {

    /**
     * The table with the whole content.
     */
    private static final Map<Integer, ShopItem> CONTENT = Maps.newHashMap();

    /**
     * The view model.
     */
    private final GadgetsViewModel viewModel;

    /**
     * The hat off item stack.
     */
    private final ItemStack hatOffItemStack;

    /**
     * The data manager for the players inventory.
     */
    private final PlayerInventoryDataManager playerInventoryDataManager;

    /**
     * The shop manager.
     */
    private final ShopManager shopManager;

    /**
     * The shop item factory.
     */
    private final ShopItemFactory shopItemFactory;

    /**
     * The skull factory.
     */
    private final SkullFactory skullFactory;

    /**
     * Create a new heads menu.
     *
     * @param player                     The player.
     * @param viewModel                  The view model.
     * @param backItemStack              The back item stack.
     * @param playerInventoryDataManager The data manager for the players inventory.
     * @param shopManager                The shop manager.
     * @param shopItemFactory            The shop item factory.
     * @param skullFactory               The skull factory.
     */
    @Inject
    public HeadsMenu(@Assisted Player player, GadgetsViewModel viewModel, @Named("backItemStack") ItemStack backItemStack, @Named("hatOffItemStack") ItemStack hatOffItemStack, PlayerInventoryDataManager playerInventoryDataManager, ShopManager shopManager, ShopItemFactory shopItemFactory, SkullFactory skullFactory, PlayerCoinsDataManager playerCoinsDataManager, @Named("balanceItemStack") ItemStack balanceItemStack) {
        super(player, "§6Gadgets §9/ §6Köpfe", 54, viewModel, backItemStack, playerCoinsDataManager, balanceItemStack);
        this.viewModel = viewModel;
        this.hatOffItemStack = hatOffItemStack;
        this.playerInventoryDataManager = playerInventoryDataManager;
        this.shopManager = shopManager;
        this.shopItemFactory = shopItemFactory;
        this.skullFactory = skullFactory;

        checkContent();
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
        playerInventoryDataManager.getPlayerInventory(player.getUniqueId()).take(1)
                .subscribe(inventory -> CONTENT.forEach((integer, itemStackShopItem) -> {
                    ItemStack itemStack = itemStackShopItem.getContent();
                    itemStack = itemStack.clone();

                    if (inventory.contains(itemStackShopItem.getUniqueId())) {
                        itemStack = new ItemBuilder(itemStack).glow().lore("").lore(ChatColor.GREEN + "Du besitzt dieses Item.").build();
                    } else {
                        itemStack = new ItemBuilder(itemStack).lore("").lore(ChatColor.RED + "Du besitzt dieses Item nicht. Preis: " + itemStackShopItem.getPrice()).build();
                    }

                    setItem(integer, itemStack, menuClickEvent -> handlePurchase(itemStackShopItem, viewModel));
                    sendItem(integer);
                }));
        setItem(49, hatOffItemStack, menuClickEvent -> {
            viewModel.handleRemoveHeadClicked(player);
            close();
        });
    }

    private void handlePurchase(ShopItem itemStackShopItem, GadgetsViewModel viewModel) {
        playerInventoryDataManager.getPlayerInventory(player.getUniqueId())
                .take(1)
                .subscribe(uuids -> handlePurchase(uuids, itemStackShopItem, viewModel));
    }

    /**
     * Handle a purchase.
     *
     * @param uuids             The players inventory.
     * @param itemStackShopItem The shop item.
     * @param viewModel         The gadgets view model.
     */
    private void handlePurchase(List<UUID> uuids, ShopItem itemStackShopItem, GadgetsViewModel viewModel) {
        if (uuids.contains(itemStackShopItem.getUniqueId())) {
            close();
            viewModel.handleApplyHeadClicked(player, itemStackShopItem.getContent());
            return;
        }

        PurchaseContext purchaseContext = shopManager.openPurchaseContext(player, itemStackShopItem);
        purchaseContext.getAbortion().take(1).subscribe(player1 -> open());
        purchaseContext.getResult().take(1).subscribe(aBoolean -> {
            if (aBoolean) {
                close();
                viewModel.handleApplyHeadClicked(player, itemStackShopItem.getContent());
                return;
            }
            open();
        }, throwable -> open());
    }

    /**
     * Check if the whole content is present and create it otherwise.
     */
    private void checkContent() {
        if (CONTENT.size() != 0) {
            return;
        }

        ItemStack headUngespielt = skullFactory.createSkull("ungespielt");
        ItemStack headRewinside = skullFactory.createSkull("rewinside");
        ItemStack headGermanletsplay = skullFactory.createSkull("GermanLetsPlay");
        ItemStack headEarliboy = skullFactory.createSkull("earliboy");
        ItemStack headHandofblood = skullFactory.createSkull("HandOfBlood");
        ItemStack headMrmoregame = skullFactory.createSkull("MrMoreGame");
        ItemStack headDner = skullFactory.createSkull("Dner");
        ItemStack headSturmwaffel = skullFactory.createSkull("SturmwaffelHD");
        ItemStack headPaluten = skullFactory.createSkull("Paluten");
        ItemStack headNieraus = skullFactory.createSkull("NieRaus");
        ItemStack headNichtnilo = skullFactory.createSkull("NichtNilo");
        ItemStack headZander = skullFactory.createSkull("Zander");
        ItemStack headHenkenbergen = skullFactory.createSkull("HenkenBergen");
        ItemStack headLordzombey = skullFactory.createSkull("LordZombey");

        CONTENT.put(19, this.shopItemFactory.createShopItem(UUID.fromString("13b62625-ef58-48a3-b541-13180b24e502"), headUngespielt.getItemMeta().getDisplayName(), headUngespielt, 1500));
        CONTENT.put(20, this.shopItemFactory.createShopItem(UUID.fromString("2aeb67ac-3ea2-4678-86be-d126d8f37452"), headRewinside.getItemMeta().getDisplayName(), headRewinside, 1000));
        CONTENT.put(21, this.shopItemFactory.createShopItem(UUID.fromString("511d91f4-cb48-4a80-818f-f5018bb894e5"), headGermanletsplay.getItemMeta().getDisplayName(), headGermanletsplay, 1000));
        CONTENT.put(22, this.shopItemFactory.createShopItem(UUID.fromString("54fd5be0-4388-466a-a1fd-1cf7a39a1bed"), headEarliboy.getItemMeta().getDisplayName(), headEarliboy, 1000));
        CONTENT.put(23, this.shopItemFactory.createShopItem(UUID.fromString("a6118b93-b781-4438-b1a5-1c6bde20c697"), headHandofblood.getItemMeta().getDisplayName(), headHandofblood, 1000));
        CONTENT.put(24, this.shopItemFactory.createShopItem(UUID.fromString("70bc3e41-72eb-4f84-aed2-f11bfd3966c4"), headMrmoregame.getItemMeta().getDisplayName(), headMrmoregame, 1000));
        CONTENT.put(25, this.shopItemFactory.createShopItem(UUID.fromString("5bcce07c-161f-422b-ac21-c8aee47ca992"), headDner.getItemMeta().getDisplayName(), headDner, 1000));
        CONTENT.put(28, this.shopItemFactory.createShopItem(UUID.fromString("aec04c7f-d8e0-41c7-85a5-bc5be9f00b5c"), headSturmwaffel.getItemMeta().getDisplayName(), headSturmwaffel, 1000));
        CONTENT.put(29, this.shopItemFactory.createShopItem(UUID.fromString("03400fe1-5244-4928-a03c-83f76691aaeb"), headPaluten.getItemMeta().getDisplayName(), headPaluten, 1000));
        CONTENT.put(30, this.shopItemFactory.createShopItem(UUID.fromString("f57ba582-6ca9-4570-a81f-55cb0496ac31"), headNieraus.getItemMeta().getDisplayName(), headNieraus, 1000));
        CONTENT.put(31, this.shopItemFactory.createShopItem(UUID.fromString("c793712f-79d5-4b67-81c0-89bcab77c67b"), headNichtnilo.getItemMeta().getDisplayName(), headNichtnilo, 1000));
        CONTENT.put(32, this.shopItemFactory.createShopItem(UUID.fromString("85a2d2ec-0ffa-44d7-9a89-0b50491ae32b"), headZander.getItemMeta().getDisplayName(), headZander, 1000));
        CONTENT.put(33, this.shopItemFactory.createShopItem(UUID.fromString("7d2efcd3-e61c-4ffa-962d-f271b0ad3916"), headHenkenbergen.getItemMeta().getDisplayName(), headHenkenbergen, 1000));
        CONTENT.put(34, this.shopItemFactory.createShopItem(UUID.fromString("5c94fab1-e5f1-4db5-94d3-3a43e61f4586"), headLordzombey.getItemMeta().getDisplayName(), headLordzombey, 1000));
    }
}
