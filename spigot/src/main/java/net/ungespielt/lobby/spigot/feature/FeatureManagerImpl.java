package net.ungespielt.lobby.spigot.feature;

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.MutableClassToInstanceMap;
import com.google.inject.Injector;
import net.ungespielt.lobby.spigot.api.feature.FeatureController;
import net.ungespielt.lobby.spigot.api.feature.FeatureManager;
import org.bukkit.plugin.Plugin;

import javax.inject.Inject;

/**
 * The default implementation of the {@link FeatureManager}.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class FeatureManagerImpl implements FeatureManager {

    /**
     * The dependency injector.
     */
    private final Injector injector;

    /**
     * The bukkit plugin.
     */
    private final Plugin plugin;

    /**
     * Contains all currently known controllers.
     */
    private final ClassToInstanceMap<FeatureController> currentFeatureControllers = MutableClassToInstanceMap.create();

    /**
     * Create a new feature manager.
     *
     * @param injector The injector.
     * @param plugin   The bukkit plugin.
     */
    @Inject
    public FeatureManagerImpl(Injector injector, Plugin plugin) {
        this.injector = injector;
        this.plugin = plugin;
    }

    @Override
    public <FeatureType extends FeatureController> FeatureType enableFeature(Class<FeatureType> controllerClass) {
        FeatureType instance = injector.getInstance(controllerClass);
        currentFeatureControllers.putInstance(controllerClass, instance);

        plugin.getLogger().info("Enabling feature controlled by " + controllerClass.getSimpleName());
        instance.onEnable();
        plugin.getLogger().info("Enabled feature controller by " + controllerClass.getSimpleName());

        return instance;
    }

    @Override
    public void disableFeature(Class<? extends FeatureController> controllerClass) {
        FeatureController instance = currentFeatureControllers.getInstance(controllerClass);

        plugin.getLogger().info("Disabling feature controlled by " + controllerClass.getSimpleName());
        instance.onDisable();
        plugin.getLogger().info("Disabled feature controlled by " + controllerClass.getSimpleName());

        currentFeatureControllers.remove(controllerClass);
    }

    @Override
    public <FeatureType extends FeatureController> FeatureType getFeature(Class<FeatureType> controllerClass) {
        return currentFeatureControllers.getInstance(controllerClass);
    }

    @Override
    public void disableFeatures() {
        currentFeatureControllers.forEach((controllerClass, featureController) -> {
            plugin.getLogger().info("Disabling feature controlled by " + controllerClass);
            featureController.onDisable();
            plugin.getLogger().info("Disabled feature controlled by " + controllerClass);
        });
    }
}
