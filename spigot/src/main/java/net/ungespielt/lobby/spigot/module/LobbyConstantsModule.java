package net.ungespielt.lobby.spigot.module;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import org.bukkit.ChatColor;

/**
 * The module that binds all constants.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class LobbyConstantsModule extends AbstractModule {

    @Override
    protected void configure() {
        bindConstant().annotatedWith(Names.named("prefix")).to(ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "ungeserver" + ChatColor.DARK_GRAY + "] " + ChatColor.GOLD);
        bindConstant().annotatedWith(Names.named("serverId")).to(System.getProperty("cluster.server.id"));
        bindConstant().annotatedWith(Names.named("shortServerId")).to(System.getProperty("cluster.server.id").substring(5));
        bindConstant().annotatedWith(Names.named("navigatorItemSlot")).to(0);
        bindConstant().annotatedWith(Names.named("lobbySwitcherItemSlot")).to(1);
        bindConstant().annotatedWith(Names.named("scoreboardDisplayName")).to(ChatColor.GREEN + "ungeserver");
        bindConstant().annotatedWith(Names.named("preferencesItemSlot")).to(8);
        bindConstant().annotatedWith(Names.named("gadgetsItemSlot")).to(4);
        bindConstant().annotatedWith(Names.named("mysticChestsItemSlot")).to(7);
        bindConstant().annotatedWith(Names.named("lobbyClusterServerGroupName")).to("lobby");

        // pets
        bindConstant().annotatedWith(Names.named("petMinTeleportDistance")).to(15);
        bindConstant().annotatedWith(Names.named("petMinWalkDistance")).to(4);
    }
}
