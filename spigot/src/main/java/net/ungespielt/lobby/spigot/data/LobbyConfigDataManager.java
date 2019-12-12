package net.ungespielt.lobby.spigot.data;

import de.jackwhite20.base.api.service.japs.PubSubService;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import net.ungespielt.lobby.shared.LobbyConfig;
import net.ungespielt.lobby.spigot.util.JsonConsumer;
import org.bukkit.plugin.Plugin;
import org.json.JSONObject;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.logging.Logger;

/**
 * The data manager that provides the lobby config.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
@Singleton
public class LobbyConfigDataManager {

    /**
     * The pub sub service for the backend.
     */
    private final PubSubService pubSubService;

    /**
     * The logger.
     */
    private final Logger logger;

    /**
     * The observable of the lobby config.
     */
    private BehaviorSubject<LobbyConfig> configObservable;

    /**
     * Create a new lobby config data manager.
     *
     * @param plugin        The plugin.
     * @param pubSubService The pub sub service.
     */
    @Inject
    public LobbyConfigDataManager(Plugin plugin, PubSubService pubSubService) {
        this.pubSubService = pubSubService;
        this.logger = plugin.getLogger();
    }

    /**
     * Get the observable of the config.
     *
     * @return The config.
     */
    public Observable<LobbyConfig> getConfig() {
        if (configObservable == null) {
            configObservable = BehaviorSubject.create();

            loadConfig();
        }

        return configObservable;
    }

    /**
     * Load the config and emit it via the observable.
     */
    private void loadConfig() {
        logger.info("Loading lobby config.");

        pubSubService.request("lobby:config", new JSONObject().put("action", "get-config"), new JsonConsumer<LobbyConfig>(LobbyConfig.class, false) {
            @Override
            public void onFailure(JSONObject jsonObject, Throwable throwable) {
                LobbyConfig lobbyConfig = new LobbyConfig();
                configObservable.onNext(lobbyConfig);
            }

            @Override
            public void onSuccess(JSONObject jsonObject, LobbyConfig model) {
                configObservable.onNext(model);
            }
        });
    }
}
