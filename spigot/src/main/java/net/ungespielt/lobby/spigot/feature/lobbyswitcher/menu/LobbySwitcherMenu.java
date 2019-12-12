package net.ungespielt.lobby.spigot.feature.lobbyswitcher.menu;

import com.google.inject.assistedinject.Assisted;
import de.jackwhite20.base.api.service.cluster.ClusterServer;
import de.jackwhite20.base.api.service.cluster.ClusterService;
import de.jackwhite20.base.api.spigot.menu.impl.Menu;
import de.jackwhite20.base.api.spigot.util.ItemBuilder;
import de.jackwhite20.base.spigot.menu.MenuImplementation;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import net.ungespielt.lobby.spigot.feature.lobbyswitcher.LobbySwitcherViewModel;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Objects;

/**
 * The menu of all lobby servers.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class LobbySwitcherMenu extends MenuImplementation implements Menu {

    /**
     * The name of the server group of the lobbies.
     */
    private final String lobbyClusterServerGroupName;

    /**
     * The view model.
     */
    private final LobbySwitcherViewModel viewModel;

    /**
     * The cluster service.
     */
    private final ClusterService clusterService;

    @Inject
    public LobbySwitcherMenu(@Assisted Player player, LobbySwitcherViewModel viewModel, ClusterService clusterService, @Named("lobbyClusterServerGroupName") String lobbyClusterServerGroupName) {
        super(player, "§6Lobby wechseln", 27);
        this.lobbyClusterServerGroupName = lobbyClusterServerGroupName;
        this.viewModel = viewModel;
        this.clusterService = clusterService;
    }

    @Override
    public void open() {
        setupItems();
        super.open();
    }

    /**
     * Setup the items of the inventory.
     */
    private void setupItems() {
        Observable.fromCallable(() -> clusterService.getServers(lobbyClusterServerGroupName))
                .subscribeOn(Schedulers.io())
                .subscribe(clusterServers -> {
                    for (int i = 0; i < clusterServers.size(); i++) {
                        ClusterServer clusterServer = clusterServers.get(i);

                        ItemBuilder itemBuilder = new ItemBuilder(Material.KNOWLEDGE_BOOK).name(ChatColor.GOLD + clusterServer.getId());

                        if (Objects.equals(clusterService.getCurrent().getId(), clusterServer.getId())) {
                            itemBuilder = itemBuilder.glow();
                        }

                        itemBuilder = itemBuilder.lore("").lore(ChatColor.GREEN + "Spieler: " + ChatColor.GRAY + clusterServer.getRealOnlinePlayers() + "/" + clusterServer.getRealMaxPlayers());
                        setItem(10 + i, itemBuilder.build(), menuClickEvent -> viewModel.handleSwitchLobbyClicked(player, clusterServer.getId()));
                    }

                    sendAllItems();
                    player.updateInventory();
                });
    }
}
