package net.ungespielt.lobby.spigot.feature.gadgets.morphs;

import net.ungespielt.lobby.spigot.api.feature.gadgets.morphs.Morph;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/**
 * The factory for the {@link MorphImpl}.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
public interface MorphFactory {

    /**
     * Create a morph for the given player.
     *
     * @param player     The player.
     * @param entityType The entity type.
     * @return The morph.
     */
    Morph createMorph(Player player, EntityType entityType);
}
