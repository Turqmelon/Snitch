package co.melondev.Snitch.entities;

import co.melondev.Snitch.SnitchPlugin;
import co.melondev.Snitch.enums.EnumAction;
import co.melondev.Snitch.enums.EnumActionVariables;
import com.google.gson.JsonElement;
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
    private boolean reverted;

    public SnitchEntry(int id, EnumAction action, SnitchPlayer snitchPlayer, SnitchWorld snitchWorld, SnitchPosition snitchPosition, long timestamp, JsonObject data, boolean reverted) {
        this.id = id;
        this.action = action;
        this.snitchPlayer = snitchPlayer;
        this.snitchWorld = snitchWorld;
        this.snitchPosition = snitchPosition;
        this.timestamp = timestamp;
        this.data = data;
        this.reverted = reverted;
    }

    public SnitchEntry(ResultSet set) throws SQLException {
        this.id = set.getInt("id");
        this.action = EnumAction.getById(set.getInt("action_id"));
        this.snitchPlayer = SnitchPlugin.getInstance().getStorage().getPlayer(set.getInt("player_id"));
        this.snitchWorld = SnitchPlugin.getInstance().getStorage().getWorld(set.getInt("world_id"));
        this.snitchPosition = new SnitchPosition(set);
        this.timestamp = set.getLong("timestamp");
        this.data = new JsonParser().parse(set.getString("data")).getAsJsonObject();
        this.reverted = set.getInt("is_reverted") == 1;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getDescriptor() {
        String base = getAction().getExplained();
        String crossout = isReverted() ? "§m" : "";

        base = base.replace("%actor", "§6" + crossout + getSnitchPlayer().getPlayerName() + "§7" + crossout);

        for(EnumActionVariables var : EnumActionVariables.values()){
            if (data.has(var.getKey())){
                JsonElement e = data.get(var.getKey());
                if (e.isJsonObject()) {
                    base = base.replace("%" + var.getKey(), "§6" + crossout + var.getReplacement(e.getAsJsonObject()) + "§7" + crossout);
                } else {
                    base = base.replace("%" + var.getKey(), "§6" + crossout + var.getReplacement(data.getAsJsonObject()) + "§7" + crossout);
                }
            }
        }

        return base;
    }

    public boolean isReverted(){
        return reverted;
    }

    public void setReverted(boolean reverted) {
        this.reverted = reverted;
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
