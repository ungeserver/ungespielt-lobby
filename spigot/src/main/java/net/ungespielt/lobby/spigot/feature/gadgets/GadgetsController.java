package net.ungespielt.lobby.spigot.feature.gadgets;

import net.ungespielt.lobby.spigot.api.event.*;
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
 * The controller for all gadgets.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
@Singleton
public class GadgetsController extends FeatureController<GadgetsViewModel> {

    /**
     * The rx manager.
     */
    private final RxManager rxManager;

    /**
     * The gadgets item stack.
     */
    private final ItemStack gadgetsItemStack;

    /**
     * Create a new feature controller by its view model.
     *
     * @param viewModel                    The view model.
     * @param rxManager                    The rx manager.
     * @param gadgetsItemStack             The gadgets item stack.
     */
    @Inject
    public GadgetsController(GadgetsViewModel viewModel, RxManager rxManager, @Named("gadgetsItemStack") ItemStack gadgetsItemStack) {
        super(viewModel);
        this.rxManager = rxManager;
        this.gadgetsItemStack = gadgetsItemStack;
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
                .filter(playerInteractEvent -> playerInteractEvent.getItem().isSimilar(gadgetsItemStack))
                .subscribe(playerInteractEvent -> getViewModel().openGadgetsOverview(playerInteractEvent.getPlayer())));

        subscribe(rxManager.observeEvent(PlayerHeadApplyEvent.class)
                .subscribe(playerHeadApplyEvent -> getViewModel().handlePlayerHeadApply(playerHeadApplyEvent.getPlayer(), playerHeadApplyEvent.getHead())));
        subscribe(rxManager.observeEvent(PlayerHeadRemoveEvent.class)
                .subscribe(playerHeadRemoveEvent -> getViewModel().handlePlayerHeadRemove(playerHeadRemoveEvent.getPlayer(), playerHeadRemoveEvent.getHead())));
        subscribe(rxManager.observeEvent(PlayerMorphEvent.class)
                .subscribe(playerMorphEvent -> getViewModel().handlePlayerMorph(playerMorphEvent.getPlayer(), playerMorphEvent.getMorph())));
        subscribe(rxManager.observeEvent(PlayerUnmorphEvent.class)
                .subscribe(playerUnmorphEvent -> getViewModel().handlePlayerUnmorph(playerUnmorphEvent.getPlayer(), playerUnmorphEvent.getMorph())));
        subscribe(rxManager.observeEvent(PlayerPetCreateEvent.class)
                .subscribe(playerPetCreateEvent -> getViewModel().handlePetCreate(playerPetCreateEvent.getPlayer(), playerPetCreateEvent.getPet())));
        subscribe(rxManager.observeEvent(PlayerPetRemoveEvent.class)
                .subscribe(playerPetRemoveEvent -> getViewModel().handlePetRemove(playerPetRemoveEvent.getPlayer(), playerPetRemoveEvent.getPet(), playerPetRemoveEvent.getReason())));
    }
}
