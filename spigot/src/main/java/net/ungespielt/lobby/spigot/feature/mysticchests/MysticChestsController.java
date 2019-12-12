package net.ungespielt.lobby.spigot.feature.mysticchests;

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
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class MysticChestsController extends FeatureController<MysticChestsViewModel> {

    /**
     * The rx manager.
     */
    private final RxManager rxManager;

    /**
     * The item stack to open the mystic chests menu.
     */
    private final ItemStack mysticChestsItemStack;

    /**
     * Create a new feature controller by its view model.
     *
     * @param viewModel The view model.
     * @param rxManager The rx manager.
     * @param mysticChestsItemStack The item stack.
     */
    @Inject
    public MysticChestsController(MysticChestsViewModel viewModel, RxManager rxManager, @Named("mysticChestsItemStack") ItemStack mysticChestsItemStack) {
        super(viewModel);
        this.rxManager = rxManager;
        this.mysticChestsItemStack = mysticChestsItemStack;
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
                .filter(playerInteractEvent -> playerInteractEvent.getItem().isSimilar(mysticChestsItemStack))
                .subscribe(playerInteractEvent -> getViewModel().openMysticChestsMenu(playerInteractEvent.getPlayer())));
    }
}
