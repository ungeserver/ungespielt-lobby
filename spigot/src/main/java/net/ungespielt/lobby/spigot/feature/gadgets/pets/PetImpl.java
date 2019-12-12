package net.ungespielt.lobby.spigot.feature.gadgets.pets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ConnectionSide;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.google.common.collect.Sets;
import com.google.inject.assistedinject.Assisted;
import de.jackwhite20.base.api.spigot.ReflectionUtils;
import io.reactivex.subjects.PublishSubject;
import net.minecraft.server.v1_12_R1.*;
import net.ungespielt.lobby.spigot.api.feature.gadgets.pets.Pet;
import net.ungespielt.lobby.spigot.api.packetwrapper.WrapperPlayClientSteerVehicle;
import net.ungespielt.lobby.spigot.api.packetwrapper.WrapperPlayServerEntityDestroy;
import net.ungespielt.lobby.spigot.api.packetwrapper.WrapperPlayServerSpawnEntityLiving;
import net.ungespielt.lobby.spigot.api.rx.RxManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import javax.inject.Inject;
import javax.inject.Named;
import java.lang.reflect.Field;
import java.util.concurrent.Future;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class PetImpl implements Pet {

    private static final String GOALSELECTOR_FIELD_NAME = "b";
    private static final String TARGETSELECTOR_FIELD_NAME = "c";
    private static Field FIELD_GOALSELECTOR;
    private static Field FIELD_TARGETSELECTOR;

    static {
        try {
            FIELD_GOALSELECTOR = PathfinderGoalSelector.class
                    .getDeclaredField(GOALSELECTOR_FIELD_NAME);
            FIELD_GOALSELECTOR.setAccessible(true);
            FIELD_TARGETSELECTOR = PathfinderGoalSelector.class
                    .getDeclaredField(TARGETSELECTOR_FIELD_NAME);
            FIELD_TARGETSELECTOR.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    /**
     * The player.
     */
    private final Player player;

    /**
     * The type of the pet.
     */
    private final EntityType entityType;

    /**
     * The pet entity.
     */
    private Entity entity;

    /**
     * The rx manager.
     */
    private final RxManager rxManager;

    /**
     * The publisher for the destruction.
     */
    private final PublishSubject<Player> destructionPublisher = PublishSubject.create();

    /**
     * The nms entity insentient.
     */
    private EntityInsentient insentient;

    /**
     * If the pet is currently ridden.
     */
    private boolean riding;

    /**
     * The bukkit plugin.
     */
    private final Plugin plugin;

    /**
     * The min distance a player and a pet should have before the pet is teleported.
     */
    private final int petMinTeleportDistance;

    /**
     * The min distance a pet needs to his owner before it begins moving.
     */
    private final int petMinWalkDistance;

    @Inject
    public PetImpl(@Assisted Player player, @Assisted EntityType entityType, @Assisted boolean adult, RxManager rxManager, Plugin plugin, @Named("petMinTeleportDistance") int petMinTeleportDistance, @Named("petMinWalkDistance") int petMinWalkDistance) {
        this.player = player;
        this.entityType = entityType;
        this.rxManager = rxManager;
        this.plugin = plugin;
        this.petMinTeleportDistance = petMinTeleportDistance;
        this.petMinWalkDistance = petMinWalkDistance;
    }

    @Override
    public void initialize() {
        Future<Entity> future = Bukkit.getScheduler().callSyncMethod(plugin, () -> {
            entity = player.getWorld().spawnEntity(player.getLocation(), entityType);
            insentient = (EntityInsentient) ReflectionUtils.getHandle(entity);

            entity.setCustomName("Haustier von " + player.getName());

            Pathfinder pathfinder = new Pathfinder();
            pathfinder.clearNativeGoals();
            pathfinder.setupCustomGoals();

            return entity;
        });

        rxManager.observeEvent(EntityDamageEvent.class)
                .takeUntil(destructionPublisher)
                .filter(entityDamageEvent -> entityDamageEvent.getEntity() == future.get())
                .subscribe(entityDamageEvent -> {
                    entityDamageEvent.setCancelled(true);
                    entityDamageEvent.setDamage(0);
                });

        rxManager.observeEvent(PlayerInteractAtEntityEvent.class)
                .takeUntil(destructionPublisher)
                .filter(playerInteractAtEntityEvent -> playerInteractAtEntityEvent.getPlayer() == player)
                .filter(playerInteractAtEntityEvent -> playerInteractAtEntityEvent.getRightClicked() == future.get())
                .filter(playerInteractAtEntityEvent -> !riding)
                .subscribe(playerInteractAtEntityEvent -> startRiding());

        rxManager.observePacket(PacketType.Play.Client.STEER_VEHICLE, ConnectionSide.CLIENT_SIDE)
                .takeUntil(packetEvent -> future.get() == null || future.get().isDead())
                .filter(packetEvent -> packetEvent.getPlayer() == player)
                .map(packetEvent -> new WrapperPlayClientSteerVehicle(packetEvent.getPacket()))
                .subscribe(this::handleSteering);

        rxManager.observeEvent(EntityCombustEvent.class)
                .takeUntil(destructionPublisher)
                .filter(entityCombustEvent -> entityCombustEvent.getEntity() == entity)
                .subscribe(entityCombustEvent -> {
                    entityCombustEvent.setCancelled(true);
                    entityCombustEvent.setDuration(0);
                });
    }

    /**
     * Start riding of the pet.
     */
    private void startRiding() {
        entity.addPassenger(player);
        riding = true;
    }

    /**
     * Handle steering by the player.e
     *
     * @param wrapperPlayClientSteerVehicle The wrapper around the steer packet.
     */
    private void handleSteering(WrapperPlayClientSteerVehicle wrapperPlayClientSteerVehicle) {
        if (wrapperPlayClientSteerVehicle.isUnmount()) {
            stopRiding();
            return;
        }

        Vector vector = new Vector(wrapperPlayClientSteerVehicle.getSideways(), wrapperPlayClientSteerVehicle.isJump() ? 3 : 0, wrapperPlayClientSteerVehicle.getForward() * 1.5);
        vector = rotateVectorAroundY(vector, player.getLocation().getYaw());
        vector = vector.multiply(0.2);

        if (entity.isOnGround()) {
            entity.setVelocity(vector);
            insentient.yaw = player.getEyeLocation().getYaw();
        }
    }

    /**
     * Stop the riding.
     */
    private void stopRiding() {
        player.leaveVehicle();
        riding = false;
    }

    /**
     * Rotate the vector around y by degrees.
     *
     * @param vector  The vector.
     * @param degrees The degrees.
     * @return The vector.
     */
    private Vector rotateVectorAroundY(Vector vector, double degrees) {
        double rad = Math.toRadians(degrees);

        double currentX = vector.getX();
        double currentZ = vector.getZ();

        double cosine = Math.cos(rad);
        double sine = Math.sin(rad);

        return new Vector((cosine * currentX - sine * currentZ), vector.getY(), (sine * currentX + cosine * currentZ));
    }

    @Override
    public void destroy() {
        stopRiding();

        entity.remove();

        destructionPublisher.onNext(player);
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

        WrapperPlayServerSpawnEntityLiving wrapperPlayServerSpawnEntityLiving = new WrapperPlayServerSpawnEntityLiving();
        wrapperPlayServerSpawnEntityLiving.setUniqueId(entity.getUniqueId());
        wrapperPlayServerSpawnEntityLiving.setMetadata(WrappedDataWatcher.getEntityWatcher(entity));
        wrapperPlayServerSpawnEntityLiving.setEntityID(entity.getEntityId());
        wrapperPlayServerSpawnEntityLiving.setX(entity.getLocation().getX());
        wrapperPlayServerSpawnEntityLiving.setY(entity.getLocation().getY());
        wrapperPlayServerSpawnEntityLiving.setZ(entity.getLocation().getZ());
        wrapperPlayServerSpawnEntityLiving.setPitch(entity.getLocation().getPitch());
        wrapperPlayServerSpawnEntityLiving.setYaw(entity.getLocation().getYaw());
        wrapperPlayServerSpawnEntityLiving.setType(entity.getType());
        wrapperPlayServerSpawnEntityLiving.sendPacket(player);
    }

    /**
     * The path finder goal that lets the pet follow the player.
     */
    public class PathfinderGoalFollowPlayer extends PathfinderGoal {

        @Override
        public void c() {
            Location location = calculateTargetLocation();
            PathEntity pathEntity = insentient.getNavigation()
                    .a(location.getBlockX(), location.getBlockY(), location.getBlockZ());

            double speed = player.isSprinting() ? 3.0 : 2.0;

            insentient.getNavigation().a(pathEntity, speed);
        }

        @Override
        public boolean a() {
            double distance = player.getLocation().distance(entity.getLocation());

            if (distance < petMinWalkDistance) {
                return false;
            }

            if (distance > petMinTeleportDistance && player.isOnGround()) {
                entity.teleport(player);
                return false;
            }

            c();
            return true;
        }

        /**
         * Used for Entity Movement. Returns the location exactly between the both Locations.
         *
         * @param scale The scale factor of the movement vector.
         * @return The point between
         */
        Location calculateTargetLocation(double scale) {
            final Vector vectorSource = entity.getLocation().toVector();
            final Vector vectorTarget = player.getLocation().toVector();

            final Vector movementVector = vectorTarget.subtract(vectorSource);
            return entity.getLocation().add(movementVector);
        }

        /**
         * Used for Entity Movement. Returns the location exactly between the both Locations with a
         * scale of 0.5.
         *
         * @return The point between
         */
        Location calculateTargetLocation() {
            return calculateTargetLocation(0.5);
        }
    }

    class Pathfinder {

        void clearNativeGoals() {
            try {
                FIELD_GOALSELECTOR.set(insentient.goalSelector, Sets.newLinkedHashSet());
                FIELD_GOALSELECTOR.set(insentient.targetSelector, Sets.newLinkedHashSet());
                FIELD_TARGETSELECTOR.set(insentient.goalSelector, Sets.newLinkedHashSet());
                FIELD_TARGETSELECTOR.set(insentient.targetSelector, Sets.newLinkedHashSet());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        void setupCustomGoals() {
            insentient.goalSelector
                    .a(0, new PathfinderGoalLookAtPlayer(insentient, EntityHuman.class, 6.0F));
            insentient.goalSelector.a(1, new PathfinderGoalRandomLookaround(insentient));
            insentient.goalSelector.a(2, new PathfinderGoalFloat(insentient));
            insentient.goalSelector.a(3, new PathfinderGoalFollowPlayer());

            if (insentient instanceof EntityCreature) {
                insentient.goalSelector.a(4,
                        new PathfinderGoalRandomStroll((EntityCreature) insentient, 1.0D));
            }
        }
    }

    @Override
    public Player getPlayer() {
        return player;
    }
}
