package net.ungespielt.lobby.spigot.feature.mysticchests;

import de.jackwhite20.base.api.spigot.menu.impl.Menu;
import net.ungespielt.lobby.spigot.api.feature.FeatureViewModel;
import net.ungespielt.lobby.spigot.api.feature.mysticchests.reward.Reward;
import net.ungespielt.lobby.spigot.data.PlayerMysticChestsDataManager;
import net.ungespielt.lobby.spigot.feature.mysticchests.menu.MysticChestsMenuFactory;
import net.ungespielt.lobby.spigot.feature.mysticchests.menu.roll.MysticChestRollMenuFactory;
import net.ungespielt.lobby.spigot.feature.mysticchests.reward.RewardFactory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class MysticChestsViewModel extends FeatureViewModel {

    /**
     * The factory for the mystic chests menu.
     */
    private final MysticChestsMenuFactory mysticChestsMenuFactory;

    /**
     * The item stack to open the mystic chests menu.
     */
    private final ItemStack mysticChestsItemStack;

    /**
     * The slot for the mystic chests item stack.
     */
    private final int mysticChestsItemSlot;

    /**
     * The data manager for the players mystic chest.
     */
    private final PlayerMysticChestsDataManager playerMysticChestsDataManager;

    /**
     * The global message prefix;
     */
    private final String prefix;

    /**
     * The factory for the roll menu.
     */
    private final MysticChestRollMenuFactory mysticChestRollMenuFactory;

    /**
     * The factory for all rewards.
     */
    private final RewardFactory rewardFactory;

    @Inject
    public MysticChestsViewModel(MysticChestsMenuFactory mysticChestsMenuFactory, @Named("mysticChestsItemStack") ItemStack mysticChestsItemStack, @Named("mysticChestsItemSlot") int mysticChestsItemSlot, PlayerMysticChestsDataManager playerMysticChestsDataManager, @Named("prefix") String prefix, MysticChestRollMenuFactory mysticChestRollMenuFactory, RewardFactory rewardFactory) {
        this.mysticChestsMenuFactory = mysticChestsMenuFactory;
        this.mysticChestsItemStack = mysticChestsItemStack;
        this.mysticChestsItemSlot = mysticChestsItemSlot;
        this.playerMysticChestsDataManager = playerMysticChestsDataManager;
        this.prefix = prefix;
        this.mysticChestRollMenuFactory = mysticChestRollMenuFactory;
        this.rewardFactory = rewardFactory;
    }

    /**
     * Handle that the given player clicked the holy mystic chests item stack.
     *
     * @param player The player.
     */
    public void openMysticChestsMenu(Player player) {
        Menu mysticChestsMenu = mysticChestsMenuFactory.createMysticChestsMenu(player);
        mysticChestsMenu.open();
    }

    /**
     * Handle that the given player joined.
     *
     * @param player The player.
     */
    public void handlePlayerJoin(Player player) {
        player.getInventory().setItem(mysticChestsItemSlot, mysticChestsItemStack);
    }

    /**
     * Handle the quit of the given player.
     *
     * @param player The player.
     */
    public void handlePlayerQuit(Player player) {
        player.getInventory().remove(mysticChestsItemStack);
    }

    /**
     * Handle that the given player clicked that he wants to start a new roll.
     *
     * @param player The player.
     */
    public void handleRollClicked(Player player) {
        playerMysticChestsDataManager.getChestCount(player.getUniqueId()).take(1).subscribe(chestCount -> {
            if (chestCount == 0) {
                player.sendMessage(prefix + "Du hast keine weiteren Kisten.");
                return;
            }

            openRollMenu(player);
        });
    }

    /**
     * Open the roll menu for the given player.
     *
     * @param player The player.
     */
    private void openRollMenu(Player player) {
        Menu mysticChestRollMenu = mysticChestRollMenuFactory.createMysticChestRollMenu(player);
        mysticChestRollMenu.open();
    }

    /**
     * Handle that the roll of the given player ended.
     *
     * @param player The player.
     */
    public void handleRewardCycleComplete(Player player) {
        Reward reward = rewardFactory.createRandomReward(player);
        reward.applyTo(player);

        playerMysticChestsDataManager.manipulateChestCount(player.getUniqueId(), -1);
    }
}
