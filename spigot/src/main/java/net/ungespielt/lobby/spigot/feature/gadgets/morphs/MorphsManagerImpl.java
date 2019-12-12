package net.ungespielt.lobby.spigot.feature.gadgets.morphs;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import net.ungespielt.lobby.spigot.api.event.PlayerMorphEvent;
import net.ungespielt.lobby.spigot.api.event.PlayerUnmorphEvent;
import net.ungespielt.lobby.spigot.api.feature.gadgets.morphs.Morph;
import net.ungespielt.lobby.spigot.api.feature.gadgets.morphs.MorphsManager;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;
import java.util.Set;

/**
 * The default implementation of the {@link MorphsManager}.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
@Singleton
public class MorphsManagerImpl implements MorphsManager {

    /**
     * All currently known morphs.
     */
    private final Map<Player, Morph> currentMorphs = Maps.newConcurrentMap();

    /**
     * The morph factory.
     */
    private final MorphFactory morphFactory;

    /**
     * The bukkit plugin manager.
     */
    private final PluginManager pluginManager;

    @Inject
    public MorphsManagerImpl(MorphFactory morphFactory, PluginManager pluginManager) {
        this.morphFactory = morphFactory;
        this.pluginManager = pluginManager;
    }

    @Override
    public Morph morphPlayer(Player player, EntityType entityType) {
        Morph morph = morphFactory.createMorph(player, entityType);

        PlayerMorphEvent playerMorphEvent = new PlayerMorphEvent(player, morph);
        pluginManager.callEvent(playerMorphEvent);

        if (playerMorphEvent.isCancelled()) {
            return null;
        }

        currentMorphs.put(player, morph);

        morph.initialize();
        return morph;
    }

    @Override
    public boolean isMorphed(Player player) {
        return currentMorphs.containsKey(player);
    }

    @Override
    public boolean unmorphPlayer(Player player) {
        Morph morph = currentMorphs.remove(player);

        if (morph != null) {
            PlayerUnmorphEvent playerUnmorphEvent = new PlayerUnmorphEvent(player, morph);
            pluginManager.callEvent(playerUnmorphEvent);
            morph.destroy();
        }

        return morph != null;
    }

    @Override
    public Set<Morph> getMorphs() {
        return ImmutableSet.copyOf(currentMorphs.values());
    }

    @Override
    public void refreshMorphs(Player player) {
        getMorphs().forEach(morph -> {
            if (morph.getPlayer() == player) {
                return;
            }

            morph.refresh(player);
        });
    }
}
