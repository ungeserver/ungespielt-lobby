package net.ungespielt.lobby.spigot.feature.mysticchests.reward;

import net.ungespielt.lobby.spigot.api.feature.mysticchests.reward.Reward;
import org.bukkit.entity.Player;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
public abstract class AbstractReward implements Reward {

    /**
     * If the reward was already applied.
     */
    private boolean applied;

    @Override
    public void applyTo(Player player) {
        if (!applied) {
            doApply(player);
        }

        applied = true;
    }

    /**
     * Apply the reward to the given player.
     *
     * @param player The player.
     */
    protected abstract void doApply(Player player);
}
