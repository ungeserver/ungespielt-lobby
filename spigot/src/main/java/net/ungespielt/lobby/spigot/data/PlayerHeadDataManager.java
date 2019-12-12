package net.ungespielt.lobby.spigot.data;

import com.google.common.collect.Maps;
import de.jackwhite20.base.api.service.japs.PubSubService;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import net.ungespielt.lobby.spigot.api.rx.RxManager;
import org.bukkit.event.player.PlayerQuitEvent;
import org.json.JSONObject;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;
import java.util.UUID;

/**
 * The data manager for the players current head.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
@Singleton
public class PlayerHeadDataManager {

    /**
     * The observables of the players heads.
     */
    private final Map<UUID, BehaviorSubject<String>> playerHeads = Maps.newHashMap();

    /**
     * The rx manager.
     */
    private final RxManager rxManager;

    /**
     * The pub sub service.
     */
    private final PubSubService pubSubService;

    @Inject
    public PlayerHeadDataManager(RxManager rxManager, PubSubService pubSubService) {
        this.rxManager = rxManager;
        this.pubSubService = pubSubService;
    }

    /**
     * Get the observable of the players heads.
     *
     * @param uniqueId The unique id.
     * @return The observable.
     */
    public Observable<String> getPlayerHead(UUID uniqueId) {
        BehaviorSubject<String> playerHead = playerHeads.get(uniqueId);

        if (playerHead == null) {
            playerHead = BehaviorSubject.create();
            playerHeads.put(uniqueId, playerHead);

            rxManager.observeEvent(PlayerQuitEvent.class)
                    .filter(playerQuitEvent -> playerQuitEvent.getPlayer().getUniqueId() == uniqueId)
                    .take(1)
                    .subscribe(playerQuitEvent -> playerHeads.remove(uniqueId));

            loadHead(uniqueId);
        }

        return playerHead.distinctUntilChanged();
    }

    /**
     * Set the head of the given player.
     *
     * @param uniqueId the players unique id.
     */
    public void setHead(UUID uniqueId, String skullOwner) {
        playerHeads.get(uniqueId).onNext(skullOwner);
        pubSubService.request("lobby:heads", new JSONObject().put("action", "set-player-head").put("uniqueId", uniqueId).put("skullOwner", skullOwner));
    }

    /**
     * Remove the head of the given player.
     *
     * @param uniqueId The unique id of the player.
     */
    public void removeHead(UUID uniqueId) {
        setHead(uniqueId, "NONE");
    }

    /**
     * Load the head of the given player.
     *
     * @param uniqueId The unique id.
     */
    private void loadHead(UUID uniqueId) {
        pubSubService.request("lobby:heads", new JSONObject().put("action", "get-player-head").put("uniqueId", uniqueId), jsonObject -> {
            String skullOwner = jsonObject.optString("skullOwner");
            playerHeads.get(uniqueId).onNext(skullOwner);
        });
    }
}
