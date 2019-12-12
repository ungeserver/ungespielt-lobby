package net.ungespielt.lobby.spigot.feature.scoreboard;

import net.ungespielt.lobby.spigot.api.feature.FeatureController;
import net.ungespielt.lobby.spigot.api.rx.RxManager;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * The controller for the whole scoreboard feature.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
@Singleton
public class ScoreboardController extends FeatureController<ScoreboardViewModel> {

    /**
     * The rx manager.
     */
    private final RxManager rxManager;

    /**
     * Create a new scoreboard controller.
     *
     * @param scoreboardViewModel The view model.
     * @param rxManager           The rx manager.
     */
    @Inject
    public ScoreboardController(ScoreboardViewModel scoreboardViewModel, RxManager rxManager) {
        super(scoreboardViewModel);
        this.rxManager = rxManager;
    }

    @Override
    protected void doOnEnable() {
        subscribe(rxManager.observeEvent(PlayerJoinEvent.class).subscribe(playerJoinEvent -> getViewModel().handlePlayerJoin(playerJoinEvent.getPlayer())));
        subscribe(rxManager.observeEvent(PlayerQuitEvent.class).subscribe(playerQuitEvent -> getViewModel().handlePlayerQuit(playerQuitEvent.getPlayer())));
    }
}
