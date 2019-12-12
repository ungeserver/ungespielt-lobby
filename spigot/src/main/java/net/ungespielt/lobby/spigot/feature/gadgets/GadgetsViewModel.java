package net.ungespielt.lobby.spigot.feature.gadgets;

import de.jackwhite20.base.api.spigot.menu.impl.Menu;
import io.reactivex.schedulers.Schedulers;
import net.ungespielt.lobby.spigot.api.feature.FeatureViewModel;
import net.ungespielt.lobby.spigot.api.feature.gadgets.heads.Head;
import net.ungespielt.lobby.spigot.api.feature.gadgets.heads.HeadsManager;
import net.ungespielt.lobby.spigot.api.feature.gadgets.morphs.Morph;
import net.ungespielt.lobby.spigot.api.feature.gadgets.morphs.MorphsManager;
import net.ungespielt.lobby.spigot.api.feature.gadgets.pets.Pet;
import net.ungespielt.lobby.spigot.api.feature.gadgets.pets.PetRemoveReason;
import net.ungespielt.lobby.spigot.api.feature.gadgets.pets.PetsManager;
import net.ungespielt.lobby.spigot.api.rx.RxManager;
import net.ungespielt.lobby.spigot.data.PlayerHeadDataManager;
import net.ungespielt.lobby.spigot.data.PlayerMorphDataManager;
import net.ungespielt.lobby.spigot.data.PlayerPetsDataManager;
import net.ungespielt.lobby.spigot.data.PlayerPreferencesDataManager;
import net.ungespielt.lobby.spigot.feature.gadgets.heads.menu.HeadsMenuFactory;
import net.ungespielt.lobby.spigot.feature.gadgets.menu.GadgetsMenuFactory;
import net.ungespielt.lobby.spigot.feature.gadgets.morphs.menu.MorphsMenuFactory;
import net.ungespielt.lobby.spigot.feature.gadgets.pets.menu.PetsMenuFactory;
import net.ungespielt.lobby.spigot.util.SkullFactory;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * The view model for {@link GadgetsController}.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
@Singleton
public class GadgetsViewModel extends FeatureViewModel {

    /**
     * The message prefix.
     */
    private final String prefix;

    /**
     * The item stack for the menu.
     */
    private final ItemStack gadgetsItemStack;

    /**
     * The slot for the gadget item.
     */
    private final int gadgetsItemSlot;

    /**
     * The factory for the gadgets menu.
     */
    private final GadgetsMenuFactory gadgetsMenuFactory;

    /**
     * The factory for the pets menu.
     */
    private final PetsMenuFactory petsMenuFactory;

    /**
     * The factory for the morphs menu.
     */
    private final MorphsMenuFactory morphsMenuFactory;

    /**
     * The factory for the heads menu.
     */
    private final HeadsMenuFactory headsMenuFactory;

    /**
     * The pets manager.
     */
    private final PetsManager petsManager;

    /**
     * The morphs manager.
     */
    private final MorphsManager morphsManager;

    /**
     * The heads manager.
     */
    private final HeadsManager headsManager;

    /**
     * The data manager for the player heads.
     */
    private final PlayerHeadDataManager playerHeadDataManager;

    /**
     * The data manager for the player morphs.
     */
    private final PlayerMorphDataManager playerMorphDataManager;

    /**
     * The player pets data manager.
     */
    private final PlayerPetsDataManager playerPetsDataManager;

    /**
     * The rx manager.
     */
    private final RxManager rxManager;

    /**
     * The skull factory.
     */
    private final SkullFactory skullFactory;

    /**
     * The player preferences data manager.
     */
    private final PlayerPreferencesDataManager playerPreferencesDataManager;

    /**
     * Create a new gadgets view model.
     *
     * @param prefix                       The message prefix.
     * @param gadgetsItemStack             The item stack.
     * @param gadgetsItemSlot              The slot for the gadget item.
     * @param gadgetsMenuFactory           The gadgets menu factory.
     * @param petsMenuFactory              The pets menu factory.
     * @param morphsMenuFactory            The morphs menu factory.
     * @param headsMenuFactory             The heads menu factory.
     * @param petsManager                  The pets manager.
     * @param morphsManager                The morphs manager.
     * @param headsManager                 The heads manager.
     * @param playerHeadDataManager        The data manager for the player heads.
     * @param playerMorphDataManager       The data manager for the player morphs.
     * @param playerPetsDataManager        The player pets data manager.
     * @param rxManager                    The rx manager.
     * @param skullFactory                 The skull factory.
     * @param playerPreferencesDataManager The data manager for the player preferences.
     */
    @Inject
    public GadgetsViewModel(@Named("prefix") String prefix, @Named("gadgetsItemStack") ItemStack gadgetsItemStack, @Named("gadgetsItemSlot") int gadgetsItemSlot, GadgetsMenuFactory gadgetsMenuFactory, PetsMenuFactory petsMenuFactory, MorphsMenuFactory morphsMenuFactory, HeadsMenuFactory headsMenuFactory, PetsManager petsManager, MorphsManager morphsManager, HeadsManager headsManager, PlayerHeadDataManager playerHeadDataManager, PlayerMorphDataManager playerMorphDataManager, PlayerPetsDataManager playerPetsDataManager, RxManager rxManager, SkullFactory skullFactory, PlayerPreferencesDataManager playerPreferencesDataManager) {
        this.prefix = prefix;
        this.gadgetsItemStack = gadgetsItemStack;
        this.gadgetsItemSlot = gadgetsItemSlot;
        this.gadgetsMenuFactory = gadgetsMenuFactory;
        this.petsMenuFactory = petsMenuFactory;
        this.morphsMenuFactory = morphsMenuFactory;
        this.headsMenuFactory = headsMenuFactory;
        this.petsManager = petsManager;
        this.morphsManager = morphsManager;
        this.headsManager = headsManager;
        this.playerHeadDataManager = playerHeadDataManager;
        this.playerMorphDataManager = playerMorphDataManager;
        this.playerPetsDataManager = playerPetsDataManager;
        this.rxManager = rxManager;
        this.skullFactory = skullFactory;
        this.playerPreferencesDataManager = playerPreferencesDataManager;
    }

    /**
     * Handle a player join.
     *
     * @param player The player.
     */
    public void handlePlayerJoin(Player player) {
        playerHeadDataManager.getPlayerHead(player.getUniqueId())
                .take(1)
                .filter(s -> !s.isEmpty() && !s.equalsIgnoreCase("NONE"))
                .subscribe(skullOwner -> applyHead(player, skullFactory.createSkull(skullOwner)));

        playerMorphDataManager.getPlayerMorph(player.getUniqueId())
                .take(1)
                .filter(entityType -> entityType != EntityType.UNKNOWN)
                .subscribe(entityType -> morphPlayer(player, entityType));

        playerPetsDataManager.getPlayerPet(player.getUniqueId())
                .take(1)
                .filter(entityType -> entityType != EntityType.UNKNOWN)
                .subscribe(entityType -> createPet(player, entityType));

        playerPreferencesDataManager.getPlayerVisibilityState(player.getUniqueId())
                .takeUntil(rxManager.observeEvent(PlayerQuitEvent.class).take(1).filter(playerQuitEvent -> playerQuitEvent.getPlayer() == player))
                .subscribeOn(Schedulers.computation())
                .subscribe(playerVisibilityState -> {
                    petsManager.refreshPets(player);
                    morphsManager.refreshMorphs(player);
                });

        player.getInventory().setItem(gadgetsItemSlot, gadgetsItemStack);
    }

    /**
     * Handle a player quit.
     *
     * @param player The player.
     */
    public void handlePlayerQuit(Player player) {
        player.getInventory().setItem(gadgetsItemSlot, null);

        headsManager.removeHead(player);
        petsManager.removePet(player, PetRemoveReason.CLEANUP);
        morphsManager.unmorphPlayer(player);
    }

    /**
     * Open the overview with all gadgets.
     *
     * @param player The player.
     */
    public void openGadgetsOverview(Player player) {
        Menu gadgetMenu = gadgetsMenuFactory.createGadgetMenu(player);
        gadgetMenu.open();
    }

    /**
     * Open the menu for the pets.
     *
     * @param player The player.
     */
    public void openPetsMenu(Player player) {
        Menu menu = petsMenuFactory.createPetsMenu(player);
        menu.open();
    }

    /**
     * Open the menu for the morphs.
     *
     * @param player The player.
     */
    public void openMorphsMenu(Player player) {
        Menu menu = morphsMenuFactory.createMorphsMenu(player);
        menu.open();
    }

    /**
     * Open the menu for the heads.
     *
     * @param player The player.
     */
    public void openHeadsMenu(Player player) {
        Menu menu = headsMenuFactory.createHeadsMenu(player);
        menu.open();
    }

    /**
     * Handle that the player wants a head of the given item stack.
     *
     * @param player  The player.
     * @param content The content.
     */
    public void handleApplyHeadClicked(Player player, ItemStack content) {
        boolean hasHead = headsManager.hasHead(player);

        if (hasHead) {
            player.sendMessage(prefix + "Du hast bereits einen Kopf auf deinem Kopf. Niemand hat die Absicht ein Kopftürmchen zu bauen.");
            return;
        }

        Head head = applyHead(player, content);
        playerHeadDataManager.setHead(player.getUniqueId(), head.getOwner());
    }

    /**
     * Apply the given head to the player.
     *
     * @param player    The player.
     * @param itemStack The item stack for the head.
     */
    private Head applyHead(Player player, ItemStack itemStack) {
        return headsManager.applyHead(player, itemStack);
    }

    /**
     * Handle that the given player wants to unmorph.
     *
     * @param player The player.
     */
    public void handleRemoveHeadClicked(Player player) {
        boolean hasHead = headsManager.hasHead(player);

        if (!hasHead) {
            player.sendMessage(prefix + "Wir können dich ja wohl kaum noch einen Kopf kürzer machen.");
            return;
        }

        removeHead(player);
        playerHeadDataManager.removeHead(player.getUniqueId());
    }

    /**
     * Remove the head of the given player.
     *
     * @param player The player.
     */
    private void removeHead(Player player) {
        headsManager.removeHead(player);
    }

    /**
     * Handle that the given player wants to morph as the given entity type.
     *
     * @param player  The player.
     * @param content The entity type.
     */
    public void handleMorphPlayerClicked(Player player, EntityType content) {
        if (morphsManager.isMorphed(player)) {
            player.sendMessage(prefix + "Manche Dinge gehören einfach nicht zusammen. Zwei Morphs zur gleichen Zeit zum Beispiel.");
            return;
        }

        morphPlayer(player, content);
        playerMorphDataManager.setMorph(player.getUniqueId(), content);
    }

    /**
     * Morph the given player as the given entity.
     *
     * @param player  The player.
     * @param content The entity.
     */
    private void morphPlayer(Player player, EntityType content) {
        morphsManager.morphPlayer(player, content);
    }

    /**
     * Handle that the given player wants to unmorph.
     *
     * @param player The player.
     */
    public void handleUnmorphPlayerClicked(Player player) {
        if (!morphsManager.isMorphed(player)) {
            player.sendMessage(prefix + "Wenn wir von dir noch mehr Zauber nehmen verschwindest du in einem bedeutungslosen Nichts. Du bist nicht gemorphed und kannst dich so auch nicht entmorphen.");
            return;
        }

        unmorphPlayer(player);
        playerMorphDataManager.removeMorph(player.getUniqueId());
    }

    /**
     * Unmorph the given player.
     *
     * @param player The player.
     */
    private void unmorphPlayer(Player player) {
        morphsManager.unmorphPlayer(player);
    }

    /**
     * Handle that the given head was applied to the given player.
     *
     * @param player The player.
     * @param head   The head.
     */
    public void handlePlayerHeadApply(Player player, Head head) {
        player.sendMessage(prefix + "Du hast dir den Kopf von " + head.getOwner() + " aufgesetzt.");
    }

    /**
     * Handle that the head of the given player was removed.
     *
     * @param player The player.
     * @param head   The head.
     */
    public void handlePlayerHeadRemove(Player player, Head head) {
        player.sendMessage(prefix + "Du wurdest einen Kopf kürzer gemacht. Du hast deinen Head verloren ;(");
    }

    /**
     * Handle that the player morphed as the given morph.
     *
     * @param player The player.
     * @param morph  The morph.
     */
    public void handlePlayerMorph(Player player, Morph morph) {
        player.sendMessage(prefix + "Du hast dich sehr verändert. Es sieht aus als würdest du an " + morph.getEntityType().getName() + "-itis leiden.");
    }

    /**
     * Handle that the given player removed the given morph.
     *
     * @param player The player.
     * @param morph  The morph.
     */
    public void handlePlayerUnmorph(Player player, Morph morph) {
        player.sendMessage(prefix + "Du siehst wieder ganz normal aus. Dein Morph ist weg.");
    }

    /**
     * Handle that the player created the given pet.
     *
     * @param player The player.
     * @param pet    The pet.
     */
    public void handlePetCreate(Player player, Pet pet) {
        player.sendMessage(prefix + "Dein neues Haustier: " + pet.getEntityType().getName());
    }

    /**
     * Handle that the given player removed the given pet.
     *
     * @param player The player.
     * @param pet    The pet.
     * @param reason The reason.
     */
    public void handlePetRemove(Player player, Pet pet, PetRemoveReason reason) {
        if (reason == PetRemoveReason.PURGE) {
            player.sendMessage(prefix + "Du hast nun kein Haustier mehr.");
        }
    }

    /**
     * Handle that the player clicked that he wants to remove his pet.
     *
     * @param player The player.
     */
    public void handleRemovePetClicked(Player player) {
        if (!petsManager.hasPet(player)) {
            player.sendMessage(prefix + "Du kannst nur mit dem arbeiten was du hast.");
            return;
        }

        removePet(player);
        playerPetsDataManager.removePet(player.getUniqueId());
    }

    /**
     * Remove the pet of the given player.
     *
     * @param player The player.
     */
    private void removePet(Player player) {
        petsManager.removePet(player);
    }

    /**
     * Handle that the payer clicked that he wants to create a pet.
     *
     * @param player  The player.
     * @param content The type of the pet.
     */
    public void handleCreatePetClicked(Player player, EntityType content) {
        if (petsManager.hasPet(player)) {
            petsManager.removePet(player, PetRemoveReason.REPLACE);
        }

        createPet(player, content);
        playerPetsDataManager.setPet(player.getUniqueId(), content);
    }

    /**
     * Create the given pet for the player.
     *
     * @param player  The player.
     * @param content The type of the pet.
     */
    private void createPet(Player player, EntityType content) {
        petsManager.createPet(player, content, false);
    }
}
