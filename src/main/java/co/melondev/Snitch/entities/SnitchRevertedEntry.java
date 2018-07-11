package co.melondev.Snitch.entities;

import co.melondev.Snitch.enums.EnumAction;
import com.google.gson.JsonObject;

public class SnitchRevertedEntry extends SnitchEntry {

    private SnitchPlayer revertingPlayer;
    private long revertTimestamp;

    public SnitchRevertedEntry(int id, EnumAction action, SnitchPlayer snitchPlayer, SnitchWorld snitchWorld, SnitchPosition snitchPosition, JsonObject data, long timestamp, SnitchPlayer revertingPlayer, long revertTimestamp) {
        super(id, action, snitchPlayer, snitchWorld, snitchPosition, timestamp, data);
        this.revertingPlayer = revertingPlayer;
        this.revertTimestamp = revertTimestamp;
    }

    public SnitchPlayer getRevertingPlayer() {
        return revertingPlayer;
    }

    public long getRevertTimestamp() {
        return revertTimestamp;
    }
}
