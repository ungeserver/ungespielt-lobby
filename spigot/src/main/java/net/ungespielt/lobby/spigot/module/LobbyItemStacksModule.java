package net.ungespielt.lobby.spigot.module;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import de.jackwhite20.base.api.spigot.util.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

/**
 * The module for all item stacks.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class LobbyItemStacksModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ItemStack.class).annotatedWith(Names.named("navigatorItemStack"))
                .toInstance(new ItemBuilder(Material.COMPASS).name(ChatColor.GOLD + "Navigator " + ChatColor.GRAY + "(Rechtsklick)").build());
        bind(ItemStack.class).annotatedWith(Names.named("navigatorItemStackSpawn"))
                .toInstance(new ItemBuilder(Material.MAGMA_CREAM).name(ChatColor.GREEN + "Spawn").build());
        bind(ItemStack.class).annotatedWith(Names.named("gadgetsItemStack"))
                .toInstance(new ItemBuilder(Material.MAGMA_CREAM).name(ChatColor.GOLD + "Gadgets " + ChatColor.GRAY + "(Rechtsklick)").build());

        // UI control elements
        bind(ItemStack.class).annotatedWith(Names.named("backItemStack"))
                .toInstance(new ItemBuilder(Material.getMaterial(351)).durability((short) 1).name(ChatColor.GREEN + "Zurück").build());
        bind(ItemStack.class).annotatedWith(Names.named("closeItemStack"))
                .toInstance(new ItemBuilder(Material.BARRIER).name(ChatColor.GREEN + "Schließen").build());
        bind(ItemStack.class).annotatedWith(Names.named("comingSoonItemStack"))
                .toInstance(new ItemBuilder(Material.BLAZE_POWDER).name(ChatColor.RED + "Coming Soon™").build());
        bind(ItemStack.class).annotatedWith(Names.named("balanceItemStack"))
                .toInstance(new ItemBuilder(Material.GOLD_INGOT).name(ChatColor.GREEN + "Coins: " + ChatColor.GOLD).build());

        // Gadgets
        bind(ItemStack.class).annotatedWith(Names.named("petsItemStack"))
                .toInstance(new ItemBuilder(Material.SADDLE).name(ChatColor.GREEN + "Haustiere").build());
        bind(ItemStack.class).annotatedWith(Names.named("carsItemStack"))
                .toInstance(new ItemBuilder(Material.STORAGE_MINECART).name(ChatColor.GREEN + "Autos").build());
        bind(ItemStack.class).annotatedWith(Names.named("morphsItemStack"))
                .toInstance(new ItemBuilder(Material.ELYTRA).name(ChatColor.GREEN + "Morphs").build());
        bind(ItemStack.class).annotatedWith(Names.named("headsItemStack"))
                .toInstance(new ItemBuilder(Material.SKULL_ITEM).name(ChatColor.GREEN + "Köpfe").build());

        // Preferences
        bind(ItemStack.class).annotatedWith(Names.named("preferencesItemStack"))
                .toInstance(new ItemBuilder(Material.COMMAND).name(ChatColor.GOLD + "Einstellungen " + ChatColor.GRAY + "(Rechtsklick)").build());
        bind(ItemStack.class).annotatedWith(Names.named("scoreboardPreferencesItemStack"))
                .toInstance(new ItemBuilder(Material.SIGN).name(ChatColor.GREEN + "Scoreboard").build());
        bind(ItemStack.class).annotatedWith(Names.named("visibilityPreferencesItemStack"))
                .toInstance(new ItemBuilder(Material.BLAZE_ROD).name(ChatColor.GREEN + "Spielersichtbarkeit").build());

        // Heads
        bind(ItemStack.class).annotatedWith(Names.named("hatOffItemStack"))
                .toInstance(new ItemBuilder(Material.BARRIER).name(ChatColor.GREEN + "Kopf löschen").build());

        // Morphs
        bind(ItemStack.class).annotatedWith(Names.named("unmorphItemStack"))
                .toInstance(new ItemBuilder(Material.BARRIER).name(ChatColor.GREEN + "Morph löschen").build());

        // Pets
        bind(ItemStack.class).annotatedWith(Names.named("removePetItemStack"))
                .toInstance(new ItemBuilder(Material.BARRIER).name(ChatColor.GREEN + "Haustier löschen").build());

        // Lobby switcher
        bind(ItemStack.class).annotatedWith(Names.named("lobbySwitcherItemStack"))
                .toInstance(new ItemBuilder(Material.WATCH).name(ChatColor.GOLD + "Lobby wechseln " + ChatColor.GRAY + "(Rechtsklick)").build());

        // Shop
        bind(ItemStack.class).annotatedWith(Names.named("purchaseItemStack"))
                .toInstance(new ItemBuilder(Material.GREEN_GLAZED_TERRACOTTA).name(ChatColor.GREEN + "Kaufen").build());
        bind(ItemStack.class).annotatedWith(Names.named("declineItemStack"))
                .toInstance(new ItemBuilder(Material.RED_GLAZED_TERRACOTTA).name(ChatColor.GREEN + "Nicht kaufen").build());

        // Cars
        bind(ItemStack.class).annotatedWith(Names.named("enterCarItemStack"))
                .toInstance(new ItemBuilder(Material.STORAGE_MINECART).name(ChatColor.GREEN + "Ins Auto einsteigen").build());
        bind(ItemStack.class).annotatedWith(Names.named("leaveCarItemStack"))
                .toInstance(new ItemBuilder(Material.MINECART).name(ChatColor.GREEN + "Aus dem Auto aussteigen.").build());

        // Preferences
        bind(ItemStack.class).annotatedWith(Names.named("activateScoreboardItemStack"))
                .toInstance(new ItemBuilder(Material.GREEN_GLAZED_TERRACOTTA).name(ChatColor.GREEN + "Scoreboard einschalten").build());
        bind(ItemStack.class).annotatedWith(Names.named("deactivateScoreboardItemStack"))
                .toInstance(new ItemBuilder(Material.RED_GLAZED_TERRACOTTA).name(ChatColor.RED + "Scoreboard ausschalten").build());
        bind(ItemStack.class).annotatedWith(Names.named("noOneVisibleItemStack"))
                .toInstance(new ItemBuilder(Material.DIAMOND).name(ChatColor.AQUA + "Niemand sichtbar. Nur du.").build());
        bind(ItemStack.class).annotatedWith(Names.named("teamVisibleItemStack"))
                .toInstance(new ItemBuilder(Material.GOLD_INGOT).name(ChatColor.GOLD + "Nur VIPs sichtbar.").build());
        bind(ItemStack.class).annotatedWith(Names.named("allVisibleItemStack"))
                .toInstance(new ItemBuilder(Material.DIRT).name(ChatColor.GRAY + "Jeder sichtbar").build());

        // Mystic Chests
        bind(ItemStack.class).annotatedWith(Names.named("mysticChestsItemStack"))
                .toInstance(new ItemBuilder(Material.ENDER_CHEST).name(ChatColor.GOLD + "Mystic Chests " + ChatColor.GRAY + " (Rechtsklick)").build());
        bind(ItemStack.class).annotatedWith(Names.named("chestCountItemStack"))
                .toInstance(new ItemBuilder(Material.DIAMOND).name(ChatColor.BLUE + "Mystic Chests: " + ChatColor.GRAY).build());
        bind(ItemStack.class).annotatedWith(Names.named("mysticRollItemStack"))
                .toInstance(new ItemBuilder(Material.CHEST).name(ChatColor.GREEN + "Eine Kiste öffnen").build());
        bind(new TypeLiteral<List<ItemStack>>() {
        }).annotatedWith(Names.named("randomRollItemStacks"))
                .toInstance(Arrays.asList(
                        new ItemBuilder(Material.DIAMOND).name(ChatColor.RED + "They see me rolling...").build(),
                        new ItemBuilder(Material.GOLD_INGOT).name(ChatColor.RED + "They see me rolling...").build(),
                        new ItemBuilder(Material.DIAMOND_BLOCK).name(ChatColor.RED + "They see me rolling...").build(),
                        new ItemBuilder(Material.BLAZE_ROD).name(ChatColor.RED + "They see me rolling...").build(),
                        new ItemBuilder(Material.CARROT_ITEM).name(ChatColor.RED + "They see me rolling...").build(),
                        new ItemBuilder(Material.COMMAND).name(ChatColor.RED + "They see me rolling...").build(),
                        new ItemBuilder(Material.FIREBALL).name(ChatColor.RED + "They see me rolling...").build(),
                        new ItemBuilder(Material.DIRT).name(ChatColor.RED + "They see me rolling...").build(),
                        new ItemBuilder(Material.DIAMOND_LEGGINGS).name(ChatColor.RED + "They see me rolling...").build(),
                        new ItemBuilder(Material.TNT).name(ChatColor.RED + "They see me rolling...").build(),
                        new ItemBuilder(Material.GOLD_SWORD).name(ChatColor.RED + "They see me rolling...").build()
                ));
        bind(ItemStack.class).annotatedWith(Names.named("rollCompleteItemStack"))
                .toInstance(new ItemBuilder(Material.COOKIE).glow().name(ChatColor.GREEN + "Gewinn gezogen! Hier klicken.").build());
        bind(ItemStack.class).annotatedWith(Names.named("randomRollRunningBorderItemStack"))
                .toInstance(new ItemBuilder(Material.RED_GLAZED_TERRACOTTA).name(ChatColor.RED + "Roll läuft noch...").build());
        bind(ItemStack.class).annotatedWith(Names.named("randomRollStoppedBorderItemStack"))
                .toInstance(new ItemBuilder(Material.GREEN_GLAZED_TERRACOTTA).glow().name(ChatColor.GREEN + "Gewinn ist bereits gezogen!").build());

        // Double jump
        bind(ItemStack.class).annotatedWith(Names.named("doubleJumpElytraItemStack"))
                .toInstance(new ItemBuilder(Material.ELYTRA).name(ChatColor.GOLD + "Elytra (Dreifachsprung)").build());

    }
}
