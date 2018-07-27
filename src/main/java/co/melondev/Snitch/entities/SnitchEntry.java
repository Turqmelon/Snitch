package co.melondev.Snitch.entities;

import co.melondev.Snitch.SnitchPlugin;
import co.melondev.Snitch.enums.EnumAction;
import co.melondev.Snitch.enums.EnumActionVariables;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * Contains all details related to an entry in our logs table.
 */
public class SnitchEntry {

    /**
     * The internal record ID, as decided by our database of choice.
     */
    private int id;
    /**
     * The action that was recorded.
     */
    private EnumAction action;
    /**
     * The player or actor that performed this action.
     */
    private SnitchPlayer snitchPlayer;
    /**
     * The world that this action happened within.
     */
    private SnitchWorld snitchWorld;
    /**
     * The position that this happened at.
     * By not using {@link org.bukkit.Location}, we avoid loading chunks needlessly
     */
    private SnitchPosition snitchPosition;
    /**
     * The unix timestamp of this event
     */
    private long timestamp;
    /**
     * Any relavent metadata that was recorded.
     * This format is parsed and written by {@link co.melondev.Snitch.util.JsonUtil}
     */
    private JsonObject data;
    /**
     * Whether or not this entry has been reverted
     */
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

    /**
     * Takes a {@link ResultSet} as returned from querying our logs table
     *
     * @param set the set of results for this roe
     * @throws SQLException if there are any database errors thrown
     */
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

    /**
     * Returns the time in which this action happened
     * @return the unix timestamp of when this activity took place
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Gets a short explanation of what happened, replacing any necessary variables
     * @return a formatted, color coded, accurate description of the event that took place
     */
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

    /**
     *
     * @return whether or not this action has been rolled back
     */
    public boolean isReverted(){
        return reverted;
    }

    /**
     * Sets the cached value for whether or not this entry was rolled back
     * @param reverted  whether or not this entry was reverted
     */
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
