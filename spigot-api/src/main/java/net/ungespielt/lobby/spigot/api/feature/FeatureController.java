package net.ungespielt.lobby.spigot.api.feature;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * A controller for features.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class FeatureController<ViewModelType extends FeatureViewModel> {

    /**
     * The view model.
     */
    private final ViewModelType viewModel;

    /**
     * The dispose bag for all subscriptions.
     */
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    /**
     * Create a new feature controller by its view model.
     *
     * @param viewModel The view model.
     */
    public FeatureController(ViewModelType viewModel) {
        this.viewModel = viewModel;
        this.viewModel.setController(this);
    }

    /**
     * Enable the feature.
     */
    public final void onEnable() {
        doOnEnable();
    }

    /**
     * Disable the feature.
     */
    public final void onDisable() {
        compositeDisposable.dispose();
        viewModel.dispose();

        doOnDisable();
    }

    /**
     * Called when the feature gets enabled.
     */
    protected void doOnEnable() {

    }

    /**
     * Called when the feature gets disabled.
     */
    protected void doOnDisable() {

    }

    /**
     * Register a subscription.
     *
     * @param disposable The subscription.
     */
    protected void subscribe(Disposable disposable) {
        compositeDisposable.add(disposable);
    }

    /**
     * Get the view model.
     *
     * @return The view model.
     */
    public ViewModelType getViewModel() {
        return viewModel;
    }
}
