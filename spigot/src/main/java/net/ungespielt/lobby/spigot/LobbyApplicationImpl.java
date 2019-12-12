package net.ungespielt.lobby.spigot;

import de.jackwhite20.base.api.spigot.feature.Feature;
import de.jackwhite20.base.api.spigot.feature.FeatureService;
import de.jackwhite20.base.api.spigot.feature.impl.*;
import net.ungespielt.lobby.spigot.api.LobbyApplication;
import net.ungespielt.lobby.spigot.api.feature.FeatureManager;
import net.ungespielt.lobby.spigot.base.*;
import net.ungespielt.lobby.spigot.feature.gadgets.GadgetsController;
import net.ungespielt.lobby.spigot.feature.jnr.JNRController;
import net.ungespielt.lobby.spigot.feature.lobbyswitcher.LobbySwitcherController;
import net.ungespielt.lobby.spigot.feature.mysticchests.MysticChestsController;
import net.ungespielt.lobby.spigot.feature.preferences.PreferencesController;
import net.ungespielt.lobby.spigot.feature.scoreboard.ScoreboardController;
import net.ungespielt.lobby.spigot.feature.spawn.SpawnController;
import net.ungespielt.lobby.spigot.feature.vanish.VanishController;
import net.ungespielt.lobby.spigot.feature.welcome.WelcomeController;
import org.bukkit.plugin.Plugin;

import javax.inject.Inject;

/**
 * The central application managing the whole lobby.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class LobbyApplicationImpl implements LobbyApplication {

    /**
     * All base features we should active at runtime.
     */
    private static final Class<? extends Feature>[] BASE_FEATURES = new Class[]{
            NoItemDropFeature.class, NoItemPickupFeature.class,
            NoFoodLevelChangeFeature.class, NoDamageFeature.class,
            NoWeatherFeature.class, NoBlockPlaceFeature.class,
            NoBlockBreakFeature.class, AdventureModeFeature.class,
            NoInventoryClickFeature.class, NoOffHandFeature.class,
            NoVoidFallFeature.class, NoEnvironmentChangeFeature.class
    };

    /**
     * The spigot plugin.
     */
    private final Plugin plugin;

    /**
     * The feature manager.
     */
    private final FeatureManager featureManager;

    /**
     * The base feature service.
     */
    private final FeatureService featureService;

    /**
     * Create a new lobby application.
     *
     * @param plugin         The plugin.
     * @param featureManager the feature manager.
     * @param featureService The base feature service.
     */
    @Inject
    public LobbyApplicationImpl(Plugin plugin, FeatureManager featureManager, FeatureService featureService) {
        this.plugin = plugin;
        this.featureManager = featureManager;
        this.featureService = featureService;
    }

    @Override
    public void init() {
        plugin.getLogger().info("Initializing lobby spigot plugin.");

        featureManager.enableFeature(WelcomeController.class);
        featureManager.enableFeature(ScoreboardController.class);
        featureManager.enableFeature(VanishController.class);
        featureManager.enableFeature(GadgetsController.class);
        featureManager.enableFeature(PreferencesController.class);
        featureManager.enableFeature(LobbySwitcherController.class);
        featureManager.enableFeature(JNRController.class);
        featureManager.enableFeature(MysticChestsController.class);
        featureManager.enableFeature(SpawnController.class);
        //featureManager.enableFeature(DoubleJumpController.class);

        for (Class<? extends Feature> baseFeature : BASE_FEATURES) {
            featureService.registerFeature(baseFeature);
        }

        plugin.getLogger().info("Initialized lobby spigot plugin.");
    }

    @Override
    public void destroy() {
        plugin.getLogger().info("Destroying lobby spigot plugin.");

        featureManager.disableFeatures();

        for (Class<? extends Feature> baseFeature : BASE_FEATURES) {
            featureService.registerFeature(baseFeature);
        }

        plugin.getLogger().info("Destroyed lobby spigot plugin.");
    }
}
