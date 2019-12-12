package net.ungespielt.lobby.spigot.module;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;
import de.jackwhite20.base.api.service.ServiceRegistry;
import de.jackwhite20.base.api.service.cluster.ClusterService;
import de.jackwhite20.base.api.service.japs.PubSubService;
import de.jackwhite20.base.api.service.permission.PermissionService;
import de.jackwhite20.base.api.service.player.PlayerService;
import de.jackwhite20.base.api.spigot.command.CommandService;
import de.jackwhite20.base.api.spigot.feature.FeatureService;
import de.jackwhite20.base.api.spigot.menu.MenuService;
import de.jackwhite20.base.api.spigot.menu.impl.Menu;
import de.jackwhite20.base.api.spigot.particle.ParticleService;
import de.jackwhite20.base.api.spigot.scoreboard.ScoreboardService;
import de.jackwhite20.base.api.spigot.title.TitleService;
import fr.neatmonster.nocheatplus.NCPAPIProvider;
import fr.neatmonster.nocheatplus.components.NoCheatPlusAPI;
import io.reactivex.Scheduler;
import net.ungespielt.lobby.spigot.LobbyApplicationImpl;
import net.ungespielt.lobby.spigot.api.LobbyApplication;
import net.ungespielt.lobby.spigot.api.feature.FeatureManager;
import net.ungespielt.lobby.spigot.api.feature.gadgets.heads.Head;
import net.ungespielt.lobby.spigot.api.feature.gadgets.heads.HeadsManager;
import net.ungespielt.lobby.spigot.api.feature.gadgets.morphs.Morph;
import net.ungespielt.lobby.spigot.api.feature.gadgets.morphs.MorphsManager;
import net.ungespielt.lobby.spigot.api.feature.gadgets.pets.Pet;
import net.ungespielt.lobby.spigot.api.feature.gadgets.pets.PetsManager;
import net.ungespielt.lobby.spigot.api.rx.RxManager;
import net.ungespielt.lobby.spigot.api.shop.PurchaseContext;
import net.ungespielt.lobby.spigot.api.shop.ShopItem;
import net.ungespielt.lobby.spigot.api.shop.ShopManager;
import net.ungespielt.lobby.spigot.feature.FeatureManagerImpl;
import net.ungespielt.lobby.spigot.feature.gadgets.heads.HeadFactory;
import net.ungespielt.lobby.spigot.feature.gadgets.heads.HeadImpl;
import net.ungespielt.lobby.spigot.feature.gadgets.heads.HeadsManagerImpl;
import net.ungespielt.lobby.spigot.feature.gadgets.heads.menu.HeadsMenu;
import net.ungespielt.lobby.spigot.feature.gadgets.heads.menu.HeadsMenuFactory;
import net.ungespielt.lobby.spigot.feature.gadgets.menu.GadgetsMenu;
import net.ungespielt.lobby.spigot.feature.gadgets.menu.GadgetsMenuFactory;
import net.ungespielt.lobby.spigot.feature.gadgets.morphs.MorphFactory;
import net.ungespielt.lobby.spigot.feature.gadgets.morphs.MorphImpl;
import net.ungespielt.lobby.spigot.feature.gadgets.morphs.MorphsManagerImpl;
import net.ungespielt.lobby.spigot.feature.gadgets.morphs.menu.MorphsMenu;
import net.ungespielt.lobby.spigot.feature.gadgets.morphs.menu.MorphsMenuFactory;
import net.ungespielt.lobby.spigot.feature.gadgets.pets.PetFactory;
import net.ungespielt.lobby.spigot.feature.gadgets.pets.PetImpl;
import net.ungespielt.lobby.spigot.feature.gadgets.pets.PetsManagerImpl;
import net.ungespielt.lobby.spigot.feature.gadgets.pets.menu.PetsMenu;
import net.ungespielt.lobby.spigot.feature.gadgets.pets.menu.PetsMenuFactory;
import net.ungespielt.lobby.spigot.feature.lobbyswitcher.menu.LobbySwitcherMenu;
import net.ungespielt.lobby.spigot.feature.lobbyswitcher.menu.LobbySwitcherMenuFactory;
import net.ungespielt.lobby.spigot.feature.mysticchests.menu.MysticChestsMenu;
import net.ungespielt.lobby.spigot.feature.mysticchests.menu.MysticChestsMenuFactory;
import net.ungespielt.lobby.spigot.feature.mysticchests.menu.roll.MysticChestRollMenu;
import net.ungespielt.lobby.spigot.feature.mysticchests.menu.roll.MysticChestRollMenuFactory;
import net.ungespielt.lobby.spigot.feature.preferences.menu.PreferencesMenu;
import net.ungespielt.lobby.spigot.feature.preferences.menu.PreferencesMenuFactory;
import net.ungespielt.lobby.spigot.feature.preferences.menu.scoreboard.ScoreboardPreferencesMenu;
import net.ungespielt.lobby.spigot.feature.preferences.menu.scoreboard.ScoreboardPreferencesMenuFactory;
import net.ungespielt.lobby.spigot.feature.preferences.menu.visibility.VisibilityMenu;
import net.ungespielt.lobby.spigot.feature.preferences.menu.visibility.VisibilityMenuFactory;
import net.ungespielt.lobby.spigot.rx.RxManagerImpl;
import net.ungespielt.lobby.spigot.rx.scheduler.SpigotRxScheduler;
import net.ungespielt.lobby.spigot.shop.*;
import net.ungespielt.lobby.spigot.shop.menu.ShopItemPurchaseMenu;
import net.ungespielt.lobby.spigot.shop.menu.ShopItemPurchaseMenuFactory;
import net.ungespielt.social.api.SocialService;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Google Guice Module for dependency injection.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class LobbyModule extends AbstractModule {

    /**
     * The central plugin we are working for.
     */
    private final Plugin plugin;

    /**
     * Create a new module instance.
     *
     * @param plugin The plugin.
     */
    public LobbyModule(Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Configure bindings.
     */
    @Override
    protected void configure() {
        // Bind Spigot stuff
        bind(Plugin.class).toInstance(plugin);
        bind(JavaPlugin.class).toInstance((JavaPlugin) plugin);
        bind(PluginManager.class).toInstance(plugin.getServer().getPluginManager());

        // Constants
        install(new LobbyConstantsModule());

        // ItemStacks
        install(new LobbyItemStacksModule());

        // Locations
        bind(Location.class).annotatedWith(Names.named("spawnLocation"))
                .toInstance(Bukkit.getServer().getWorlds().get(0).getSpawnLocation());

        // Factories
        install(new FactoryModuleBuilder()
                .implement(Menu.class, GadgetsMenu.class)
                .build(GadgetsMenuFactory.class));
        install(new FactoryModuleBuilder()
                .implement(Menu.class, PetsMenu.class)
                .build(PetsMenuFactory.class));
        install(new FactoryModuleBuilder()
                .implement(Menu.class, MorphsMenu.class)
                .build(MorphsMenuFactory.class));
        install(new FactoryModuleBuilder()
                .implement(Menu.class, HeadsMenu.class)
                .build(HeadsMenuFactory.class));
        install(new FactoryModuleBuilder()
                .implement(Menu.class, PreferencesMenu.class)
                .build(PreferencesMenuFactory.class));
        install(new FactoryModuleBuilder()
                .implement(Head.class, HeadImpl.class)
                .build(HeadFactory.class));
        install(new FactoryModuleBuilder()
                .implement(Menu.class, ShopItemPurchaseMenu.class)
                .build(ShopItemPurchaseMenuFactory.class));
        install(new FactoryModuleBuilder()
                .implement(PurchaseContext.class, PurchaseContextImpl.class)
                .build(PurchaseContextFactory.class));
        install(new FactoryModuleBuilder()
                .implement(ShopItem.class, ShopItemImpl.class)
                .build(ShopItemFactory.class));
        install(new FactoryModuleBuilder()
                .implement(Morph.class, MorphImpl.class)
                .build(MorphFactory.class));
        install(new FactoryModuleBuilder()
                .implement(Menu.class, LobbySwitcherMenu.class)
                .build(LobbySwitcherMenuFactory.class));
        install(new FactoryModuleBuilder()
                .implement(Pet.class, PetImpl.class)
                .build(PetFactory.class));
        install(new FactoryModuleBuilder()
                .implement(Menu.class, ScoreboardPreferencesMenu.class)
                .build(ScoreboardPreferencesMenuFactory.class));
        install(new FactoryModuleBuilder()
                .implement(Menu.class, VisibilityMenu.class)
                .build(VisibilityMenuFactory.class));
        install(new FactoryModuleBuilder()
                .implement(Menu.class, MysticChestsMenu.class)
                .build(MysticChestsMenuFactory.class));
        install(new FactoryModuleBuilder()
                .implement(Menu.class, MysticChestRollMenu.class)
                .build(MysticChestRollMenuFactory.class));

        // Rx
        bind(Scheduler.class).annotatedWith(Names.named("syncScheduler")).toInstance(new SpigotRxScheduler(true, plugin.getServer().getScheduler(), plugin));
        bind(Scheduler.class).annotatedWith(Names.named("asyncScheduler")).toInstance(new SpigotRxScheduler(false, plugin.getServer().getScheduler(), plugin));
        bind(RxManager.class).to(RxManagerImpl.class);

        // Application
        bind(LobbyApplication.class).to(LobbyApplicationImpl.class);

        // Shop
        bind(ShopManager.class).to(ShopManagerImpl.class);

        // Features
        bind(FeatureManager.class).to(FeatureManagerImpl.class);

        // Gadgets
        bind(PetsManager.class).to(PetsManagerImpl.class);
        bind(MorphsManager.class).to(MorphsManagerImpl.class);
        bind(HeadsManager.class).to(HeadsManagerImpl.class);

        // ProtocolLib
        bind(ProtocolManager.class).toInstance(ProtocolLibrary.getProtocolManager());

        // NoCheatPlus
        bind(NoCheatPlusAPI.class).toInstance(NCPAPIProvider.getNoCheatPlusAPI());
    }

    /**
     * Provide bukkit server instance.
     *
     * @return The server instance.
     */
    @Provides
    Server providesServer() {
        return Bukkit.getServer();
    }

    /**
     * Provide the title service of the spigot base.
     *
     * @return The title service.
     */
    @Provides
    TitleService providesTitleService() {
        return ServiceRegistry.lookup(TitleService.class);
    }

    @Provides
    PubSubService providesPubSubService() {
        return ServiceRegistry.lookup(PubSubService.class);
    }

    @Provides
    MenuService providesMenuService() {
        return ServiceRegistry.lookup(MenuService.class);
    }

    @Provides
    ScoreboardService providesScoreboardService() {
        return ServiceRegistry.lookup(ScoreboardService.class);
    }

    @Provides
    PermissionService providesPermissionService() {
        return ServiceRegistry.lookup(PermissionService.class);
    }

    @Provides
    PlayerService providesPlayerService() {
        return ServiceRegistry.lookup(PlayerService.class);
    }

    @Provides
    CommandService providesCommandService() {
        return ServiceRegistry.lookup(CommandService.class);
    }

    @Provides
    FeatureService providesFeatureService() {
        return ServiceRegistry.lookup(FeatureService.class);
    }

    @Provides
    ClusterService providesClusterService() {
        return ServiceRegistry.lookup(ClusterService.class);
    }

    @Provides
    ParticleService providesParticleService() {
        return ServiceRegistry.lookup(ParticleService.class);
    }

    @Provides
    SocialService providesSocialService() {
        return ServiceRegistry.lookup(SocialService.class);
    }
}
