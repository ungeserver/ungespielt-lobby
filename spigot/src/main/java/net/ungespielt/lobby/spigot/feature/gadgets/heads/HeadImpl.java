package net.ungespielt.lobby.spigot.feature.gadgets.heads;

import com.google.inject.assistedinject.Assisted;
import io.reactivex.subjects.PublishSubject;
import net.ungespielt.lobby.spigot.api.feature.gadgets.heads.Head;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import javax.inject.Inject;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class HeadImpl implements Head {

    /**
     * The player.
     */
    private final Player player;

    /**
     * The new head of the player.
     */
    private final ItemStack head;

    /**
     * The observable of the player hatting off.
     */
    private final PublishSubject<Player> hatOffPublisher = PublishSubject.create();

    /**
     * Create a new head.
     *
     * @param player The player.
     * @param head   the item of the head.
     */
    @Inject
    public HeadImpl(@Assisted Player player, @Assisted ItemStack head) {
        this.player = player;
        this.head = head;
    }

    @Override
    public void hatOn() {
        player.getInventory().setHelmet(head);
    }

    @Override
    public void hatOff() {
        player.getInventory().remove(head);
        player.getInventory().setHelmet(new ItemStack(Material.AIR));
        player.updateInventory();

        hatOffPublisher.onNext(player);
    }

    @Override
    public ItemStack getHead() {
        return head;
    }

    @Override
    public String getOwner() {
        return ((SkullMeta) head.getItemMeta()).getOwner();
    }
}
