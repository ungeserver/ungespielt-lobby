package net.ungespielt.lobby.japs;

import de.jackwhite20.japs.api.module.Module;
import de.jackwhite20.japs.api.module.ModuleManager;
import de.jackwhite20.japs.api.module.annotation.ModuleInfo;
import net.ungespielt.lobby.japs.data.LobbyConfigManager;
import net.ungespielt.lobby.japs.data.PlayerManager;
import net.ungespielt.lobby.japs.handler.*;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
@ModuleInfo(name = "LobbyModule", version = "3.7-SNAPSHOT", author = "Felix Klauke")
public class LobbyModule implements Module {

    /**
     * The module manager.
     */
    private ModuleManager moduleManager;

    /**
     * The config manager.
     */
    private LobbyConfigManager configManager;

    /**
     * The player manager.
     */
    private PlayerManager playerManager;

    @Override
    public void onEnable(ModuleManager moduleManager) {
        this.moduleManager = moduleManager;
        this.configManager = new LobbyConfigManager(moduleManager.getHighBucket());
        this.playerManager = new PlayerManager(moduleManager.getHighBucket(), moduleManager.getLogger());

        moduleManager.registerRequestHandler(LobbyConfigRequestHandler.class, configManager);
        moduleManager.registerRequestHandler(PlayerVanishHandler.class, playerManager);
        moduleManager.registerRequestHandler(PlayerInventoryHandler.class, playerManager);
        moduleManager.registerRequestHandler(PlayerHeadHandler.class, playerManager);
        moduleManager.registerRequestHandler(PlayerMorphHandler.class, playerManager);
        moduleManager.registerRequestHandler(PlayerPetHandler.class, playerManager);
        moduleManager.registerRequestHandler(PlayerPreferencesHandler.class, playerManager);
        moduleManager.registerRequestHandler(PlayerMysticChestsHandler.class, playerManager);
    }

    @Override
    public void onDisable() {
        moduleManager.unregisterRequestHandler(LobbyConfigRequestHandler.class);
        moduleManager.unregisterRequestHandler(PlayerVanishHandler.class);
        moduleManager.unregisterRequestHandler(PlayerInventoryHandler.class);
        moduleManager.unregisterRequestHandler(PlayerHeadHandler.class);
        moduleManager.unregisterRequestHandler(PlayerMorphHandler.class);
        moduleManager.unregisterRequestHandler(PlayerPetHandler.class);
        moduleManager.unregisterRequestHandler(PlayerPreferencesHandler.class);
        moduleManager.unregisterRequestHandler(PlayerMysticChestsHandler.class);
    }
}
