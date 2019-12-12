package net.ungespielt.lobby.spigot.feature.mysticchests.reward;

import de.jackwhite20.base.api.service.player.PlayerService;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Random;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class CoinsReward extends AbstractReward {

    /**
     * The random instance to calculate coins.
     */
    private static final Random RANDOM = new Random();

    /**
     * The amount of coins the player will get.
     */
    private final int coinsAmount = Math.abs((int) (500 + RANDOM.nextGaussian() * 450));

    /**
     * The player service.
     */
    private final PlayerService playerService;

    /**
     * The global message prefix.
     */
    private final String prefix;

    /**
     * Create a new coins reward.
     *
     * @param playerService The player service.
     * @param prefix        The global message prefix.
     */
    @Inject
    public CoinsReward(PlayerService playerService, @Named("prefix") String prefix) {
        this.playerService = playerService;
        this.prefix = prefix;
    }

    @Override
    protected void doApply(Player player) {
        int coins = playerService.manipulateCoins(player.getUniqueId(), coinsAmount);
        player.sendMessage(prefix + "Du hast " + coinsAmount + " Coins gewonnen. Du hast nun " + coins + " Coins ;)");
    }
}
