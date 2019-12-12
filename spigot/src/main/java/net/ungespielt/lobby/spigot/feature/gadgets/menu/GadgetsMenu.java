package net.ungespielt.lobby.spigot.feature.gadgets.menu;

import com.google.inject.assistedinject.Assisted;
import de.jackwhite20.base.api.spigot.menu.impl.Menu;
import de.jackwhite20.base.spigot.menu.MenuImplementation;
import net.ungespielt.lobby.spigot.feature.gadgets.GadgetsViewModel;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * The menu with the overview over all gadgets.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class GadgetsMenu extends MenuImplementation implements Menu {

    /**
     * The player.
     */
    private final Player player;

    /**
     * The view model.
     */
    private final GadgetsViewModel viewModel;

    /**
     * The item stack for pets.
     */
    private final ItemStack petsItemStack;

    /**
     * The item stack for morphs.
     */
    private final ItemStack morphsItemStack;

    /**
     * The item stack for heads.
     */
    private final ItemStack headsItemStack;

    /**
     * Create a new gadgets menu.
     *
     * @param player          The player.
     * @param viewModel       The view model.
     * @param petsItemStack   The pets item stack.
     * @param morphsItemStack The morphs item stack.
     * @param headsItemStack  The heads item stack.
     */
    @Inject
    public GadgetsMenu(@Assisted Player player, GadgetsViewModel viewModel,
                       @Named("petsItemStack") ItemStack petsItemStack,
                       @Named("morphsItemStack") ItemStack morphsItemStack,
                       @Named("headsItemStack") ItemStack headsItemStack) {
        super(player, "§6Gadgets", 27);
        this.player = player;
        this.viewModel = viewModel;
        this.petsItemStack = petsItemStack;
        this.morphsItemStack = morphsItemStack;
        this.headsItemStack = headsItemStack;
    }

    private void setupItems() {
        setItem(11, petsItemStack, menuClickEvent -> viewModel.openPetsMenu(player));
        setItem(13, headsItemStack, menuClickEvent -> viewModel.openHeadsMenu(player));
        setItem(15, morphsItemStack, menuClickEvent -> viewModel.openMorphsMenu(player));
    }

    @Override
    public void open() {
        setupItems();
        super.open();
    }
}
