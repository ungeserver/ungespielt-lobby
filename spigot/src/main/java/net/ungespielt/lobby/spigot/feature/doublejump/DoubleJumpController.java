package net.ungespielt.lobby.spigot.feature.doublejump;

import net.ungespielt.lobby.spigot.api.feature.FeatureController;
import net.ungespielt.lobby.spigot.api.rx.RxManager;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import javax.inject.Inject;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class DoubleJumpController extends FeatureController<DoubleJumpViewModel> {

    /**
     * The rx manager used for subscriptions on events.
     */
    private final RxManager rxManager;

    /**
     * Create a new feature controller by its view model.
     *
     * @param viewModel The view model.
     * @param rxManager The rx manager.
     */
    @Inject
    public DoubleJumpController(DoubleJumpViewModel viewModel, RxManager rxManager) {
        super(viewModel);
        this.rxManager = rxManager;
    }

    @Override
    protected void doOnEnable() {
        subscribe(rxManager.observeEvent(PlayerToggleFlightEvent.class).subscribe(playerToggleFlightEvent -> getViewModel().handlePlayerToggleFlight(playerToggleFlightEvent.getPlayer(), playerToggleFlightEvent.isFlying())));
        subscribe(rxManager.observeEvent(PlayerJoinEvent.class).subscribe(playerJoinEvent -> getViewModel().handlePlayerJoin(playerJoinEvent.getPlayer())));
        subscribe(rxManager.observeEvent(PlayerGameModeChangeEvent.class).subscribe(playerGameModeChangeEvent -> getViewModel().handlePlayerGameModeChange(playerGameModeChangeEvent.getPlayer())));
        subscribe(rxManager.observeEvent(PlayerQuitEvent.class).subscribe(playerQuitEvent -> getViewModel().handlePlayerQuit(playerQuitEvent.getPlayer())));
    }
}
