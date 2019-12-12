package net.ungespielt.lobby.spigot.feature.mysticchests.reward;

import com.google.inject.Injector;
import net.ungespielt.lobby.spigot.api.feature.mysticchests.reward.Reward;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
@Singleton
public class RewardFactory {

    /**
     * The di injector.
     */
    private final Injector injector;

    @Inject
    public RewardFactory(Injector injector) {
        this.injector = injector;
    }

    /**
     * Create a random reward for the given player.
     *
     * @param player The player.
     * @return The reward.
     */
    public Reward createRandomReward(Player player) {
        return injector.getInstance(CoinsReward.class);
    }
}
