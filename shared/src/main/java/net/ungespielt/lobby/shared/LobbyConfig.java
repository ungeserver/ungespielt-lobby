package net.ungespielt.lobby.shared;

import java.util.Arrays;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class LobbyConfig {

    private String messagePrefix;
    private String[] bossBarContent;

    public LobbyConfig() {
    }

    public LobbyConfig(String[] bossBarContent) {
        this.bossBarContent = bossBarContent;
    }

    public String[] getBossBarContent() {
        return bossBarContent;
    }

    public String getMessagePrefix() {
        return messagePrefix;
    }

    public void setBossBarContent(String[] bossBarContent) {
        this.bossBarContent = bossBarContent;
    }

    @Override
    public String toString() {
        return "LobbyConfig{" +
                "messagePrefix='" + messagePrefix + '\'' +
                ", bossBarContent=" + Arrays.toString(bossBarContent) +
                '}';
    }
}
