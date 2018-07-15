package co.melondev.Snitch.entities;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SnitchPlayer {

    private int id;
    private UUID uuid;
    private String playerName;

    public SnitchPlayer(ResultSet set) throws SQLException {
        this.id = set.getInt("id");
        this.uuid = UUID.fromString(set.getString("uuid"));
        this.playerName = set.getString("player_name");
    }

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

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
}
