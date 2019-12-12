package net.ungespielt.lobby.spigot.api;

/**
 * Interface for the central management application.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
public interface LobbyApplication {

    /**
     * Initialize the application.
     */
    void init();

    /**
     * Destroy the application.
     */
    void destroy();
}
