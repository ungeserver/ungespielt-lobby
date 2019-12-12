package net.ungespielt.lobby.spigot.feature.preferences.menu.visibility;

import com.google.inject.assistedinject.Assisted;
import de.jackwhite20.base.api.spigot.menu.impl.Menu;
import de.jackwhite20.base.api.spigot.util.ItemBuilder;
import de.jackwhite20.base.spigot.menu.MenuImplementation;
import net.ungespielt.lobby.shared.PlayerVisibilityState;
import net.ungespielt.lobby.spigot.data.PlayerPreferencesDataManager;
import net.ungespielt.lobby.spigot.feature.preferences.PreferencesViewModel;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class VisibilityMenu extends MenuImplementation implements Menu {

    /**
     * The corresponding view model.
     */
    private final PreferencesViewModel preferencesViewModel;

    /**
     * The back ui item stack.
     */
    private final ItemStack backItemStack;

    /**
     * The data manager for the player preferences used to highlight the current state.
     */
    private final PlayerPreferencesDataManager playerPreferencesDataManager;

    /**
     * The item stack for displaying no one.
     */
    private ItemStack noOneVisibleItemStack;

    /**
     * Only special players item stack.
     */
    private ItemStack teamVisibleItemStack;

    /**
     * The item stack for all players being visible.
     */
    private ItemStack allVisibleItemStack;

    @Inject
    public VisibilityMenu(@Assisted Player player, PreferencesViewModel preferencesViewModel, @Named("backItemStack") ItemStack backItemStack, @Named("noOneVisibleItemStack") ItemStack noOneVisibleItemStack, @Named("teamVisibleItemStack") ItemStack teamVisibleItemStack, @Named("allVisibleItemStack") ItemStack allVisibleItemStack, PlayerPreferencesDataManager playerPreferencesDataManager) {
        super(player, "§6Einstellungen §9/ §6Spielersichtbarkeit", 27);
        this.preferencesViewModel = preferencesViewModel;
        this.backItemStack = backItemStack;
        this.noOneVisibleItemStack = noOneVisibleItemStack;
        this.teamVisibleItemStack = teamVisibleItemStack;
        this.allVisibleItemStack = allVisibleItemStack;
        this.playerPreferencesDataManager = playerPreferencesDataManager;
    }

    @Override
    public void open() {
        setupItems();
        super.open();
    }

    /**
     * Setup all items in the inventory.
     */
    private void setupItems() {
        setItem(0, backItemStack, menuClickEvent -> preferencesViewModel.openPreferencesMenu(player));

        playerPreferencesDataManager.getPlayerVisibilityState(player.getUniqueId())
                .take(1).subscribe(playerVisibilityState -> {
            switch (playerVisibilityState) {
                case ALL: {
                    allVisibleItemStack = new ItemBuilder(allVisibleItemStack.clone()).glow().build();
                    break;
                }
                case NONE: {
                    noOneVisibleItemStack = new ItemBuilder(noOneVisibleItemStack.clone()).glow().build();
                    break;
                }
                case TEAM: {
                    teamVisibleItemStack = new ItemBuilder(teamVisibleItemStack.clone()).glow().build();
                    break;
                }
            }

            setItem(11, allVisibleItemStack, menuClickEvent -> {
                close();
                preferencesViewModel.handleVisibilityClicked(player, PlayerVisibilityState.ALL);
            });
            setItem(13, teamVisibleItemStack, menuClickEvent -> {
                close();
                preferencesViewModel.handleVisibilityClicked(player, PlayerVisibilityState.TEAM);
            });
            setItem(15, noOneVisibleItemStack, menuClickEvent -> {
                close();
                preferencesViewModel.handleVisibilityClicked(player, PlayerVisibilityState.NONE);
            });
        });
    }
}
