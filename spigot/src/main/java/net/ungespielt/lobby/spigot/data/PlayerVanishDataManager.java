package net.ungespielt.lobby.spigot.data;

import com.google.common.collect.Maps;
import de.jackwhite20.base.api.service.japs.PubSubService;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import net.ungespielt.lobby.spigot.api.rx.RxManager;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;
import org.json.JSONObject;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;

/**
 * The data manager for the players vanish state.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
@Singleton
public class PlayerVanishDataManager {

    /**
     * The pub sub service.
     */
    private final PubSubService pubSubService;

    /**
     * The observables of the players vanish state.
     */
    private final Map<Player, BehaviorSubject<Boolean>> playerVanishStates = Maps.newConcurrentMap();

    /**
     * The rx manager.
     */
    private final RxManager rxManager;

    /**
     * Create a new vanish data manager.
     *
     * @param pubSubService The pub sub service.
     * @param rxManager The rx manager.
     */
    @Inject
    public PlayerVanishDataManager(PubSubService pubSubService, RxManager rxManager) {
        this.pubSubService = pubSubService;
        this.rxManager = rxManager;
    }

    /**
     * Get the vanish state of a player.
     *
     * @param player The player.
     * @return The observable.
     */
    public Observable<Boolean> getPlayerVanishState(Player player) {
        BehaviorSubject<Boolean> vanishState = playerVanishStates.get(player);

        if (vanishState == null) {
            vanishState = BehaviorSubject.create();

            playerVanishStates.put(player, vanishState);
            loadVanishState(player);

            rxManager.observeEvent(PlayerQuitEvent.class)
                    .filter(playerQuitEvent -> playerQuitEvent.getPlayer() == player)
                    .take(1)
                    .subscribe(playerQuitEvent -> playerVanishStates.remove(player));
        }

        return vanishState;
    }

    /**
     * Set if a player is vanished.
     *
     * @param player            The player.
     * @param playerVanishState If the player is vanished or not.
     */
    public void setPlayerVanishState(Player player, boolean playerVanishState) {
        BehaviorSubject<Boolean> vanishState = playerVanishStates.get(player);

        if (vanishState == null) {
            vanishState = BehaviorSubject.create();
            playerVanishStates.put(player, vanishState);
        }

        vanishState.onNext(playerVanishState);
        updatePlayerVanishState(player, playerVanishState);
    }

    /**
     * Load the vanish state of the given player.
     *
     * @param player The player.
     */
    private void loadVanishState(Player player) {
        pubSubService.request("lobby:vanish", new JSONObject().put("uniqueId", player.getUniqueId()).put("action", "get-player-vanish-state"), jsonObject -> {
            boolean vanished = jsonObject.optBoolean("vanished", false);
            playerVanishStates.get(player).onNext(vanished);
        });
    }

    /**
     * Update the vanish state of the player in the network.
     *
     * @param player            The player.
     * @param playerVanishState The player vanish state.
     */
    private void updatePlayerVanishState(Player player, boolean playerVanishState) {
        pubSubService.request("lobby:vanish", new JSONObject().put("uniqueId", player.getUniqueId()).put("vanished", playerVanishState).put("action", "set-player-vanish-state"));
    }
}
