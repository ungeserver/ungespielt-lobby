package net.ungespielt.lobby.japs.handler;

import de.jackwhite20.base.api.handler.annotation.Channel;
import de.jackwhite20.base.api.handler.annotation.Key;
import de.jackwhite20.base.api.handler.annotation.Value;
import net.ungespielt.lobby.japs.data.PlayerManager;
import org.json.JSONObject;

import java.util.UUID;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
@Channel("lobby:heads")
public class PlayerHeadHandler {

    /**
     * The player manager.
     */
    private final PlayerManager playerManager;

    /**
     * Create a new player head handler.
     *
     * @param playerManager The player manager.
     */
    public PlayerHeadHandler(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @Key("action")
    @Value("get-player-head")
    public JSONObject getPlayerHead(JSONObject jsonObject) {
        UUID uniqueId = UUID.fromString(jsonObject.getString("uniqueId"));
        return new JSONObject().put("skullOwner", playerManager.getPlayerHead(uniqueId));
    }

    @Key("action")
    @Value("set-player-head")
    public JSONObject setPlayerHead(JSONObject jsonObject) {
        UUID uniqueId = UUID.fromString(jsonObject.getString("uniqueId"));
        String skullOwner = jsonObject.getString("skullOwner");

        playerManager.setPlayerHead(uniqueId, skullOwner);
        return new JSONObject();
    }
}
