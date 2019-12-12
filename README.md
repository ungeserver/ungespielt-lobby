# Permissions
The following permissions are needed:
- lobby.command.vanish - Players vanishing themselves.
- lobby.doublejump.triple - Players can jump three times

# Lobby architecture
In the following the main architecture will be explained.

## Dependency Injection
We use dependency injection via google guice. Therefor you should try to use constructor injection wherever
possible to keep the code clean and still scalable. All bindings should be configured using the LobbyModule under
net.ungespielt.lobby.spigot.module

If you have any questions don't hesitate to ask.

## Rx instead of listeners
To get rid of bukkits plain old listeners we use RxJava with a mapping of its schedulers to fit into
the bukkit scheduler architecture. If you want to listen for an event you use inject the RxManager and
then register a listener via this:

```java
Disposable subscription = rxManager.observeEvent(PlayerJoinEvent.class)
    .subscribeOn(Schedulers.computation())
    .subscribe(new Consumer() {...});
```

Remember to dispose the subscription if you don't need it anymore.

## A bit of MVVM / Features
All main features have their base in a controller that extends the FeatureController. The controller should
be as clean as possible and will manage the lifecycle of a feature.

_Example:_
```java
package net.ungespielt.lobby.spigot.feature.welcome;

import net.ungespielt.lobby.spigot.api.feature.FeatureController;
import net.ungespielt.lobby.spigot.api.rx.RxManager;
import org.bukkit.event.player.PlayerJoinEvent;

import javax.inject.Inject;

/**
 * Welcome controller that will say hello to joining players.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class WelcomeController extends FeatureController<WelcomeViewModel> {

    /**
     * The rx manager.
     */
    private final RxManager rxManager;

    /**
     * Create a new welcome controller.
     *
     * @param welcomeViewModel The view model.
     * @param rxManager        The rx manager.
     */
    @Inject
    public WelcomeController(WelcomeViewModel welcomeViewModel, RxManager rxManager) {
        super(welcomeViewModel);
        this.rxManager = rxManager;
    }

    @Override
    protected void doOnEnable() {
        subscribe(rxManager.observeEvent(PlayerJoinEvent.class).subscribe(playerJoinEvent -> getViewModel().handlePlayerJoin(playerJoinEvent.getPlayer())));
    }
}
```

The controller should setup all bindings to events, commands and so on to the view model. Using this
architecture the view model is very easy to test and also very scalable. Using dependency injection
it is also super easy to swap components.

```java
package net.ungespielt.lobby.spigot.feature.welcome;

import de.jackwhite20.base.api.spigot.title.TitleService;
import net.md_5.bungee.api.chat.TextComponent;
import net.ungespielt.lobby.spigot.api.feature.FeatureViewModel;
import net.ungespielt.lobby.spigot.data.LobbyConfigDataManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * View Model for {@link WelcomeController}.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class WelcomeViewModel extends FeatureViewModel {

    /**
     * The data manager that will provide the config.
     */
    private final LobbyConfigDataManager configDataManager;

    /**
     * The title service.
     */
    private final TitleService titleService;

    /**
     * The id of the server.
     */
    private final String serverId;

    /**
     * The logger to log all actions.
     */
    private final Logger logger;

    /**
     * Create a new welcome view model.
     *
     * @param titleService The title service.
     * @param serverId The server id.
     */
    @Inject
    public WelcomeViewModel(LobbyConfigDataManager configDataManager, TitleService titleService, @Named("shortServerId") String serverId, Plugin plugin) {
        this.configDataManager = configDataManager;
        this.titleService = titleService;
        this.serverId = serverId;
        this.logger = plugin.getLogger();
    }

    /**
     * Handle a player join.
     *
     * @param player The player who joined.
     */
    public void handlePlayerJoin(Player player) {
        logger.log(Level.FINE, "Welcoming player " + player.getName() + ".");

        TextComponent mainTitle = new TextComponent(ChatColor.GOLD + "Willkommen " + player.getDisplayName());
        TextComponent subTitle = new TextComponent(ChatColor.AQUA + "auf " + ChatColor.GREEN + serverId);

        titleService.sendTitle(player, 20, 60, 20, mainTitle, subTitle);

        subscribe(configDataManager.getConfig().subscribe(config -> {
            BossBar bossBar = Bukkit.createBossBar(config.getBossBarContent()[new Random().nextInt(config.getBossBarContent().length)], BarColor.BLUE, BarStyle.SOLID);
            bossBar.addPlayer(player);
        }, throwable -> logger.log(Level.INFO, "Error displaying bossbar from config. Leaving bossbar blank.")));
    }
}
```

## Building Menus
In earlier versions it was very hard to setup menus. In the newer version this process is highly integrated
with google guice using assisted injection. Using this it is very easy to build player specific but also
general valid menus.

You will have to create a class for the menu that looks like the following:
```java
package net.ungespielt.lobby.spigot.feature.navigator.menu;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import de.jackwhite20.base.api.spigot.menu.impl.Menu;
import de.jackwhite20.base.spigot.menu.MenuImplementation;
import net.ungespielt.lobby.spigot.feature.navigator.NavigatorViewModel;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.inject.Named;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class NavigatorMenu extends MenuImplementation implements Menu {

    @Inject
    public NavigatorMenu(@Assisted Player player, @Assisted NavigatorViewModel viewModel, @Named("navigatorItemStackSpawn") ItemStack navigatorItemStackSpawn) {
        super(player, "Navigator", 54);
    }
}
```

You can setup everything that is necessary in the constructor. To be able to use assisted injection
you now have to define the factory:

```java
package net.ungespielt.lobby.spigot.feature.navigator.menu;

import de.jackwhite20.base.api.spigot.menu.impl.Menu;
import net.ungespielt.lobby.spigot.feature.navigator.NavigatorViewModel;
import org.bukkit.entity.Player;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
public interface NavigatorMenuFactory {

    /**
     * Create a menu for the given player.
     *
     * @param player             The player.
     * @param navigatorViewModel the view model.
     * @return The menu.
     */
    Menu createMenu(Player player, NavigatorViewModel navigatorViewModel);
}
```

The factory has to be bound in the module:
```java
install(new FactoryModuleBuilder()
                .implement(Menu.class, NavigatorMenu.class)
                .build(NavigatorMenuFactory.class));
```

Now its very easy to use. You can inject the factory and then use it like the following:
```java
Menu menu = menuFactory.createMenu(player, viewModel);
````
