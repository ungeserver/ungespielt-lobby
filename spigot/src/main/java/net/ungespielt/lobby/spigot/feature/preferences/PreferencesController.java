package net.ungespielt.lobby.spigot.feature.preferences;

import net.ungespielt.lobby.spigot.api.event.PlayerVisibilityStateChangeEvent;
import net.ungespielt.lobby.spigot.api.feature.FeatureController;
import net.ungespielt.lobby.spigot.api.rx.RxManager;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * The controller for the player preferences.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
@Singleton
public class PreferencesController extends FeatureController<PreferencesViewModel> {

    /**
     * The rx manager.
     */
    private final RxManager rxManager;

    /**
     * The item stack for the preferences.
     */
    private final ItemStack preferencesItemStack;

    /**
     * Create a new feature controller by its view model.
     *
     * @param viewModel            The view model.
     * @param rxManager            The rx manager.
     * @param preferencesItemStack The preferences item stack.
     */
    @Inject
    public PreferencesController(PreferencesViewModel viewModel, RxManager rxManager, @Named("preferencesItemStack") ItemStack preferencesItemStack) {
        super(viewModel);
        this.rxManager = rxManager;
        this.preferencesItemStack = preferencesItemStack;
    }

    @Override
    protected void doOnEnable() {
        subscribe(rxManager.observeEvent(PlayerJoinEvent.class)
                .subscribe(playerJoinEvent -> getViewModel().handlePlayerJoin(playerJoinEvent.getPlayer())));
        subscribe(rxManager.observeEvent(PlayerQuitEvent.class)
                .subscribe(playerQuitEvent -> getViewModel().handlePlayerQuit(playerQuitEvent.getPlayer())));
        subscribe(rxManager.observeEvent(PlayerInteractEvent.class)
                .filter(playerInteractEvent -> playerInteractEvent.getAction() == Action.RIGHT_CLICK_BLOCK || playerInteractEvent.getAction() == Action.RIGHT_CLICK_AIR)
                .filter(playerInteractEvent -> playerInteractEvent.getItem() != null)
                .filter(playerInteractEvent -> playerInteractEvent.getItem().isSimilar(preferencesItemStack))
                .subscribe(playerInteractEvent -> getViewModel().openPreferencesMenu(playerInteractEvent.getPlayer())));
        subscribe(rxManager.observeEvent(PlayerVisibilityStateChangeEvent.class).subscribe(
                playerVisibilityStateChangeEvent -> getViewModel().handlePlayerVisibilityChange(playerVisibilityStateChangeEvent.getPlayer(), playerVisibilityStateChangeEvent.getNewVisibilityState())
        ));
    }
}
