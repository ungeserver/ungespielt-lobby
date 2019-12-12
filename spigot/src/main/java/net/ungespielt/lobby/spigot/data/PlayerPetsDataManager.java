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
public class PlayerPetsDataManager {

    /**
     * The player pet observables.
     */
    private final Map<UUID, BehaviorSubject<EntityType>> playerPets = Maps.newHashMap();

    /**
     * The pub sub service.
     */
    private final PubSubService pubSubService;

    /**
     * The rx manager.
     */
    private final RxManager rxManager;

    @Inject
    public PlayerPetsDataManager(PubSubService pubSubService, RxManager rxManager) {
        this.pubSubService = pubSubService;
        this.rxManager = rxManager;
    }

    /**
     * Get the pet of the player.
     *
     * @param uniqueId The unique id of the player.
     * @return The observable.
     */
    public Observable<EntityType> getPlayerPet(UUID uniqueId) {
        BehaviorSubject<EntityType> playerPet = playerPets.get(uniqueId);

        if (playerPet == null) {
            playerPet = BehaviorSubject.create();
            playerPets.put(uniqueId, playerPet);

            loadPet(uniqueId);

            rxManager.observeEvent(PlayerQuitEvent.class)
                    .filter(playerQuitEvent -> playerQuitEvent.getPlayer().getUniqueId() == uniqueId)
                    .take(1)
                    .subscribe(playerQuitEvent -> playerPets.remove(uniqueId));
        }

        return playerPet;
    }

    /**
     * Load the pet  of the given player.
     *
     * @param uniqueId The unique id of the player.
     */
    private void loadPet(UUID uniqueId) {
        pubSubService.request("lobby:pets", new JSONObject().put("action", "get-player-pet").put("uniqueId", uniqueId), jsonObject -> {
            String currentPet = jsonObject.optString("currentPet");
            if (currentPet == null || currentPet.equalsIgnoreCase("NONE") || currentPet.isEmpty()) {
                return;
            }

            EntityType entityType = EntityType.valueOf(currentPet);
            playerPets.get(uniqueId).onNext(entityType);
        });
    }

    /**
     * Set the pet of the given player.
     *
     * @param uniqueId   The unique id of the player.
     * @param entityType The entity type of the pet.
     */
    public void setPet(UUID uniqueId, EntityType entityType) {
        pubSubService.request("lobby:pets", new JSONObject().put("action", "set-player-pet").put("uniqueId", uniqueId).put("currentPet", entityType.name()));
        playerPets.get(uniqueId).onNext(entityType);
    }

    /**
     * Remove the head of the given player.
     *
     * @param uniqueId The unique id.
     */
    public void removePet(UUID uniqueId) {
        setPet(uniqueId, EntityType.UNKNOWN);
    }
}
