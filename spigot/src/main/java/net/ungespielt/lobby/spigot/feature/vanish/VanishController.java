package net.ungespielt.lobby.spigot.feature.vanish;

import net.ungespielt.lobby.spigot.api.feature.FeatureController;
import net.ungespielt.lobby.spigot.api.rx.RxManager;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Control the vanish state of all players.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
@Singleton
public class VanishController extends FeatureController<VanishViewModel> {

    /**
     * The rx manager.
     */
    private final RxManager rxManager;

    /**
     * Create a new feature controller by its view model.
     *
     * @param viewModel The view model.
     * @param rxManager The rx manager.
     */
    @Inject
    public VanishController(VanishViewModel viewModel, RxManager rxManager) {
        super(viewModel);
        this.rxManager = rxManager;
    }

    @Override
    protected void doOnEnable() {
        subscribe(rxManager.observeEvent(PlayerJoinEvent.class)
                .subscribe(playerJoinEvent -> getViewModel().handlePlayerJoin(playerJoinEvent.getPlayer())));
        subscribe(rxManager.observeEvent(PlayerQuitEvent.class)
                .subscribe(playerQuitEvent -> getViewModel().handlePlayerQuit(playerQuitEvent.getPlayer())));
        subscribe(rxManager.observeCommand("vanish", "Vanish yourself.", "/vanish", "v")
                .subscribe(commandExecution -> getViewModel().handleCommandVanish(commandExecution)));
    }
}
