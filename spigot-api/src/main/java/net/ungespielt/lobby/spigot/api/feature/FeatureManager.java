package net.ungespielt.lobby.spigot.api.feature;

/**
 * The feature manager that will manage if features are enabled or not.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
public interface FeatureManager {

    /**
     * Enable a feature by its class.
     *
     * @param controllerClass The class of the controller.
     * @param <FeatureType> The type of the feature.
     *
     * @return The instance of the controller.
     */
    <FeatureType extends FeatureController> FeatureType enableFeature(Class<FeatureType> controllerClass);

    /**
     * Disable a feature by its controller class.
     *
     * @param controllerClass The controller class.
     */
    void disableFeature(Class<? extends FeatureController> controllerClass);

    /**
     * Get the instance of a controller of a feature by its class.
     *
     * @param controllerClass The class of the controller.
     * @param <FeatureType> The type of the feature.
     *
     * @return The instance of the controller.
     */
    <FeatureType extends FeatureController> FeatureType getFeature(Class<FeatureType> controllerClass);

    /**
     * Disable all features.
     */
    void disableFeatures();
}
