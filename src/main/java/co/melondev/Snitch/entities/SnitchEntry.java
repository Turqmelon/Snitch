package co.melondev.Snitch.entities;

import co.melondev.Snitch.SnitchPlugin;
import co.melondev.Snitch.enums.EnumAction;
import co.melondev.Snitch.enums.EnumActionVariables;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SnitchEntry {

    private int id;
    private EnumAction action;
    private SnitchPlayer snitchPlayer;
    private SnitchWorld snitchWorld;
    private SnitchPosition snitchPosition;
    private long timestamp;
    private JsonObject data;

    public SnitchEntry(int id, EnumAction action, SnitchPlayer snitchPlayer, SnitchWorld snitchWorld, SnitchPosition snitchPosition, long timestamp, JsonObject data) {
        this.id = id;
        this.action = action;
        this.snitchPlayer = snitchPlayer;
        this.snitchWorld = snitchWorld;
        this.snitchPosition = snitchPosition;
        this.timestamp = timestamp;
        this.data = data;
    }

    public SnitchEntry(ResultSet set) throws SQLException {
        this.id = set.getInt("id");
        this.action = EnumAction.getById(set.getInt("action_id"));
        this.snitchPlayer = SnitchPlugin.getInstance().getStorage().getPlayer(set.getInt("player_id"));
        this.snitchWorld = SnitchPlugin.getInstance().getStorage().getWorld(set.getInt("world_id"));
        this.snitchPosition = new SnitchPosition(set);
        this.timestamp = set.getLong("timestamp");
        this.data = new JsonParser().parse(set.getString("data")).getAsJsonObject();
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getDescriptor() {
        String base = getAction().getExplained();

        base = base.replace("%actor", "§6" + getSnitchPlayer().getPlayerName() + "§7");

        for(EnumActionVariables var : EnumActionVariables.values()){
            if (data.has(var.getKey())){
                base = base.replace("%" + var.getKey(), "§6" + var.getReplacement(data.get(var.getKey()).getAsJsonObject()) + "§7");
            }
        }

        return base;
    }

    public boolean isReverted(){
        return false;
    }

    public int getId() {
        return id;
    }

    public EnumAction getAction() {
        return action;
    }

    public SnitchPlayer getSnitchPlayer() {
        return snitchPlayer;
    }

    public SnitchWorld getSnitchWorld() {
        return snitchWorld;
    }

    public SnitchPosition getSnitchPosition() {
        return snitchPosition;
    }

    public JsonObject getData() {
        return data;
    }
}
