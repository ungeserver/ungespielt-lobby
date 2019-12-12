package net.ungespielt.lobby.spigot.data;

import com.google.common.collect.Maps;
import de.jackwhite20.base.api.service.japs.PubSubService;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import net.ungespielt.lobby.spigot.api.rx.RxManager;
import org.bukkit.event.player.PlayerQuitEvent;
import org.json.JSONObject;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;
import java.util.UUID;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
@Singleton
public class PlayerMysticChestsDataManager {

    /**
     * All known subjects.
     */
    private final Map<UUID, PublishSubject<Integer>> playerChestCountSubjects = Maps.newConcurrentMap();

    /**
     * The pub sub service.
     */
    private final PubSubService pubSubService;

    /**
     * The rx manager.
     */
    private final RxManager rxManager;

    @Inject
    public PlayerMysticChestsDataManager(PubSubService pubSubService, RxManager rxManager) {
        this.pubSubService = pubSubService;
        this.rxManager = rxManager;
    }

    /**
     * Get the observable of the players mystic chest count.
     *
     * @param uniqueId The unique id.
     * @return The observable.
     */
    public Observable<Integer> getChestCount(UUID uniqueId) {
        PublishSubject<Integer> chestCount = playerChestCountSubjects.get(uniqueId);

        if (chestCount == null) {
            chestCount = PublishSubject.create();

            playerChestCountSubjects.put(uniqueId, chestCount);

            rxManager.observeEvent(PlayerQuitEvent.class)
                    .take(1)
                    .filter(playerQuitEvent -> playerQuitEvent.getPlayer().getUniqueId() == uniqueId)
                    .subscribe(playerQuitEvent -> playerChestCountSubjects.remove(uniqueId));
        }

        loadChestCount(uniqueId);

        return chestCount.distinctUntilChanged();
    }

    /**
     * Manipulate the amount of chests a player has.
     *
     * @param uniqueId   The unique id.
     * @param chestCount The chest count.
     */
    public void manipulateChestCount(UUID uniqueId, int chestCount) {
        pubSubService.request("lobby:player:mysticchests", new JSONObject()
                .put("action", "manipulate-player-chest-count")
                .put("uniqueId", uniqueId)
                .put("chestCount", chestCount), jsonObject -> {
            int newChestCount = jsonObject.getInt("chestCount");

            PublishSubject<Integer> publishSubject = playerChestCountSubjects.get(uniqueId);
            if (publishSubject != null) {
                publishSubject.onNext(newChestCount);
            }
        });
    }

    /**
     * Load the chest count of the given player.
     *
     * @param uniqueId The unique id of the player.
     */
    private void loadChestCount(UUID uniqueId) {
        pubSubService.request("lobby:player:mysticchests", new JSONObject()
                .put("action", "get-player-chest-count")
                .put("uniqueId", uniqueId), jsonObject -> playerChestCountSubjects.get(uniqueId).onNext(jsonObject.getInt("chestCount")));
    }
}
