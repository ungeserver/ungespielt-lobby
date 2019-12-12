package net.ungespielt.lobby.spigot.api.feature;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * The base view model.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class FeatureViewModel {

    /**
     * The composite disposable of all subscriptions in the view model.
     */
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    /**
     * The controller of this view model.
     */
    private FeatureController<FeatureViewModel> featureController;

    /**
     * Get the feature controller.
     *
     * @return The feature controller.
     */
    public FeatureController<FeatureViewModel> getFeatureController() {
        return featureController;
    }

    /**
     * Set the controller instance.
     *
     * @param controller      The controller.
     * @param <ViewModelType> The type of the view model.
     */
    public <ViewModelType extends FeatureViewModel> void setController(FeatureController controller) {
        this.featureController = controller;
    }

    /**
     * Register the given disposable for our composite disposable.
     *
     * @param disposable The disposable.
     */
    protected void subscribe(Disposable disposable) {
        compositeDisposable.add(disposable);
    }

    /**
     * Cancel the disposal of the given subscription.
     *
     * @param disposable The subscription.
     */
    protected void unsubscribe(Disposable disposable) {
        compositeDisposable.remove(disposable);
    }

    /**
     * Dispose all subscriptions.
     */
    public void dispose() {
        compositeDisposable.dispose();
    }
}
