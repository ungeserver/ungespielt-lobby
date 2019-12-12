package net.ungespielt.lobby.spigot.feature.spawn;

import net.ungespielt.lobby.spigot.api.feature.FeatureController;
import net.ungespielt.lobby.spigot.api.rx.RxManager;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
@Singleton
public class SpawnController extends FeatureController<SpawnViewModel> {

    /**
     * The rx manager needed for listening for commands.
     */
    private final RxManager rxManager;

    /**
     * Create a new feature controller by its view model.
     *
     * @param viewModel The view model.
     * @param rxManager The rx manager.
     */
    @Inject
    public SpawnController(SpawnViewModel viewModel, RxManager rxManager) {
        super(viewModel);
        this.rxManager = rxManager;
    }

    @Override
    protected void doOnEnable() {
        subscribe(rxManager.observeCommand("spawn", "Go back to spawn.").filter(commandExecution -> commandExecution.getCommandSender() instanceof Player).subscribe(commandExecution -> getViewModel().handleSpawnCommand(((Player) commandExecution.getCommandSender()))));
    }
}
