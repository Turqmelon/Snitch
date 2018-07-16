package co.melondev.Snitch.entities;

import co.melondev.Snitch.enums.EnumAction;
import com.google.gson.JsonObject;

/**
 * Created by Devon on 7/16/18.
 */
public class GroupedSnitchEntry extends SnitchEntry {

    private int count;

    public GroupedSnitchEntry(int id, EnumAction action, SnitchPlayer snitchPlayer, SnitchWorld snitchWorld, SnitchPosition snitchPosition, long timestamp, JsonObject data, int count) {
        super(id, action, snitchPlayer, snitchWorld, snitchPosition, timestamp, data);
        this.count = count;
    }

    public int getCount() {
        return count;
    }
}
