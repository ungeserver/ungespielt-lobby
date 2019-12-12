package net.ungespielt.lobby.spigot.feature.preferences.menu.scoreboard;

import com.google.inject.assistedinject.Assisted;
import de.jackwhite20.base.api.spigot.menu.impl.Menu;
import de.jackwhite20.base.api.spigot.util.ItemBuilder;
import de.jackwhite20.base.spigot.menu.MenuImplementation;
import net.ungespielt.lobby.spigot.data.PlayerPreferencesDataManager;
import net.ungespielt.lobby.spigot.feature.preferences.PreferencesViewModel;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * The factory for the players scoreboard preferences.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class ScoreboardPreferencesMenu extends MenuImplementation implements Menu {

    /**
     * The preferences view model used for callbacks.
     */
    private final PreferencesViewModel preferencesViewModel;

    /**
     * The item stack to go back in navigation tree.
     */
    private final ItemStack backItemStack;

    /**
     * The item stack used to deactivate the scoreboard.
     */
    private final ItemStack deactivateScoreboardItemStack;

    /**
     * The item stack used to activate the scoreboard.
     */
    private final ItemStack activateScoreboardItemStack;

    /**
     * The players preferences data manager.
     */
    private final PlayerPreferencesDataManager playerPreferencesDataManager;

    @Inject
    public ScoreboardPreferencesMenu(@Assisted Player player, PreferencesViewModel preferencesViewModel, @Named("backItemStack") ItemStack backItemStack, @Named("deactivateScoreboardItemStack") ItemStack deactivateScoreboardItemStack, @Named("activateScoreboardItemStack") ItemStack activateScoreboardItemStack, PlayerPreferencesDataManager playerPreferencesDataManager) {
        super(player, "§6Einstellungen §9/ §6Scoreboard", 27);
        this.preferencesViewModel = preferencesViewModel;
        this.backItemStack = backItemStack;
        this.deactivateScoreboardItemStack = deactivateScoreboardItemStack;
        this.activateScoreboardItemStack = activateScoreboardItemStack;
        this.playerPreferencesDataManager = playerPreferencesDataManager;
    }

    @Override
    public void open() {
        setupItems();
        super.open();
    }

    /**
     * Setup all items in the menu.
     */
    private void setupItems() {
        setItem(0, backItemStack, menuClickEvent -> preferencesViewModel.openPreferencesMenu(player));

        playerPreferencesDataManager.getPlayerPreferences(player.getUniqueId()).take(1).subscribe(playerPreferences -> {
            setItem(11, playerPreferences.isScoreboardEnabled() ? new ItemBuilder(activateScoreboardItemStack.clone()).glow().build() : activateScoreboardItemStack, menuClickEvent -> {
                close();
                preferencesViewModel.handleActivateScoreboardClicked(menuClickEvent.getPlayer());
            });
            setItem(15, playerPreferences.isScoreboardEnabled() ? deactivateScoreboardItemStack : new ItemBuilder(deactivateScoreboardItemStack.clone()).glow().build(), menuClickEvent -> {
                close();
                preferencesViewModel.handleDeactivateScoreboardClock(menuClickEvent.getPlayer());
            });

            sendItem(11);
            sendItem(15);
        });
    }
}
