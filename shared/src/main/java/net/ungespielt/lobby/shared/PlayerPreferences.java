package net.ungespielt.lobby.shared;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class PlayerPreferences {

    /**
     * If the players scoreboard is enabled.
     */
    private boolean scoreboardEnabled;

    /**
     * The player visibility state.
     */
    private PlayerVisibilityState playerVisibilityState;

    /**
     * Create a new player preferences model.
     *
     * @param scoreboardEnabled If the scoreboard is enabled.
     * @param playerVisibilityState The player visibility state.
     */
    public PlayerPreferences(boolean scoreboardEnabled, PlayerVisibilityState playerVisibilityState) {
        this.scoreboardEnabled = scoreboardEnabled;
        this.playerVisibilityState = playerVisibilityState;
    }

    public boolean isScoreboardEnabled() {
        return scoreboardEnabled;
    }

    public void setScoreboardEnabled(boolean scoreboardEnabled) {
        this.scoreboardEnabled = scoreboardEnabled;
    }

    public PlayerVisibilityState getPlayerVisibilityState() {
        return playerVisibilityState;
    }

    public void setPlayerVisibilityState(PlayerVisibilityState playerVisibilityState) {
        this.playerVisibilityState = playerVisibilityState;
    }
}
