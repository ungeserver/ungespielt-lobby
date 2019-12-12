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
@Channel("lobby:player:mysticchests")
public class PlayerMysticChestsHandler {

    /**
     * The player manager.
     */
    private final PlayerManager playerManager;

    /**
     * Create a new player mystic chests handler.
     *
     * @param playerManager The player manager.
     */
    public PlayerMysticChestsHandler(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @Key("action")
    @Value("get-player-chest-count")
    public JSONObject getPlayerChestCount(JSONObject jsonObject) {
        UUID uniqueId = UUID.fromString(jsonObject.getString("uniqueId"));
        return new JSONObject().put("chestCount", playerManager.getChestCount(uniqueId));
    }

    @Key("action")
    @Value("manipulate-player-chest-count")
    public JSONObject manipulatePlayerChestCount(JSONObject jsonObject) {
        UUID uniqueId = UUID.fromString(jsonObject.getString("uniqueId"));
        int chestCount = jsonObject.getInt("chestCount");

        return new JSONObject().put("chestCount", playerManager.manipulateChestCount(uniqueId, chestCount));
    }
}
