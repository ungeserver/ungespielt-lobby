package net.ungespielt.lobby.spigot.feature.gadgets.morphs;

import com.comphenix.packetwrapper.WrapperPlayServerEntityMetadata;
import com.comphenix.packetwrapper.WrapperPlayServerNamedEntitySpawn;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ConnectionSide;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.google.inject.assistedinject.Assisted;
import io.reactivex.subjects.PublishSubject;
import net.ungespielt.lobby.spigot.api.feature.gadgets.morphs.Morph;
import net.ungespielt.lobby.spigot.api.packetwrapper.WrapperPlayServerEntityDestroy;
import net.ungespielt.lobby.spigot.api.packetwrapper.WrapperPlayServerSpawnEntityLiving;
import net.ungespielt.lobby.spigot.api.rx.RxManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import javax.inject.Inject;
import java.util.UUID;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class MorphImpl implements Morph {

    /**
     * The player.
     */
    private final Player player;

    /**
     * The type of the morph.
     */
    private final EntityType entityType;

    /**
     * The observable of the player unmorphing himself.
     */
    private final PublishSubject<Player> unmorphPublisher = PublishSubject.create();

    /**
     * The rx manager.
     */
    private final RxManager rxManager;

    /**
     * The plugin needed for running tasks.
     */
    private final Plugin plugin;

    /**
     * The data watcher.
     */
    private final WrappedDataWatcher wrappedDataWatcher = new WrappedDataWatcher();
    /**
     * The current bossbar that shows the player which morph he is using.
     */
    private BossBar bossBar;

    @Inject
    public MorphImpl(@Assisted Player player, @Assisted EntityType entityType, RxManager rxManager, Plugin plugin) {
        this.player = player;
        this.entityType = entityType;
        this.rxManager = rxManager;
        this.plugin = plugin;
    }

    @Override
    public void initialize() {
        bossBar = Bukkit.createBossBar(ChatColor.GOLD + "Aktiver Morph: " + ChatColor.DARK_PURPLE + entityType.getName(),
                BarColor.BLUE, BarStyle.SOLID);
        bossBar.addPlayer(player);

        initDataWatcher();
        startObserving();
        refreshPlayer(player);
    }

    /**
     * Begin observing packets to modify meta data stuff.
     */
    private void startObserving() {
        rxManager.observePacket(PacketType.Play.Server.NAMED_ENTITY_SPAWN, ConnectionSide.SERVER_SIDE)
                .takeUntil(unmorphPublisher)
                .filter(packetEvent -> {
                    WrapperPlayServerNamedEntitySpawn wrapperPlayServerNamedEntitySpawn = new WrapperPlayServerNamedEntitySpawn(packetEvent.getPacket());
                    UUID playerUUID = wrapperPlayServerNamedEntitySpawn.getPlayerUUID();
                    Player target = Bukkit.getPlayer(playerUUID);
                    return target != null && playerUUID == player.getUniqueId() && wrapperPlayServerNamedEntitySpawn.getEntityID() == player.getEntityId();
                })
                .subscribe(packetEvent -> {
                    WrapperPlayServerSpawnEntityLiving wrapperPlayServerSpawnEntityLiving = getSpawnPacket();
                    packetEvent.setCancelled(true);
                    wrapperPlayServerSpawnEntityLiving.sendPacket(packetEvent.getPlayer());
                });

        rxManager.observePacket(PacketType.Play.Server.ENTITY_METADATA, ConnectionSide.SERVER_SIDE)
                .takeUntil(unmorphPublisher)
                .filter(packetEvent -> {
                    WrapperPlayServerEntityMetadata wrapperPlayServerEntityMetadata = new WrapperPlayServerEntityMetadata(packetEvent.getPacket());
                    return wrapperPlayServerEntityMetadata.getEntityID() != packetEvent.getPlayer().getEntityId();
                })
                .subscribe(packetEvent -> {
                    WrapperPlayServerEntityMetadata wrapperPlayServerEntityMetadata = new WrapperPlayServerEntityMetadata(packetEvent.getPacket());
                    Entity entity = wrapperPlayServerEntityMetadata.getEntity(packetEvent);
                    if (entity != null && entity instanceof Player) {
                        wrapperPlayServerEntityMetadata.setMetadata(wrappedDataWatcher.getWatchableObjects());
                        packetEvent.setPacket(wrapperPlayServerEntityMetadata.getHandle());
                    }
                });
    }

    /**
     * Initialize that data watcher with the players name.
     */
    private void initDataWatcher() {
        wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(2, WrappedDataWatcher.Registry.get(String.class)), player.getName());
        wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(3, WrappedDataWatcher.Registry.get(Boolean.class)), true);
    }

    /**
     * Get the packet that will spawn this morph.
     *
     * @return The packet wrapper.
     */
    private WrapperPlayServerSpawnEntityLiving getSpawnPacket() {
        WrapperPlayServerSpawnEntityLiving wrapperSpawn = new WrapperPlayServerSpawnEntityLiving();
        wrapperSpawn.setType(entityType);
        wrapperSpawn.setUniqueId(player.getUniqueId());
        wrapperSpawn.setMetadata(wrappedDataWatcher);
        wrapperSpawn.setEntityID(player.getEntityId());
        wrapperSpawn.setX(player.getLocation().getX());
        wrapperSpawn.setY(player.getLocation().getY());
        wrapperSpawn.setZ(player.getLocation().getZ());

        return wrapperSpawn;
    }

    @Override
    public void destroy() {
        unmorphPublisher.onNext(player);

        refreshPlayer(player);
        bossBar.removePlayer(player);
    }

    /**
     * Refresh the view on this player for all other players.
     *
     * @param player The player.
     */
    private void refreshPlayer(Player player) {
        if (Bukkit.isPrimaryThread()) {
            for (Player player1 : Bukkit.getOnlinePlayers()) {
                player1.hidePlayer(player);
                player1.showPlayer(player);
            }

            return;
        }

        Bukkit.getScheduler().runTask(plugin, () -> {
            for (Player player1 : Bukkit.getOnlinePlayers()) {
                player1.hidePlayer(player);
                player1.showPlayer(player);
            }
        });
    }

    @Override
    public EntityType getEntityType() {
        return entityType;
    }

    @Override
    public void refresh(Player player) {
        WrapperPlayServerEntityDestroy wrapperPlayServerEntityDestroy = new WrapperPlayServerEntityDestroy();
        wrapperPlayServerEntityDestroy.setEntityIds(new int[]{player.getEntityId()});
        wrapperPlayServerEntityDestroy.sendPacket(player);

        getSpawnPacket().sendPacket(player);
    }

    @Override
    public Player getPlayer() {
        return player;
    }
}
