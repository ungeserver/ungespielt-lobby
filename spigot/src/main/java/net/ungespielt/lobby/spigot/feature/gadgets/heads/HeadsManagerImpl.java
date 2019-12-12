package net.ungespielt.lobby.spigot.feature.gadgets.heads;

import com.google.common.collect.Maps;
import net.ungespielt.lobby.spigot.api.event.PlayerHeadApplyEvent;
import net.ungespielt.lobby.spigot.api.event.PlayerHeadRemoveEvent;
import net.ungespielt.lobby.spigot.api.feature.gadgets.heads.Head;
import net.ungespielt.lobby.spigot.api.feature.gadgets.heads.HeadsManager;
import net.ungespielt.lobby.spigot.api.rx.RxManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;

/**
 * The default implementation of the {@link HeadsManager}.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
@Singleton
public class HeadsManagerImpl implements HeadsManager {

    /**
     * All currently active heads.
     */
    private final Map<Player, Head> currentHeads = Maps.newConcurrentMap();

    /**
     * The factory for all heads.
     */
    private final HeadFactory headFactory;

    /**
     * The rx manager.
     */
    private final RxManager rxManager;

    /**
     * The bukkit plugin manager.
     */
    private final PluginManager pluginManager;

    /**
     * Create a new heads manager.
     * @param headFactory The head factory.
     * @param rxManager   The rx manager.
     * @param pluginManager The plugin manager.
     */
    @Inject
    public HeadsManagerImpl(HeadFactory headFactory, RxManager rxManager, PluginManager pluginManager) {
        this.headFactory = headFactory;
        this.rxManager = rxManager;
        this.pluginManager = pluginManager;
    }

    @Override
    public Head applyHead(Player player, ItemStack itemStack) {
        Head head = headFactory.createHead(player, itemStack);

        PlayerHeadApplyEvent playerHeadApplyEvent = new PlayerHeadApplyEvent(player, head);
        pluginManager.callEvent(playerHeadApplyEvent);

        if (playerHeadApplyEvent.isCancelled()) {
            return null;
        }

        currentHeads.put(player, head);

        head.hatOn();
        startObserving(player, head);
        return head;
    }

    /**
     * Start observing the head lifecycle.
     *
     * @param player The player.
     * @param head   The head.
     */
    private void startObserving(Player player, Head head) {
        rxManager.observeEvent(InventoryClickEvent.class)
                .take(1)
                .filter(inventoryClickEvent -> inventoryClickEvent.getRawSlot() == 5)
                .filter(inventoryClickEvent -> inventoryClickEvent.getClick() != ClickType.CREATIVE)
                .filter(inventoryClickEvent -> inventoryClickEvent.getClick() != ClickType.UNKNOWN)
                .subscribe(inventoryClickEvent -> {
                    inventoryClickEvent.setCancelled(true);
                    inventoryClickEvent.setResult(Event.Result.DENY);
                    removeHead(player);
                    player.updateInventory();
                });
    }

    @Override
    public boolean hasHead(Player player) {
        return currentHeads.containsKey(player);
    }

    @Override
    public boolean removeHead(Player player) {
        Head head = currentHeads.remove(player);

        if (head != null) {
            PlayerHeadRemoveEvent playerHeadRemoveEvent = new PlayerHeadRemoveEvent(player, head);
            pluginManager.callEvent(playerHeadRemoveEvent);
            head.hatOff();
        }

        return head != null;
    }
}
