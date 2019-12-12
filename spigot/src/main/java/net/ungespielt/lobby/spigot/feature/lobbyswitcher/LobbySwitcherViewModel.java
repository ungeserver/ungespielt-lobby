package net.ungespielt.lobby.spigot.feature.lobbyswitcher;

import de.jackwhite20.base.api.service.player.PlayerService;
import de.jackwhite20.base.api.service.player.SendPolicy;
import de.jackwhite20.base.api.spigot.menu.impl.Menu;
import net.ungespielt.lobby.spigot.api.feature.FeatureViewModel;
import net.ungespielt.lobby.spigot.feature.lobbyswitcher.menu.LobbySwitcherMenuFactory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Objects;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class LobbySwitcherViewModel extends FeatureViewModel {

    /**
     * The factory for the lobby switcher menu.
     */
    private final LobbySwitcherMenuFactory lobbySwitcherMenuFactory;

    /**
     * The item stack for the lobby switcher.
     */
    private final ItemStack lobbySwitcherItemStack;

    /**
     * The player service.
     */
    private final PlayerService playerService;

    /**
     * The cluster server id of the current server.
     */
    private final String serverId;

    /**
     * The global message prefix.
     */
    private final String prefix;

    /**
     * The item slot for the lobby switcher.
     */
    private final int lobbySwitcherItemSlot;

    @Inject
    public LobbySwitcherViewModel(LobbySwitcherMenuFactory lobbySwitcherMenuFactory, @Named("lobbySwitcherItemStack") ItemStack lobbySwitcherItemStack, PlayerService playerService, @Named("serverId") String serverId, @Named("prefix") String prefix, @Named("lobbySwitcherItemSlot") int lobbySwitcherItemSlot) {
        this.lobbySwitcherMenuFactory = lobbySwitcherMenuFactory;
        this.lobbySwitcherItemStack = lobbySwitcherItemStack;
        this.playerService = playerService;
        this.serverId = serverId;
        this.prefix = prefix;
        this.lobbySwitcherItemSlot = lobbySwitcherItemSlot;
    }

    /**
     * Handle the join of a player.
     *
     * @param player The player.
     */
    public void handlePlayerJoin(Player player) {
        player.getInventory().setItem(lobbySwitcherItemSlot, lobbySwitcherItemStack);
    }

    /**
     * Handle the quit of a player.
     *
     * @param player The player.
     */
    public void handlePlayerQuit(Player player) {
        player.getInventory().remove(lobbySwitcherItemStack);
    }

    /**
     * Open the lobby switcher menu for the given player.
     *
     * @param player The player.
     */
    public void openLobbySwitcherMenu(Player player) {
        Menu menu = lobbySwitcherMenuFactory.createLobbySwitcherMenu(player);
        menu.open();
    }

    /**
     * Let the player switch to the given lobby.
     *
     * @param player The player.
     * @param id     The id of the lobby.
     */
    public void handleSwitchLobbyClicked(Player player, String id) {
        if (Objects.equals(serverId, id)) {
            player.sendMessage(prefix + "Du bist bereits auf dieser Lobby.");
            return;
        }

        playerService.send(player.getUniqueId(), id, SendPolicy.DIRECT);
    }
}
