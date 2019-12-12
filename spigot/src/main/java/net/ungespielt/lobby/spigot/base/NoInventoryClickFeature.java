package net.ungespielt.lobby.spigot.base;

import de.jackwhite20.base.api.spigot.feature.Feature;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class NoInventoryClickFeature extends Feature implements Listener {

    public NoInventoryClickFeature(String name) {
        super(name);
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {
        ClickType clickType = inventoryClickEvent.getClick();
        if (clickType != ClickType.CREATIVE) {
            inventoryClickEvent.setCancelled(true);
            inventoryClickEvent.setResult(Event.Result.DENY);
        }
    }
}
