package co.melondev.Snitch.entities;

import java.util.UUID;

public class SnitchPlayer {

    private int id;
    private UUID uuid;
    private String playerName;

    public SnitchPlayer(int id, UUID uuid, String playerName) {
        this.id = id;
        this.uuid = uuid;
        this.playerName = playerName;
    }

    public int getId() {
        return id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getPlayerName() {
        return playerName;
    }
}
