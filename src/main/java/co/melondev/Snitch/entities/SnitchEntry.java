package co.melondev.Snitch.entities;

import co.melondev.Snitch.enums.EnumAction;
import co.melondev.Snitch.enums.EnumActionVariables;
import co.melondev.Snitch.util.ItemUtil;
import com.google.gson.JsonObject;
import net.minecraft.server.v1_8_R3.MojangsonParseException;

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

    public long getTimestamp() {
        return timestamp;
    }

    public String getDescriptor() {
        String base = getAction().getExplained();

        base = base.replace("%actor", getSnitchPlayer().getPlayerName());

        for(EnumActionVariables var : EnumActionVariables.values()){
            if (data.has(var.getKey())){
                base = base.replace("%" + var.getKey(), var.getReplacement(data.get(var.getKey()).getAsJsonObject()));
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
