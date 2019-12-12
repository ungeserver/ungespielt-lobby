package net.ungespielt.lobby.japs.handler;

import com.couchbase.client.java.document.json.JsonObject;
import de.jackwhite20.base.api.handler.annotation.Channel;
import de.jackwhite20.base.api.handler.annotation.Key;
import de.jackwhite20.base.api.handler.annotation.Value;
import net.ungespielt.lobby.japs.data.PlayerManager;
import org.json.JSONObject;

import java.util.UUID;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
@Channel("lobby:player:preferences")
public class PlayerPreferencesHandler {

    /**
     * The player manager.
     */
    private final PlayerManager playerManager;

    /**
     * Create a new player preferences handler.
     *
     * @param playerManager The player manager.
     */
    public PlayerPreferencesHandler(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @Key("action")
    @Value("get-player-preferences")
    public JSONObject getPlayerPreferences(JSONObject jsonObject) {
        UUID uniqueId = UUID.fromString(jsonObject.getString("uniqueId"));

        return new JSONObject().put("preferences", new JSONObject(playerManager.getPlayerPreferences(uniqueId).toMap()));
    }

    @Key("action")
    @Value("set-player-preferences")
    public JSONObject setPlayerPreferences(JSONObject jsonObject) {
        UUID uniqueId = UUID.fromString(jsonObject.getString("uniqueId"));
        String preferences = jsonObject.getString("preferences");
        playerManager.setPlayerPreferences(uniqueId, JsonObject.fromJson(preferences));
        return new JSONObject();
    }
}
