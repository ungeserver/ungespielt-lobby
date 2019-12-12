package net.ungespielt.lobby.spigot.feature.mysticchests.menu.roll;

import com.google.inject.assistedinject.Assisted;
import de.jackwhite20.base.api.spigot.menu.impl.Menu;
import de.jackwhite20.base.spigot.menu.MenuImplementation;
import net.ungespielt.lobby.spigot.feature.mysticchests.MysticChestsViewModel;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collections;
import java.util.List;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class MysticChestRollMenu extends MenuImplementation implements Menu {

    /**
     * The deltas of the absolute position to generate a circle.
     */
    private static final int[] CIRCLE_DELTAS = {-10, -9, -8, -1, 1, 8, 9, 10};

    /**
     * The player.
     */
    private final Player player;

    /**
     * The view model.
     */
    private final MysticChestsViewModel mysticChestsViewModel;

    /**
     * The item stacks used to visualize the roll.
     */
    private final List<ItemStack> randomRollItemStacks;

    /**
     * The bukkit plugin.
     */
    private final Plugin plugin;

    /**
     * The item stacks that shows a complete roll cycle.
     */
    private final ItemStack rollCompleteItemStack;

    /**
     * The item stack for the border that says that the roll is running.
     */
    private final ItemStack randomRollRunningBorderItemStack;

    /**
     * The item stack for the border that says that the roll is stopped.
     */

    private final ItemStack randomRollStoppedBorderItemStack;

    /**
     * The task used for rolling.
     */
    private BukkitTask rollTask;

    @Inject
    public MysticChestRollMenu(@Assisted Player player, MysticChestsViewModel mysticChestsViewModel, @Named("randomRollItemStacks") List<ItemStack> randomRollItemStacks, Plugin plugin, @Named("rollCompleteItemStack") ItemStack rollCompleteItemStack, @Named("randomRollRunningBorderItemStack") ItemStack randomRollRunningBorderItemStack, @Named("randomRollStoppedBorderItemStack") ItemStack randomRollStoppedBorderItemStack) {
        super(player, "§6Mysticchests §9/ §6Kiste öffnen", 45);
        this.player = player;
        this.mysticChestsViewModel = mysticChestsViewModel;
        this.randomRollItemStacks = randomRollItemStacks;
        this.plugin = plugin;
        this.rollCompleteItemStack = rollCompleteItemStack;
        this.randomRollRunningBorderItemStack = randomRollRunningBorderItemStack;
        this.randomRollStoppedBorderItemStack = randomRollStoppedBorderItemStack;
    }

    @Override
    public void open() {
        super.open();

        startRoll();

        setPlayerCloseListener(player1 -> {
            if (rollTask != null) {
                rollTask.cancel();
            }

            return true;
        });
    }

    private void startRoll() {
        setCircleAround(22, randomRollRunningBorderItemStack);

        rollTask = new BukkitRunnable() {
            @Override
            public void run() {
                double i = 10;

                while (i < 1000) {
                    i = 1.3 * i;

                    try {
                        Thread.sleep((long) i);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    player.playSound(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1, 1);

                    Collections.shuffle(randomRollItemStacks);
                    setItem(22, randomRollItemStacks.get(0));
                    sendItem(22);
                }

                player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT, 1, 1);

                setCircleAround(22, randomRollStoppedBorderItemStack);

                setItem(22, rollCompleteItemStack, menuClickEvent -> {
                    close();
                    mysticChestsViewModel.handleRewardCycleComplete(player);
                });

                sendItem(22);
            }
        }.runTaskAsynchronously(plugin);
    }

    /**
     * Generate a border around the given position.
     *
     * @param pos       The position.
     * @param itemStack The item stack.
     */
    private void setCircleAround(int pos, ItemStack itemStack) {
        for (int circleDelta : CIRCLE_DELTAS) {
            setItem(pos + circleDelta, itemStack);
            sendItem(pos + circleDelta);
        }
    }
}
