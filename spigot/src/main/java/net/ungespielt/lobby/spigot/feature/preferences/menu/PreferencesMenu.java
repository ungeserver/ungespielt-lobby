package net.ungespielt.lobby.spigot.feature.preferences.menu;

import com.google.inject.assistedinject.Assisted;
import de.jackwhite20.base.api.spigot.menu.impl.Menu;
import de.jackwhite20.base.spigot.menu.MenuImplementation;
import net.ungespielt.lobby.spigot.feature.preferences.PreferencesViewModel;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * The menu with the players preferences.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class PreferencesMenu extends MenuImplementation implements Menu {

    /**
     * The view model.
     */
    private final PreferencesViewModel viewModel;

    /**
     * The item stack for the scoreboard preferences.
     */
    private final ItemStack scoreboardPreferencesItemStack;

    /**
     * The item stack for the visibility preferences.
     */
    private final ItemStack visibilityPreferencesItemStack;

    /**
     * The item stack for content that is coming soon.
     */
    private final ItemStack comingSoonItemStack;

    @Inject
    public PreferencesMenu(@Assisted Player player, PreferencesViewModel viewModel, @Named("scoreboardPreferencesItemStack") ItemStack scoreboardPreferencesItemStack, @Named("visibilityPreferencesItemStack") ItemStack visibilityPreferencesItemStack, @Named("comingSoonItemStack") ItemStack comingSoonItemStack) {
        super(player, "§6Einstellungen", 27);
        this.viewModel = viewModel;
        this.scoreboardPreferencesItemStack = scoreboardPreferencesItemStack;
        this.visibilityPreferencesItemStack = visibilityPreferencesItemStack;
        this.comingSoonItemStack = comingSoonItemStack;
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
        setItem(11, comingSoonItemStack);
        setItem(13, visibilityPreferencesItemStack, menuClickEvent -> viewModel.openVisibilityPreferencesMenu(menuClickEvent.getPlayer()));
        setItem(15, scoreboardPreferencesItemStack, menuClickEvent -> viewModel.openScoreboardPreferencesMenu(menuClickEvent.getPlayer()));
    }
}
