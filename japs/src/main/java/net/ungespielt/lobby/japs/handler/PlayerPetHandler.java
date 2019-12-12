package net.ungespielt.lobby.japs.handler;

import de.jackwhite20.base.api.handler.annotation.Channel;
import de.jackwhite20.base.api.handler.annotation.Key;
import de.jackwhite20.base.api.handler.annotation.Value;
import net.ungespielt.lobby.japs.data.PlayerManager;
import org.json.JSONObject;

import java.util.UUID;

/**
 * The handler for the player pets.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
@Channel("lobby:pets")
public class PlayerPetHandler {

    /**
     * The player manager.
     */
    private final PlayerManager playerManager;

    /**
     * Create a new player pet handler.
     *
     * @param playerManager The player manager.
     */
    public PlayerPetHandler(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @Key("action")
    @Value("get-player-pet")
    public JSONObject getPlayerPet(JSONObject jsonObject) {
        UUID uniqueId = UUID.fromString(jsonObject.getString("uniqueId"));

        return new JSONObject().put("currentPet", playerManager.getPlayerPet(uniqueId));
    }

    @Key("action")
    @Value("set-player-pet")
    public JSONObject setPlayerPet(JSONObject jsonObject) {
        UUID uniqueId = UUID.fromString(jsonObject.getString("uniqueId"));
        String entityType = jsonObject.getString("currentPet");

        playerManager.setPlayerPet(uniqueId, entityType);

        return new JSONObject();
    }
}
