package net.ungespielt.lobby.spigot.data;

import com.google.common.collect.Maps;
import de.jackwhite20.base.api.service.player.PlayerService;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import net.ungespielt.lobby.spigot.api.rx.RxManager;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Data manager for players coins.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
@Singleton
public class PlayerCoinsDataManager {

    /**
     * The base player service.
     */
    private final PlayerService playerService;

    /**
     * The player coins.
     */
    private final Map<UUID, BehaviorSubject<Integer>> playerCoinsSubjects = Maps.newHashMap();

    /**
     * The rx manager.
     */
    private final RxManager rxManager;

    /**
     * The logger that will log all coin changes.
     */
    private final Logger logger;

    /**
     * Create a new player coins data manager by its underlying player service.
     *
     * @param playerService The player service.
     * @param rxManager The rx manager.
     */
    @Inject
    public PlayerCoinsDataManager(Plugin plugin, PlayerService playerService, RxManager rxManager) {
        this.playerService = playerService;
        this.rxManager = rxManager;
        this.logger = plugin.getLogger();
    }

    /**
     * Get the observable of the players coins.
     *
     * @param uniqueId The players unique id.
     * @return The player coins observable.
     */
    public Observable<Integer> getPlayerCoins(UUID uniqueId) {
        BehaviorSubject<Integer> playerCoins = playerCoinsSubjects.get(uniqueId);

        if (playerCoins == null) {
            playerCoins = BehaviorSubject.create();
            playerCoinsSubjects.put(uniqueId, playerCoins);

            loadCoins(uniqueId);

            rxManager.observeEvent(PlayerQuitEvent.class)
                    .filter(playerQuitEvent -> playerQuitEvent.getPlayer().getUniqueId() == uniqueId)
                    .take(1)
                    .subscribe(playerQuitEvent -> playerCoinsSubjects.remove(uniqueId));
        }

        return playerCoins;
    }

    /**
     * Load the coins of the given player.
     *
     * @param uniqueId The players unique id.
     */
    private void loadCoins(UUID uniqueId) {
        BehaviorSubject<Integer> publishSubject = playerCoinsSubjects.get(uniqueId);
        int coins = playerService.getCoins(uniqueId);
        publishSubject.onNext(coins);
    }

    /**
     * Manipulate the coins of a player.
     *
     * @param uniqueId The players unique id.
     * @param amount The amount.
     */
    public void manipulateCoins(UUID uniqueId, int amount) {
        int manipulateCoins = playerService.manipulateCoins(uniqueId, amount);
        BehaviorSubject<Integer> playerCoins = (BehaviorSubject<Integer>) getPlayerCoins(uniqueId);
        playerCoins.onNext(manipulateCoins);
    }
}
