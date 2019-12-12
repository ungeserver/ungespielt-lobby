package net.ungespielt.lobby.spigot.feature.welcome;

import net.ungespielt.lobby.spigot.api.feature.FeatureController;
import net.ungespielt.lobby.spigot.api.rx.RxManager;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Welcome controller that will say hello to joining players.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
@Singleton
public class WelcomeController extends FeatureController<WelcomeViewModel> {

    /**
     * The rx manager.
     */
    private final RxManager rxManager;

    /**
     * Create a new welcome controller.
     *
     * @param welcomeViewModel The view model.
     * @param rxManager        The rx manager.
     */
    @Inject
    public WelcomeController(WelcomeViewModel welcomeViewModel, RxManager rxManager) {
        super(welcomeViewModel);
        this.rxManager = rxManager;
    }

    @Override
    protected void doOnEnable() {
        subscribe(rxManager.observeEvent(PlayerJoinEvent.class, EventPriority.LOWEST).subscribe(playerJoinEvent -> getViewModel().handlePlayerJoin(playerJoinEvent.getPlayer())));
    }
}
