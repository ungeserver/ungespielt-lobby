package net.ungespielt.lobby.spigot.feature.preferences;

import com.comphenix.packetwrapper.WrapperPlayServerNamedEntitySpawn;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ConnectionSide;
import de.jackwhite20.base.api.spigot.menu.impl.Menu;
import io.reactivex.schedulers.Schedulers;
import net.ungespielt.lobby.shared.PlayerVisibilityState;
import net.ungespielt.lobby.spigot.api.event.PlayerVisibilityStateChangeEvent;
import net.ungespielt.lobby.spigot.api.feature.FeatureViewModel;
import net.ungespielt.lobby.spigot.api.packetwrapper.WrapperPlayServerSpawnEntityLiving;
import net.ungespielt.lobby.spigot.api.rx.RxManager;
import net.ungespielt.lobby.spigot.data.PlayerGroupDataManager;
import net.ungespielt.lobby.spigot.data.PlayerPreferencesDataManager;
import net.ungespielt.lobby.spigot.feature.preferences.menu.PreferencesMenuFactory;
import net.ungespielt.lobby.spigot.feature.preferences.menu.scoreboard.ScoreboardPreferencesMenuFactory;
import net.ungespielt.lobby.spigot.feature.preferences.menu.visibility.VisibilityMenuFactory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.UUID;

/**
 * The view model for {@link PreferencesController}.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
@Singleton
public class PreferencesViewModel extends FeatureViewModel {

    /**
     * The global message prefix.
     */
    private final String prefix;

    /**
     * The factory for the preferences menu.
     */
    private final PreferencesMenuFactory preferencesMenuFactory;

    /**
     * The item stack for the preferences.
     */
    private final ItemStack preferencesItemStack;

    /**
     * The item slot for the preferences item stack.
     */
    private final int preferencesItemSlot;

    /**
     * The factory for the scoreboard preferences menu.
     */
    private final ScoreboardPreferencesMenuFactory scoreboardPreferencesMenuFactory;

    /**
     * The factory for the visibility menu.
     */
    private final VisibilityMenuFactory visibilityMenuFactory;

    /**
     * The player preferences data manager.
     */
    private final PlayerPreferencesDataManager playerPreferencesDataManager;

    /**
     * The bukkit plugin manager.
     */
    private final PluginManager pluginManager;

    /**
     * The rx manager used for subscriptions.
     */
    private RxManager rxManager;

    /**
     * Th eplugin used to run sync tasks.
     */
    private final Plugin plugin;

    @Inject
    public PreferencesViewModel(@Named("prefix") String prefix, PreferencesMenuFactory preferencesMenuFactory, @Named("preferencesItemStack") ItemStack preferencesItemStack, @Named("preferencesItemSlot") int preferencesItemSlot, ScoreboardPreferencesMenuFactory scoreboardPreferencesMenuFactory, VisibilityMenuFactory visibilityMenuFactory, PlayerPreferencesDataManager playerPreferencesDataManager, PluginManager pluginManager, RxManager rxManager, PlayerGroupDataManager playerGroupDataManager, Plugin plugin) {
        this.prefix = prefix;
        this.preferencesMenuFactory = preferencesMenuFactory;
        this.preferencesItemStack = preferencesItemStack;
        this.preferencesItemSlot = preferencesItemSlot;
        this.scoreboardPreferencesMenuFactory = scoreboardPreferencesMenuFactory;
        this.visibilityMenuFactory = visibilityMenuFactory;
        this.playerPreferencesDataManager = playerPreferencesDataManager;
        this.pluginManager = pluginManager;
        this.rxManager = rxManager;
        this.plugin = plugin;

        rxManager.observePacket(PacketType.Play.Server.NAMED_ENTITY_SPAWN, ConnectionSide.SERVER_SIDE)
                .subscribe(packetEvent -> {
                    UUID playerUniqueId = packetEvent.getPlayer().getUniqueId();
                    playerPreferencesDataManager.getPlayerVisibilityState(playerUniqueId)
                            .take(1)
                            .subscribe(playerVisibilityState -> {
                                WrapperPlayServerNamedEntitySpawn wrapperPlayServerNamedEntitySpawn = new WrapperPlayServerNamedEntitySpawn(packetEvent.getPacket());
                                if (wrapperPlayServerNamedEntitySpawn.getEntityID() < 0) {
                                    return;
                                }

                                if (playerVisibilityState == PlayerVisibilityState.NONE) {
                                    packetEvent.setCancelled(true);
                                } else if (playerVisibilityState == PlayerVisibilityState.TEAM) {
                                    UUID playerUUID = wrapperPlayServerNamedEntitySpawn.getPlayerUUID();

                                    playerGroupDataManager.getPlayerGroup(playerUUID).take(1)
                                            .subscribe(group -> {
                                                if (group.getId() < 10) {
                                                    packetEvent.setCancelled(true);
                                                }
                                            });
                                }
                            });
                });

        rxManager.observePacket(PacketType.Play.Server.SPAWN_ENTITY_LIVING, ConnectionSide.SERVER_SIDE)
                .subscribe(packetEvent -> {
                    UUID playerUniqueId = packetEvent.getPlayer().getUniqueId();
                    playerPreferencesDataManager.getPlayerVisibilityState(playerUniqueId)
                            .take(1)
                            .subscribe(playerVisibilityState -> {
                                WrapperPlayServerSpawnEntityLiving wrapperPlayServerSpawnEntityLiving = new WrapperPlayServerSpawnEntityLiving(packetEvent.getPacket());
                                if (wrapperPlayServerSpawnEntityLiving.getEntityID() < 0) {
                                    return;
                                }

                                if (playerVisibilityState == PlayerVisibilityState.NONE) {
                                    packetEvent.setCancelled(true);
                                } else if (playerVisibilityState == PlayerVisibilityState.TEAM) {
                                    UUID playerUUID = wrapperPlayServerSpawnEntityLiving.getUniqueId();

                                    playerGroupDataManager.getPlayerGroup(playerUUID).take(1)
                                            .subscribe(group -> {
                                                if (group.getId() < 10) {
                                                    packetEvent.setCancelled(true);
                                                }
                                            });
                                }
                            });
                });
    }

    /**
     * Handle the join of a player.
     *
     * @param player The player.
     */
    public void handlePlayerJoin(Player player) {
        player.getInventory().setItem(preferencesItemSlot, preferencesItemStack);

        playerPreferencesDataManager.getPlayerVisibilityState(player.getUniqueId())
                .takeUntil(rxManager.observeEvent(PlayerQuitEvent.class).take(1).filter(playerQuitEvent -> playerQuitEvent.getPlayer() == player))
                .subscribeOn(Schedulers.computation())
                .subscribe(playerVisibilityState -> Bukkit.getOnlinePlayers().forEach(currentPlayer -> plugin.getServer().getScheduler().runTask(plugin, () -> {
                    player.hidePlayer(currentPlayer);
                    player.showPlayer(currentPlayer);
                })));
    }

    /**
     * Handle the quit of a player.
     *
     * @param player The player.
     */
    public void handlePlayerQuit(Player player) {
        player.getInventory().remove(preferencesItemStack);
    }

    /**
     * open the preferences menu for the given player.
     *
     * @param player The player.
     */
    public void openPreferencesMenu(Player player) {
        Menu preferencesMenu = preferencesMenuFactory.createPreferencesMenu(player);
        preferencesMenu.open();
    }

    /**
     * Open the scoreboard preferences menu for the given player.
     *
     * @param player The player to open the inventory for.
     */
    public void openScoreboardPreferencesMenu(Player player) {
        Menu scoreboardPreferencesMenu = scoreboardPreferencesMenuFactory.createScoreboardPreferencesMenu(player);
        scoreboardPreferencesMenu.open();
    }

    /**
     * Open the visibility preferences menu for the given player.
     *
     * @param player The player to open the inventory for.
     */
    public void openVisibilityPreferencesMenu(Player player) {
        Menu visibilityMenu = visibilityMenuFactory.createVisibilityMenu(player);
        visibilityMenu.open();
    }

    /**
     * Handle that the player clicked that he wants to activate the scoreboard.
     *
     * @param player The player.
     */
    public void handleActivateScoreboardClicked(Player player) {
        playerPreferencesDataManager.getPlayerPreferences(player.getUniqueId()).take(1).subscribe(playerPreferences -> {
            playerPreferences.setScoreboardEnabled(true);
            playerPreferencesDataManager.updatePlayerPreferences(player.getUniqueId(), playerPreferences);
        });
    }

    /**
     * Handle that the player clicked that he wants to deactivate the scoreboard.
     *
     * @param player The player.
     */
    public void handleDeactivateScoreboardClock(Player player) {
        playerPreferencesDataManager.getPlayerPreferences(player.getUniqueId()).take(1).subscribe(playerPreferences -> {
            playerPreferences.setScoreboardEnabled(false);
            playerPreferencesDataManager.updatePlayerPreferences(player.getUniqueId(), playerPreferences);
        });
    }

    /**
     * Handle that the given player clicked for the given visibility.
     *
     * @param player                The player.
     * @param playerVisibilityState The clicked visibility state.
     */
    public void handleVisibilityClicked(Player player, PlayerVisibilityState playerVisibilityState) {
        playerPreferencesDataManager.getPlayerPreferences(player.getUniqueId()).take(1).subscribe(playerPreferences -> {
            if (playerPreferences.getPlayerVisibilityState() == playerVisibilityState) {
                player.sendMessage(prefix + "Zwei mal den gleichen Status zu setzen ist wie ein aufgegessenes Butterbrot zu essen.");
                return;
            }

            PlayerVisibilityStateChangeEvent playerVisibilityStateChangeEvent = new PlayerVisibilityStateChangeEvent(player, playerPreferences.getPlayerVisibilityState(), playerVisibilityState);
            pluginManager.callEvent(playerVisibilityStateChangeEvent);

            if (playerVisibilityStateChangeEvent.isCancelled()) {
                return;
            }

            playerPreferences.setPlayerVisibilityState(playerVisibilityState);
            playerPreferencesDataManager.updatePlayerPreferences(player.getUniqueId(), playerPreferences);
        });
    }
    /**
     * Handle that the given player changed his visibility to the given state.
     *
     * @param player             The player.
     * @param newVisibilityState The state.
     */
    public void handlePlayerVisibilityChange(Player player, PlayerVisibilityState newVisibilityState) {
        player.sendMessage(prefix + "Dein neuer Sichtbarkeitsstatus: " + (newVisibilityState == PlayerVisibilityState.ALL ? "Alle sichtbar" : newVisibilityState == PlayerVisibilityState.TEAM ? "Besondere Schneeflocken sichtbar" : "Niemand sichtbar"));
    }
}
