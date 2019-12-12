package net.ungespielt.lobby.spigot.feature.doublejump;

import com.google.common.collect.Sets;
import de.jackwhite20.base.api.spigot.particle.ParticleService;
import de.jackwhite20.base.api.spigot.particle.ParticleType;
import fr.neatmonster.nocheatplus.checks.CheckType;
import fr.neatmonster.nocheatplus.components.NoCheatPlusAPI;
import fr.neatmonster.nocheatplus.hooks.NCPExemptionManager;
import net.ungespielt.lobby.spigot.api.feature.FeatureViewModel;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Set;
import java.util.UUID;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class DoubleJumpViewModel extends FeatureViewModel {

    /**
     * The permission a player needs to perform a double jump.
     */
    private static final String PERMISSION_DOUBLE_JUMP = "lobby.jump.double";

    /**
     * The permission a player needs to perform a double jump.
     */
    private static final String PERMISSION_TRIPLE_JUMP = "lobby.jump.triple";

    /**
     * Contains all player unique ids for the players in the double jump state.
     */
    private final Set<UUID> currentDoubleJumps = Sets.newConcurrentHashSet();

    /**
     * Contains all player unique ids for the players in the triple jump state.
     */
    private final Set<UUID> currentTripleJumps = Sets.newConcurrentHashSet();

    /**
     * The bukkit plugin instance.
     */
    private final Plugin plugin;

    /**
     * Particle server to spice up the double jump.
     */
    private final ParticleService particleService;

    /**
     * The elytra used for triple jump.
     */
    private final ItemStack doubleJumpElytraItemStack;

    /**
     * The no cheat plu api to exempt player from being reset while jumping.
     */
    private final NoCheatPlusAPI noCheatPlusAPI;

    @Inject
    public DoubleJumpViewModel(Plugin plugin, ParticleService particleService,
                               @Named("doubleJumpElytraItemStack") ItemStack doubleJumpElytraItemStack, NoCheatPlusAPI noCheatPlusAPI) {
        this.plugin = plugin;
        this.particleService = particleService;
        this.doubleJumpElytraItemStack = doubleJumpElytraItemStack;
        this.noCheatPlusAPI = noCheatPlusAPI;
    }

    /**
     * Handle that the given player joined the server. We have to allow flying here.
     *
     * @param player The player.
     */
    public void handlePlayerJoin(Player player) {
        player.setAllowFlight(true);
    }

    /**
     * Handle that the given player changed his game mode. We have to re allow flying here as the initial setting
     * of {@link #handlePlayerJoin(Player)} wont apply anymore.
     *
     * @param player The player.
     */
    public void handlePlayerGameModeChange(Player player) {
        player.setAllowFlight(true);
    }

    /**
     * Handle that the given player toggled his fly state.
     *
     * @param player The player.
     * @param flying The fly state.
     */
    public void handlePlayerToggleFlight(Player player, boolean flying) {
        if (currentTripleJumps.contains(player.getUniqueId()) || !flying || player.isInsideVehicle() || player.getInventory().getChestplate() != null || !player.hasPermission(PERMISSION_DOUBLE_JUMP)) {
            return;
        }

        UUID uniqueId = player.getUniqueId();

        NCPExemptionManager.exemptPermanently(uniqueId, CheckType.MOVING_SURVIVALFLY);

        double velocityStrength = 1.7;
        if (!currentDoubleJumps.contains(uniqueId)) {
            currentDoubleJumps.add(uniqueId);

            // En- or disable trigger for next jump.
            player.setAllowFlight(player.hasPermission(PERMISSION_TRIPLE_JUMP));

            startDoubleJumpEndDetection(player);
        } else if (player.hasPermission(PERMISSION_TRIPLE_JUMP)) {
            currentTripleJumps.add(uniqueId);

            velocityStrength = 2;

            PlayerInventory playerInventory = player.getInventory();
            playerInventory.setChestplate(doubleJumpElytraItemStack);

            player.setGliding(true);
            player.setFlying(false);
            player.setAllowFlight(false);

            startTripleJumpEndDetection(player);
        } else {
            return;
        }

        performJump(player, velocityStrength);
    }

    /**
     * Detect when the given player ends his double jump session.
     *
     * @param player The player.
     */
    private void startDoubleJumpEndDetection(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancel();
                    currentDoubleJumps.remove(player.getUniqueId());
                    return;
                }

                // Check if player is still flying.
                if (!player.isOnGround()) {
                    return;
                }

                // Re allow flight for next double jump.
                player.setAllowFlight(true);
                currentDoubleJumps.remove(player.getUniqueId());

                cancel();

                NCPExemptionManager.unexempt(player.getUniqueId(), CheckType.MOVING_SURVIVALFLY);
            }
        }.runTaskTimer(plugin, 1, 1);
    }

    /**
     * Detect when the given player ends his triple jump session.
     *
     * @param player The player.
     */
    private void startTripleJumpEndDetection(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancel();
                    currentTripleJumps.remove(player.getUniqueId());
                }

                if (player.getInventory().getChestplate() == null || player.isOnGround()) {
                    player.setAllowFlight(true);

                    currentDoubleJumps.remove(player.getUniqueId());
                    currentTripleJumps.remove(player.getUniqueId());

                    player.getInventory().setChestplate(new ItemStack(Material.AIR));

                    cancel();

                    NCPExemptionManager.unexempt(player.getUniqueId(), CheckType.MOVING_SURVIVALFLY);
                    return;
                }

                player.setAllowFlight(false);
                player.setFlying(false);
                player.setGliding(true);
            }
        }.runTaskTimer(plugin, 1, 1);
    }

    /**
     * Perform the jump of the player wit the given strength.
     *
     * @param player   The player.
     * @param strength The strength of the jump.
     */
    private void performJump(Player player, double strength) {
        particleService.sendParticle(player, ParticleType.CLOUD, true, player.getLocation(), new Vector(0, -1, 0), 0.2F, 15);
        Location loc = player.getLocation();
        player.setVelocity(new Vector(strength * -Math.sin(Math.toRadians(loc.getYaw())), 0.4 * strength, strength * Math.cos(Math.toRadians(loc.getYaw()))));
    }


    /**
     * Handle that the given player quit the server.
     *
     * @param player The player.
     */
    public void handlePlayerQuit(Player player) {
        currentDoubleJumps.remove(player.getUniqueId());
        currentTripleJumps.remove(player.getUniqueId());
    }
}
