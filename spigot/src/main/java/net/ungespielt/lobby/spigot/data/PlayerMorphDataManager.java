package net.ungespielt.lobby.spigot.data;

import com.google.common.collect.Maps;
import de.jackwhite20.base.api.service.japs.PubSubService;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import net.ungespielt.lobby.spigot.api.rx.RxManager;
import org.bukkit.entity.EntityType;
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
public class PlayerMorphDataManager {

    /**
     * The player morph observables.
     */
    private final Map<UUID, BehaviorSubject<EntityType>> playerMorphs = Maps.newHashMap();

    /**
     * The pub sub service.
     */
    private final PubSubService pubSubService;

    /**
     * The rx manager.
     */
    private final RxManager rxManager;

    @Inject
    public PlayerMorphDataManager(PubSubService pubSubService, RxManager rxManager) {
        this.pubSubService = pubSubService;
        this.rxManager = rxManager;
    }

    /**
     * Get the morph of the player.
     *
     * @param uniqueId The unique id of the player.
     * @return The observable.
     */
    public Observable<EntityType> getPlayerMorph(UUID uniqueId) {
        BehaviorSubject<EntityType> playerMorph = playerMorphs.get(uniqueId);

        if (playerMorph == null) {
            playerMorph = BehaviorSubject.create();
            playerMorphs.put(uniqueId, playerMorph);

            loadMorph(uniqueId);

            rxManager.observeEvent(PlayerQuitEvent.class)
                    .filter(playerQuitEvent -> playerQuitEvent.getPlayer().getUniqueId() == uniqueId)
                    .take(1)
                    .subscribe(playerQuitEvent -> playerMorphs.remove(uniqueId));
        }

        return playerMorph;
    }

    /**
     * Load the morph of the given player.
     *
     * @param uniqueId The unique id of the player.
     */
    private void loadMorph(UUID uniqueId) {
        pubSubService.request("lobby:morphs", new JSONObject().put("action", "get-player-morph").put("uniqueId", uniqueId), jsonObject -> {
            String currentMorph = jsonObject.optString("currentMorph");
            if (currentMorph == null || currentMorph.equalsIgnoreCase("NONE") || currentMorph.isEmpty()) {
                return;
            }

            EntityType entityType = EntityType.valueOf(currentMorph);
            playerMorphs.get(uniqueId).onNext(entityType);
        });
    }

    /**
     * Set the morph of the given player.
     *
     * @param uniqueId   The unique id of the player.
     * @param entityType The entity type of the morph.
     */
    public void setMorph(UUID uniqueId, EntityType entityType) {
        pubSubService.request("lobby:morphs", new JSONObject().put("action", "set-player-morph").put("uniqueId", uniqueId).put("currentMorph", entityType.name()));
        playerMorphs.get(uniqueId).onNext(entityType);
    }

    /**
     * Remove the head of the given player.
     *
     * @param uniqueId The unique id.
     */
    public void removeMorph(UUID uniqueId) {
        setMorph(uniqueId, EntityType.UNKNOWN);
    }
}
