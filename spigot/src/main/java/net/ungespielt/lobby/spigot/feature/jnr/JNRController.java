package net.ungespielt.lobby.spigot.feature.jnr;

import net.ungespielt.lobby.spigot.api.feature.FeatureController;
import net.ungespielt.lobby.spigot.api.feature.FeatureViewModel;

import javax.inject.Inject;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class JNRController extends FeatureController {

    /**
     * Create a new feature controller by its view model.
     *
     * @param viewModel The view model.
     */
    @Inject
    public JNRController(FeatureViewModel viewModel) {
        super(viewModel);
    }
}
