package net.ungespielt.lobby.japs.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.jackwhite20.base.api.handler.annotation.Channel;
import de.jackwhite20.base.api.handler.annotation.Key;
import de.jackwhite20.base.api.handler.annotation.Value;
import net.ungespielt.lobby.japs.data.LobbyConfigManager;
import net.ungespielt.lobby.shared.LobbyConfig;
import org.json.JSONObject;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
@Channel("lobby:config")
public class LobbyConfigRequestHandler {

    /**
     * The gson instance.
     */
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * The config manager.
     */
    private final LobbyConfigManager configManager;

    /**
     * Create a new config request handler.
     *
     * @param configManager The config manager.
     */
    public LobbyConfigRequestHandler(LobbyConfigManager configManager) {
        this.configManager = configManager;
    }

    @Key("key")
    @Value("get-config")
    public JSONObject getConfig(JSONObject jsonObject) {
        LobbyConfig config = configManager.getConfig();
        return new JSONObject(gson.toJson(config));
    }
}
