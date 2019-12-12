package net.ungespielt.lobby.spigot.data;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.jackwhite20.base.api.service.japs.PubSubService;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import net.ungespielt.lobby.spigot.api.rx.RxManager;
import org.bukkit.event.player.PlayerQuitEvent;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * The data manager for the Players inventory.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
@Singleton
public class PlayerInventoryDataManager {

    /**
     * The subjects of the player inventories.
     */
    private final Map<UUID, BehaviorSubject<List<UUID>>> playerInventorySubjects = Maps.newConcurrentMap();

    /**
     * The pub sub service.
     */
    private final PubSubService pubSubService;

    /**
     * The rx manager.
     */
    private final RxManager rxManager;

    @Inject
    public PlayerInventoryDataManager(PubSubService pubSubService, RxManager rxManager) {
        this.pubSubService = pubSubService;
        this.rxManager = rxManager;
    }

    /**
     * Get the inventory of the given player.
     *
     * @param uniqueId The player's unique id.
     * @return The observable with the inventory.
     */
    public Observable<List<UUID>> getPlayerInventory(UUID uniqueId) {
        BehaviorSubject<List<UUID>> playerInventory = playerInventorySubjects.get(uniqueId);

        if (playerInventory == null) {
            playerInventory = BehaviorSubject.create();
            playerInventorySubjects.put(uniqueId, playerInventory);

            loadPlayerInventory(uniqueId);

            rxManager.observeEvent(PlayerQuitEvent.class)
                    .filter(playerQuitEvent -> playerQuitEvent.getPlayer().getUniqueId() == uniqueId)
                    .take(1)
                    .subscribe(playerQuitEvent -> playerInventorySubjects.remove(uniqueId));
        }

        return playerInventory;
    }

    /**
     * Add an item to a players inventory.
     *
     * @param uniqueID   The players unique id.
     * @param uniqueId The item.
     */
    public void addToInventory(UUID uniqueID, UUID uniqueId) {
        getPlayerInventory(uniqueID).take(1).subscribe(uuids -> {
            uuids.add(uniqueId);
            playerInventorySubjects.get(uniqueID).onNext(uuids);
        });

        pubSubService.request("lobby:inventory", new JSONObject().put("action", "add-to-player-inventory").put("uniqueId", uniqueID).put("shopItemId", uniqueId));
    }

    /**
     * Load the inventory of the given Player.
     *
     * @param uniqueId The player's unique id.
     */
    private void loadPlayerInventory(UUID uniqueId) {
        pubSubService.request("lobby:inventory", new JSONObject().put("action", "get-player-inventory").put("uniqueId", uniqueId), jsonObject -> {
            JSONArray inventoryArray = jsonObject.optJSONArray("inventory");
            List<UUID> inventory = inventoryArray == null ? Lists.newArrayList() : inventoryArray.toList().stream().map(o -> UUID.fromString(o.toString())).collect(Collectors.toList());
            playerInventorySubjects.get(uniqueId).onNext(inventory);
        });
    }
}
