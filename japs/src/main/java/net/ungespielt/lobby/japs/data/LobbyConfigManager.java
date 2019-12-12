package net.ungespielt.lobby.japs.data;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.ungespielt.lobby.shared.LobbyConfig;

/**
 * The manager for the config manager.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class LobbyConfigManager {

    /**
     * The gson instance.
     */
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * The underlying bucket.
     */
    private final Bucket bucket;

    /**
     * Create a new config manager.
     *
     * @param bucket The bucket.
     */
    public LobbyConfigManager(Bucket bucket) {
        this.bucket = bucket;
    }

    /**
     * Get the config from the database.
     *
     * @return The config.
     */
    public LobbyConfig getConfig() {
        JsonDocument jsonDocument = bucket.get("lobby:config");

        if (jsonDocument == null) {
            JsonObject jsonObject = JsonObject.fromJson(gson.toJson(new LobbyConfig()));
            jsonDocument = JsonDocument.create("lobby:config", jsonObject);
            bucket.upsert(jsonDocument);
        }

        JsonObject content = jsonDocument.content();
        return gson.fromJson(content.toString(), LobbyConfig.class);
    }
}
