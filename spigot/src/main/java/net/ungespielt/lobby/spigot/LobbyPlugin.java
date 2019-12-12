package net.ungespielt.lobby.spigot;

import com.google.inject.Guice;
import com.google.inject.Injector;
import net.ungespielt.lobby.spigot.api.LobbyApplication;
import net.ungespielt.lobby.spigot.module.LobbyModule;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Spigot plugin class.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class LobbyPlugin extends JavaPlugin {

    /**
     * The google guice dependency injector.
     */
    private Injector injector;

    /**
     * The central lobby application.
     */
    private LobbyApplication lobbyApplication;

    @Override
    public void onEnable() {
        injector = Guice.createInjector(new LobbyModule(this));
        lobbyApplication = injector.getInstance(LobbyApplication.class);
        lobbyApplication.init();
    }

    @Override
    public void onDisable() {
        lobbyApplication.destroy();
    }
}
