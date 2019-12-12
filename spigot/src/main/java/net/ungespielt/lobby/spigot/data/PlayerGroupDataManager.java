package net.ungespielt.lobby.spigot.data;

import com.google.common.collect.Maps;
import de.jackwhite20.base.api.service.permission.Group;
import de.jackwhite20.base.api.service.permission.PermissionService;
import de.jackwhite20.base.api.service.permission.Permissions;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import net.ungespielt.lobby.spigot.api.rx.RxManager;
import org.bukkit.event.player.PlayerQuitEvent;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;
import java.util.UUID;

/**
 * The data manager that provides the group of a player.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
@Singleton
public class PlayerGroupDataManager {

    /**
     * The default group if no other is available.
     */
    private static final Group DEFAULT_GROUP = new Group(0, "Spieler", "§7", "§f", "§f", new Permissions());

    /**
     * The palyer groups.
     */
    private final Map<UUID, BehaviorSubject<Group>> playerGroupSubjects = Maps.newConcurrentMap();

    /**
     * The permission service.
     */
    private final PermissionService permissionService;

    /**
     * The rx manager.
     */
    private final RxManager rxManager;

    /**
     * Create a new player group data manager by its underlying service.
     *
     * @param permissionService The permission service.
     * @param rxManager The rx manager.
     */
    @Inject
    public PlayerGroupDataManager(PermissionService permissionService, RxManager rxManager) {
        this.permissionService = permissionService;
        this.rxManager = rxManager;
    }

    /**
     * Get the observable with the group of a player.
     *
     * @param uniqueId The player.
     * @return The observable that will emit the group of a player.
     */
    public Observable<Group> getPlayerGroup(UUID uniqueId) {
        BehaviorSubject<Group> playerGroup = playerGroupSubjects.get(uniqueId);

        if (playerGroup == null) {
            playerGroup = BehaviorSubject.createDefault(DEFAULT_GROUP);
            playerGroupSubjects.put(uniqueId, playerGroup);

            loadGroup(uniqueId);

            rxManager.observeEvent(PlayerQuitEvent.class)
                    .filter(playerQuitEvent -> playerQuitEvent.getPlayer().getUniqueId() == uniqueId)
                    .take(1)
                    .subscribe(playerQuitEvent -> playerGroupSubjects.remove(uniqueId));
        }

        return playerGroup;
    }

    /**
     * Load the group of the given player.
     *
     * @param uniqueId The player.
     */
    private void loadGroup(UUID uniqueId) {
        Group group = permissionService.getGroup(uniqueId);
        playerGroupSubjects.get(uniqueId).onNext(group == null ? DEFAULT_GROUP : group);
    }
}
