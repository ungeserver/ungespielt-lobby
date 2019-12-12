package net.ungespielt.lobby.spigot.feature.lobbyswitcher;

import net.ungespielt.lobby.spigot.api.feature.FeatureController;
import net.ungespielt.lobby.spigot.api.rx.RxManager;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * The controller for the lobby switcher.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class LobbySwitcherController extends FeatureController<LobbySwitcherViewModel> {

    /**
     * The rx manager.
     */
    private final RxManager rxManager;

    /**
     * The item stack for the lobby switcher.
     */
    private final ItemStack lobbySwitcherItemStack;

    /**
     * Create a new feature controller by its view model.
     *
     * @param viewModel The view model
     * @param rxManager The rx manager.
     * @param lobbySwitcherItemStack The lobby switcher item stack.
     */
    @Inject
    public LobbySwitcherController(LobbySwitcherViewModel viewModel, RxManager rxManager, @Named("lobbySwitcherItemStack") ItemStack lobbySwitcherItemStack) {
        super(viewModel);
        this.rxManager = rxManager;
        this.lobbySwitcherItemStack = lobbySwitcherItemStack;
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
                .filter(playerInteractEvent -> playerInteractEvent.getItem().isSimilar(lobbySwitcherItemStack))
                .subscribe(playerInteractEvent -> getViewModel().openLobbySwitcherMenu(playerInteractEvent.getPlayer())));
    }
}
