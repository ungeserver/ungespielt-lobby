package net.ungespielt.lobby.spigot.feature.welcome;

import de.jackwhite20.base.api.spigot.title.TitleService;
import net.md_5.bungee.api.chat.TextComponent;
import net.ungespielt.lobby.spigot.api.feature.FeatureViewModel;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * View Model for {@link WelcomeController}.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
@Singleton
public class WelcomeViewModel extends FeatureViewModel {

    /**
     * The title service.
     */
    private final TitleService titleService;

    /**
     * The id of the server.
     */
    private final String serverId;

    /**
     * Create a new welcome view model.
     *
     * @param titleService The title service.
     * @param serverId The server id.
     */
    @Inject
    public WelcomeViewModel(TitleService titleService, @Named("shortServerId") String serverId) {
        this.titleService = titleService;
        this.serverId = serverId;
    }

    /**
     * Handle a player join.
     *
     * @param player The player who joined.
     */
    public void handlePlayerJoin(Player player) {
        // Clear inventory to cleanup dirty shut down
        player.getInventory().clear();

        sendWelcomeTitle(player);

        // Teleport the player to world spawn. TODO: Extract spawn location to lobby config.
        player.teleport(player.getLocation().getWorld().getSpawnLocation());
    }

    /**
     * Welcome a player using title and sub title. Usually this titles should give a welcoming hello and tell the
     * Player which server he joined.
     * <p>
     * TODO: Add some particles to make joining much more funny.
     *
     * @param player The player.
     */
    private void sendWelcomeTitle(Player player) {
        TextComponent mainTitle = new TextComponent(ChatColor.GOLD + "Willkommen " + player.getDisplayName());
        TextComponent subTitle = new TextComponent(ChatColor.AQUA + "auf " + ChatColor.GREEN + serverId);

        titleService.sendTitle(player, 20, 60, 20, mainTitle, subTitle);
    }
}
