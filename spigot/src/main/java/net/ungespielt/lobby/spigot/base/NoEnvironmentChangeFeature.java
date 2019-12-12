package net.ungespielt.lobby.spigot.base;

import de.jackwhite20.base.api.spigot.feature.Feature;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class NoEnvironmentChangeFeature extends Feature implements Listener {

    public NoEnvironmentChangeFeature(String name) {
        super(name);
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    @EventHandler
    public void onBlockIgnite(BlockIgniteEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onBlockSpread(BlockSpreadEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onBlockGrow(BlockGrowEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onTrapdoor(PlayerInteractEvent playerInteractEvent) {
        Action action = playerInteractEvent.getAction();
        Block clickedBlock = playerInteractEvent.getClickedBlock();

        if (action == Action.RIGHT_CLICK_BLOCK && clickedBlock != null && clickedBlock.getType() == Material.TRAP_DOOR) {
            playerInteractEvent.setCancelled(true);
            playerInteractEvent.setUseInteractedBlock(Event.Result.DENY);
            playerInteractEvent.setUseItemInHand(Event.Result.DENY);
        } else if (action == Action.PHYSICAL) {
            playerInteractEvent.setCancelled(true);
        }
    }

    @EventHandler
    public void onExplosion(EntityExplodeEvent entityExplodeEvent) {
        entityExplodeEvent.setCancelled(true);
        entityExplodeEvent.blockList().clear();
    }
}
