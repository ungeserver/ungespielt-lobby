package net.ungespielt.lobby.spigot.api.feature.gadgets.pets;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
public enum PetRemoveReason {

    /**
     * When the server cleaned the pet up.
     */
    CLEANUP,

    /**
     * The pet is replaced with a new pet.
     */
    REPLACE,

    /**
     * The pet gets completely purged and none new is created.
     */
    PURGE
}
