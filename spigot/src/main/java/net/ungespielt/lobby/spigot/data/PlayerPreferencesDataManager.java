package net.ungespielt.lobby.spigot.data;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.jackwhite20.base.api.service.japs.PubSubService;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import net.ungespielt.lobby.shared.PlayerPreferences;
import net.ungespielt.lobby.shared.PlayerVisibilityState;
import net.ungespielt.lobby.spigot.api.rx.RxManager;
import org.bukkit.event.player.PlayerQuitEvent;
import org.json.JSONObject;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * The data manager for the players preferences.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
@Singleton
public class PlayerPreferencesDataManager {

    /**
     * The gson instance.
     */
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * The observable of the players preferences.
     */
    private final Map<UUID, BehaviorSubject<PlayerPreferences>> playerPreferencesSubjects = Maps.newConcurrentMap();

    /**
     * The pub sub service.
     */
    private final PubSubService pubSubService;

    /**
     * The rx manager.
     */
    private final RxManager rxManager;

    @Inject
    public PlayerPreferencesDataManager(PubSubService pubSubService, RxManager rxManager) {
        this.pubSubService = pubSubService;
        this.rxManager = rxManager;
    }

    public Observable<PlayerVisibilityState> getPlayerVisibilityState(UUID uniqueId) {
        return getPlayerPreferences(uniqueId).map(PlayerPreferences::getPlayerVisibilityState)
                .distinctUntilChanged();
    }

    public Observable<PlayerPreferences> getPlayerPreferences(UUID uniqueId) {
        BehaviorSubject<PlayerPreferences> playerPreferences = playerPreferencesSubjects.get(uniqueId);

        if (playerPreferences == null) {
            playerPreferences = BehaviorSubject.create();

            playerPreferencesSubjects.put(uniqueId, playerPreferences);

            loadPlayerPreferences(uniqueId);

            rxManager.observeEvent(PlayerQuitEvent.class)
                    .filter(playerQuitEvent -> playerQuitEvent.getPlayer().getUniqueId() == uniqueId)
                    .take(1)
                    .subscribe(playerQuitEvent -> playerPreferencesSubjects.remove(uniqueId));
        }

        return playerPreferences;
    }

    /**
     * Load the players preferences from the server.
     *
     * @param uniqueId The unique id.
     */
    private void loadPlayerPreferences(UUID uniqueId) {
        pubSubService.request("lobby:player:preferences", new JSONObject().put("action", "get-player-preferences").put("uniqueId", uniqueId), new Consumer<JSONObject>() {
            @Override
            public void accept(JSONObject jsonObject) {
                JSONObject preferences = jsonObject.getJSONObject("preferences");
                playerPreferencesSubjects.get(uniqueId).onNext(gson.fromJson(preferences.toString(), PlayerPreferences.class));
            }
        });
    }

    /**
     * Update the players preferences.
     *
     * @param uniqueId          The unique id of the player.
     * @param playerPreferences The players new preferences.
     */
    public void updatePlayerPreferences(UUID uniqueId, PlayerPreferences playerPreferences) {
        pubSubService.request("lobby:player:preferences", new JSONObject().put("action", "set-player-preferences").put("uniqueId", uniqueId).put("preferences", gson.toJson(playerPreferences)));
        playerPreferencesSubjects.get(uniqueId).onNext(playerPreferences);
    }
}
