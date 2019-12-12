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
@Channel("lobby:morphs")
public class PlayerMorphHandler {

    /**
     * The player manager.
     */
    private final PlayerManager playerManager;

    /**
     * Create a new player morph request handler.
     *
     * @param playerManager The player manager.
     */
    public PlayerMorphHandler(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @Key("action")
    @Value("get-player-morph")
    public JSONObject getPlayerMorph(JSONObject jsonObject) {
        UUID uniqueId = UUID.fromString(jsonObject.getString("uniqueId"));

        return new JSONObject().put("currentMorph", playerManager.getMorph(uniqueId));
    }

    @Key("action")
    @Value("set-player-morph")
    public JSONObject setPlayerMorph(JSONObject jsonObject) {
        UUID uniqueId = UUID.fromString(jsonObject.getString("uniqueId"));
        String currentMorph = jsonObject.getString("currentMorph");

        playerManager.setMorph(uniqueId, currentMorph);

        return new JSONObject();
    }
}
