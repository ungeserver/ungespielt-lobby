package net.ungespielt.lobby.spigot.feature.scoreboard;

import de.jackwhite20.base.api.service.permission.Group;
import de.jackwhite20.base.api.service.permission.Permissions;
import de.jackwhite20.base.api.spigot.scoreboard.ScoreboardService;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import net.ungespielt.lobby.shared.PlayerPreferences;
import net.ungespielt.lobby.spigot.api.feature.FeatureViewModel;
import net.ungespielt.lobby.spigot.data.PlayerCoinsDataManager;
import net.ungespielt.lobby.spigot.data.PlayerGroupDataManager;
import net.ungespielt.lobby.spigot.data.PlayerPreferencesDataManager;
import net.ungespielt.social.api.SocialService;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.DisplaySlot;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * View model of {@link ScoreboardController}.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
@Singleton
public class ScoreboardViewModel extends FeatureViewModel {

    /**
     * The scoreboard service.
     */
    private final ScoreboardService scoreboardService;

    /**
     * The display name of the scoreboard.
     */
    private final String scoreboardDisplayName;

    /**
     * The data manager of a players group.
     */
    private final PlayerGroupDataManager playerGroupDataManager;

    /**
     * The data manager of a players coins.
     */
    private final PlayerCoinsDataManager playerCoinsDataManager;

    /**
     * The short version of the server id.
     */
    private final String shortServerId;

    /**
     * The logger to log all actions.
     */
    private final Logger logger;

    /**
     * The data manager for the players preferences.
     */
    private final PlayerPreferencesDataManager playerPreferencesDataManager;

    /**
     * The social service.
     */
    private final SocialService socialService;

    /**
     * Create a new scoreboard view model.
     *  @param scoreboardService            The scoreboard service.
     * @param scoreboardDisplayName        The display name of the scoreboard.
     * @param playerGroupDataManager       The data manager of a players group.
     * @param playerCoinsDataManager       The players coins data manager.
     * @param shortServerId                The short version of the server id.
     * @param plugin                       The bukkit plugin.
     * @param playerPreferencesDataManager The data manager for the players preferences.
     * @param socialService                 The social service.
     */
    @Inject
    public ScoreboardViewModel(ScoreboardService scoreboardService, @Named("scoreboardDisplayName") String scoreboardDisplayName, PlayerGroupDataManager playerGroupDataManager, PlayerCoinsDataManager playerCoinsDataManager, @Named("shortServerId") String shortServerId, Plugin plugin, PlayerPreferencesDataManager playerPreferencesDataManager, SocialService socialService) {
        this.scoreboardService = scoreboardService;
        this.scoreboardDisplayName = scoreboardDisplayName;
        this.playerGroupDataManager = playerGroupDataManager;
        this.playerCoinsDataManager = playerCoinsDataManager;
        this.shortServerId = shortServerId;
        this.logger = plugin.getLogger();
        this.playerPreferencesDataManager = playerPreferencesDataManager;
        this.socialService = socialService;
    }

    /**
     * Handle a player join.
     *
     * @param player The player.
     */
    public void handlePlayerJoin(Player player) {
        playerPreferencesDataManager.getPlayerPreferences(player.getUniqueId())
                .takeUntil(playerPreferences -> !player.isOnline())
                .observeOn(Schedulers.computation())
                .subscribeOn(Schedulers.computation())
                .subscribe(playerPreferences -> {
                    if (playerPreferences.isScoreboardEnabled() && scoreboardService.getScoreboard(player.getName()) == null) {
                        createScoreboard(player);
                    } else if (!playerPreferences.isScoreboardEnabled() && scoreboardService.getScoreboard(player.getName()) != null) {
                        removeScoreboard(player);
                    }
                });
    }

    /**
     * Create a new scoreboard for the given player.
     *
     * @param player The player.
     */
    private void createScoreboard(Player player) {
        logger.log(Level.FINE, "Setting up scoreboard for player " + player.getName() + ".");

        scoreboardService.registerScoreboard(player.getName(), player.getName());
        scoreboardService.setDisplayName(scoreboardDisplayName, player.getName());
        scoreboardService.setDisplaySlot(DisplaySlot.SIDEBAR, player.getName());

        // Username
        scoreboardService.addScore("§1", 15, player.getName());
        scoreboardService.addScore("§6Username:", 14, player.getName());
        scoreboardService.addScore(ChatColor.GRAY + " » " + player.getName(), 13, player.getName());

        // Rang
        scoreboardService.addScore("§2", 12, player.getName());
        scoreboardService.addScore("§6Rang: ", 11, player.getName());

        // Coins
        scoreboardService.addScore("§3", 9, player.getName());
        scoreboardService.addScore("§6Coins:", 8, player.getName());

        // Server
        scoreboardService.addScore("§4", 6, player.getName());
        scoreboardService.addScore("§6Server:", 5, player.getName());
        scoreboardService.addScore(ChatColor.GRAY + " » " + shortServerId, 4, player.getName());

        initializeSubscriptions(player);

        scoreboardService.send(player, player.getName());
    }

    /**
     * Subscribe to the changing values of the scoreboard.
     *
     * @param player The player.
     */
    private void initializeSubscriptions(Player player) {
        Observable<PlayerPreferences> playerPreferencesObservable = playerPreferencesDataManager.getPlayerPreferences(player.getUniqueId())
                .filter(playerPreferences -> !playerPreferences.isScoreboardEnabled());

        playerGroupDataManager.getPlayerGroup(player.getUniqueId())
                .takeWhile(group -> scoreboardService.getScoreboard(player.getName()) != null)
                .takeUntil(playerPreferencesObservable)
                .distinctUntilChanged()
                .startWith(new Group(0, "Spieler", "§7", "$r", "", new Permissions()))
                .buffer(2, 1)
                .map(list -> list.stream().map(group -> ChatColor.GRAY + " » " + group.getPrefix() + group.getName()).collect(Collectors.toList()))
                .subscribe(strings -> handleScoreUpdate(10, player, strings));

        playerCoinsDataManager.getPlayerCoins(player.getUniqueId())
                .takeWhile(group -> scoreboardService.getScoreboard(player.getName()) != null)
                .takeUntil(playerPreferencesObservable)
                .distinctUntilChanged()
                .startWith(0)
                .buffer(2, 1)
                .map(list -> list.stream().map(integer -> ChatColor.GRAY + " » " + integer).collect(Collectors.toList()))
                .subscribe(strings -> handleScoreUpdate(7, player, strings));
    }

    /**
     * Handle the update of a score.
     *
     * @param currentScore The current value.
     * @param player       The player.
     * @param strings      The updated strings.
     */
    private void handleScoreUpdate(int currentScore, Player player, List<String> strings) {
        if (scoreboardService.getScoreboard(player.getName()) == null) {
            return;
        }

        if (strings.size() == 2) {
            scoreboardService.removeScore(strings.get(0), player.getName());
            scoreboardService.addScore(strings.get(1), currentScore, player.getName());
        } else {
            scoreboardService.addScore(strings.get(0), currentScore, player.getName());
        }
    }

    /**
     * Handle a player quit.
     *
     * @param player The player.
     */
    public void handlePlayerQuit(Player player) {
        removeScoreboard(player);
    }

    /**
     * Remove the scoreboar for the given player.
     *
     * @param player The player.
     */
    private void removeScoreboard(Player player) {
        logger.log(Level.FINE, "Removing scoreboard for player " + player.getName() + ".");

        scoreboardService.remove(player, player.getName());
    }
}
