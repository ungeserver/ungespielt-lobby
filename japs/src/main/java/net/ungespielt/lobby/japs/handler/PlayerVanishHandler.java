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
@Channel("lobby:vanish")
public class PlayerVanishHandler {

    /**
     * The player manager.
     */
    private final PlayerManager playerManager;

    /**
     * Create a new player vanish handler.
     *
     * @param playerManager The player manager.
     */
    public PlayerVanishHandler(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @Key("action")
    @Value("get-player-vanish-state")
    public JSONObject getPlayerVanishState(JSONObject jsonObject) {
        UUID uniqueId = UUID.fromString(jsonObject.getString("uniqueId"));

        return new JSONObject().put("vanished", playerManager.isPlayerVanished(uniqueId));
    }

    @Key("action")
    @Value("set-player-vanish-state")
    public JSONObject setPlayerVanishState(JSONObject jsonObject) {
        UUID uniqueId = UUID.fromString(jsonObject.getString("uniqueId"));
        boolean vanished = jsonObject.getBoolean("vanished");

        playerManager.setPlayerVanished(uniqueId, vanished);
        return new JSONObject();
    }
}
