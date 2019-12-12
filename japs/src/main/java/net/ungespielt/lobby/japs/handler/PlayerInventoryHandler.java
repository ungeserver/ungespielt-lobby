package net.ungespielt.lobby.japs.handler;

import de.jackwhite20.base.api.handler.annotation.Channel;
import de.jackwhite20.base.api.handler.annotation.Key;
import de.jackwhite20.base.api.handler.annotation.Value;
import net.ungespielt.lobby.japs.data.PlayerManager;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.UUID;

/**
 * The handler for player inventory actions.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
@Channel("lobby:inventory")
public class PlayerInventoryHandler {

    /**
     * The player manager.
     */
    private final PlayerManager playerManager;

    /**
     * Create a new player inventory handler.
     *
     * @param playerManager The player manager.
     */
    public PlayerInventoryHandler(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @Key("action")
    @Value("get-player-inventory")
    public JSONObject getInventory(JSONObject jsonObject) {
        UUID uniqueId = UUID.fromString(jsonObject.getString("uniqueId"));
        return new JSONObject().put("inventory", new JSONArray(playerManager.getInventory(uniqueId)));
    }

    @Key("action")
    @Value("add-to-player-inventory")
    public JSONObject addToInventory(JSONObject jsonObject) {
        UUID uniqueId = UUID.fromString(jsonObject.getString("uniqueId"));
        UUID shopItemId = UUID.fromString(jsonObject.getString("shopItemId"));

        playerManager.addToPlayerInventory(uniqueId, shopItemId);
        return new JSONObject();
    }
}
