package net.ungespielt.lobby.spigot.feature.mysticchests.menu;

import com.google.inject.assistedinject.Assisted;
import de.jackwhite20.base.api.spigot.menu.impl.Menu;
import de.jackwhite20.base.spigot.menu.MenuImplementation;
import net.ungespielt.lobby.spigot.data.PlayerMysticChestsDataManager;
import net.ungespielt.lobby.spigot.feature.mysticchests.MysticChestsViewModel;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class MysticChestsMenu extends MenuImplementation implements Menu {

    /**
     * The player.
     */
    private final Player player;

    /**
     * The view model.
     */
    private final MysticChestsViewModel mysticChestsViewModel;

    /**
     * The item stack for the chest count.
     */
    private final ItemStack chestCountItemStack;

    /**
     * The data manager for the players mystic chests.
     */
    private final PlayerMysticChestsDataManager playerMysticChestsDataManager;

    /**
     * The item stack used to start a new roll.
     */
    private final ItemStack mysticRollItemStack;

    @Inject
    public MysticChestsMenu(@Assisted Player player, MysticChestsViewModel mysticChestsViewModel, @Named("chestCountItemStack") ItemStack chestCountItemStack, PlayerMysticChestsDataManager playerMysticChestsDataManager, @Named("mysticRollItemStack") ItemStack mysticRollItemStack) {
        super(player, "§6Mysticchests", 27);
        this.player = player;
        this.mysticChestsViewModel = mysticChestsViewModel;
        this.chestCountItemStack = chestCountItemStack;
        this.playerMysticChestsDataManager = playerMysticChestsDataManager;
        this.mysticRollItemStack = mysticRollItemStack;

        setupItems();
    }

    private void setupItems() {
        setItem(15, mysticRollItemStack, menuClickEvent -> {
            mysticChestsViewModel.handleRollClicked(menuClickEvent.getPlayer());
            close();
        });
    }

    @Override
    public void open() {
        playerMysticChestsDataManager.getChestCount(player.getUniqueId()).take(1)
                .subscribe(chestCount -> {
                    ItemStack clone = chestCountItemStack.clone();
                    ItemMeta itemMeta = clone.getItemMeta();
                    itemMeta.setDisplayName(itemMeta.getDisplayName() + chestCount);
                    clone.setItemMeta(itemMeta);

                    setItem(11, clone);
                    sendItem(11);
                });

        super.open();
    }
}
