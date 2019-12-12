package net.ungespielt.lobby.japs.data;

import com.couchbase.client.core.message.kv.subdoc.multi.Lookup;
import com.couchbase.client.core.message.kv.subdoc.multi.Mutation;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.subdoc.DocumentFragment;
import com.couchbase.client.java.subdoc.SubdocOptionsBuilder;
import net.ungespielt.lobby.shared.PlayerVisibilityState;
import org.slf4j.Logger;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * The manager for all player data.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class PlayerManager {

    /**
     * The prefix for the document ids.
     */
    private static final String DOCUMENT_PREFIX = "lobby:player:";

    /**
     * The bucket with the player data.
     */
    private final Bucket bucket;

    /**
     * The logger that will log all actions.
     */
    private final Logger logger;

    /**
     * Create a new player manager.
     *
     * @param bucket The bucket with the player data.
     * @param logger The logger that will log all actions.
     */
    public PlayerManager(Bucket bucket, Logger logger) {
        this.bucket = bucket;
        this.logger = logger;
    }

    /**
     * Check if the player is vanished.
     *
     * @param uniqueId The unique id of the player.
     * @return If the player is vanished.
     */
    public boolean isPlayerVanished(UUID uniqueId) {
        ensureExistence(uniqueId);

        DocumentFragment<Lookup> vanished = bucket.lookupIn(DOCUMENT_PREFIX + uniqueId).get("vanished").execute();
        return vanished.content(0, Boolean.class);
    }

    /**
     * Create the document for the given player.
     *
     * @param uniqueId The unqiue id of the player.
     */
    private void ensureExistence(UUID uniqueId) {
        if (!bucket.exists(DOCUMENT_PREFIX + uniqueId)) {
            JsonDocument jsonDocument = JsonDocument.create(DOCUMENT_PREFIX + uniqueId, JsonObject.create()
                    .put("vanished", false)
                    .put("inventory", JsonArray.create())
                    .put("currentMorph", "UNKNOWN")
                    .put("currentHead", "NONE")
                    .put("currentPet", "UNKNOWN")
                    .put("mysticChestCount", 0)
                    .put("type", "lobbyplayer")
                    .put("preferences", JsonObject.create()
                            .put("scoreboardActivated", true)
                            .put("playerVisibilityState", PlayerVisibilityState.ALL.name())));
            bucket.upsert(jsonDocument);
        }
    }

    /**
     * Set if the given player is vanished.
     *
     * @param uniqueId The unique id.
     * @param vanished If the player is vanished.
     */
    public void setPlayerVanished(UUID uniqueId, boolean vanished) {
        ensureExistence(uniqueId);

        bucket.mutateIn(DOCUMENT_PREFIX + uniqueId).upsert("vanished", vanished, SubdocOptionsBuilder.builder().createPath(true)).execute();
    }

    /**
     * Get the inventory of the given player.
     *
     * @param uniqueId The unique id of the player.
     * @return The inventory.
     */
    public List<String> getInventory(UUID uniqueId) {
        ensureExistence(uniqueId);

        DocumentFragment<Lookup> inventory = bucket.lookupIn(DOCUMENT_PREFIX + uniqueId).get("inventory").execute();
        return inventory.content(0, JsonArray.class).toList().stream().map(Object::toString).collect(Collectors.toList());
    }

    /**
     * Add the given item to the players inventory.
     *
     * @param uniqueId   The id of the player.
     * @param shopItemId The id of the item.
     */
    public void addToPlayerInventory(UUID uniqueId, UUID shopItemId) {
        ensureExistence(uniqueId);

        bucket.mutateIn(DOCUMENT_PREFIX + uniqueId).arrayAppend("inventory", shopItemId, SubdocOptionsBuilder.builder().createPath(true)).execute();
    }

    /**
     * Set the head of the given player.
     *
     * @param uniqueId   The unique id of the player.
     * @param skullOwner The skull owner.
     */
    public void setPlayerHead(UUID uniqueId, String skullOwner) {
        ensureExistence(uniqueId);

        bucket.mutateIn(DOCUMENT_PREFIX + uniqueId).upsert("currentHead", skullOwner).execute();
    }

    /**
     * Get the current head of the player.
     *
     * @param uniqueId The unique id of the player.
     * @return The owner of the skull.
     */
    public String getPlayerHead(UUID uniqueId) {
        ensureExistence(uniqueId);

        DocumentFragment<Lookup> currentHead = bucket.lookupIn(DOCUMENT_PREFIX + uniqueId).get("currentHead").execute();
        return currentHead.content(0, String.class);
    }

    /**
     * Get the morph of the given player.
     *
     * @param uniqueId The unique id.
     * @return The current morph of the player.
     */
    public String getMorph(UUID uniqueId) {
        ensureExistence(uniqueId);

        DocumentFragment<Lookup> currentHead = bucket.lookupIn(DOCUMENT_PREFIX + uniqueId).get("currentMorph").execute();
        return currentHead.content(0, String.class);
    }

    /**
     * Set the morph of a player.
     *
     * @param uniqueId     the unique id of the player.
     * @param currentMorph The morph.
     */
    public void setMorph(UUID uniqueId, String currentMorph) {
        ensureExistence(uniqueId);

        bucket.mutateIn(DOCUMENT_PREFIX + uniqueId).upsert("currentMorph", currentMorph).execute();
    }

    /**
     * Get pet of the given player.
     *
     * @param uniqueId The unique id.
     * @return The pet of the player.
     */
    public String getPlayerPet(UUID uniqueId) {
        ensureExistence(uniqueId);

        DocumentFragment<Lookup> currentHead = bucket.lookupIn(DOCUMENT_PREFIX + uniqueId).get("currentPet").execute();
        return currentHead.content(0, String.class);
    }

    /**
     * Set the pet of the given player.
     *
     * @param uniqueId   The unique id.
     * @param entityType The type of the pet.
     */
    public void setPlayerPet(UUID uniqueId, String entityType) {
        ensureExistence(uniqueId);

        bucket.mutateIn(DOCUMENT_PREFIX + uniqueId).upsert("currentPet", entityType).execute();
    }

    /**
     * Get the preferences of the given player.
     *
     * @param uniqueId The unique id.
     * @return The players preferences.
     */
    public JsonObject getPlayerPreferences(UUID uniqueId) {
        ensureExistence(uniqueId);

        DocumentFragment<Lookup> currentHead = bucket.lookupIn(DOCUMENT_PREFIX + uniqueId).get("preferences").execute();
        return currentHead.content(0, JsonObject.class);
    }

    /**
     * Update the preferences of the given player.
     *
     * @param uniqueId          The unique id.
     * @param playerPreferences The player preferences.
     */
    public void setPlayerPreferences(UUID uniqueId, JsonObject playerPreferences) {
        ensureExistence(uniqueId);

        bucket.mutateIn(DOCUMENT_PREFIX + uniqueId).upsert("preferences", playerPreferences).execute();
    }

    /**
     * Get the chest count of the given player.
     *
     * @param uniqueId The player.
     * @return The chest count.
     */
    public int getChestCount(UUID uniqueId) {
        ensureExistence(uniqueId);

        DocumentFragment<Lookup> currentHead = bucket.lookupIn(DOCUMENT_PREFIX + uniqueId).get("mysticChestCount").execute();
        return currentHead.content(0, Integer.class);
    }

    /**
     * Manipulate the chest count of the given player.
     *
     * @param uniqueId   The unique id.
     * @param chestCount The chest count.
     * @return The new chest amount.
     */
    public int manipulateChestCount(UUID uniqueId, int chestCount) {
        ensureExistence(uniqueId);

        DocumentFragment<Mutation> currentChests = bucket.mutateIn(DOCUMENT_PREFIX + uniqueId).counter("mysticChestCount", chestCount).execute();
        return currentChests.content(0, Long.class).intValue();
    }
}
