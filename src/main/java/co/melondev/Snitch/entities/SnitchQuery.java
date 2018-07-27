package co.melondev.Snitch.entities;

import co.melondev.Snitch.SnitchPlugin;
import co.melondev.Snitch.enums.EnumAction;
import co.melondev.Snitch.enums.EnumParam;
import org.apache.commons.lang.Validate;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A search request within Snitch.
 */
public class SnitchQuery {

    /**
     * A map of players to search for, and whether or not they're excluded
     */
    private Map<SnitchPlayer, Boolean> players = new HashMap<>();

    /**
     * A map of actions to search for, and whether or not they're excluded
     */
    private Map<EnumAction, Boolean> actions = new HashMap<>();

    /**
     * The time to search for records since. We default this to current time - {@link co.melondev.Snitch.util.Config#defaultTime}
     */
    private long since;

    /**
     * The time to search for records before. We default this to the current time.
     */
    private long before;

    /**
     * Whether or not to filter entries to a specific world. If this is null, we search all worlds.
     */
    private SnitchWorld world = null;

    /**
     * Whether or not this query is relative to a specific position.
     */
    private SnitchPosition position = null;

    /**
     * The range to check around {@link #position}
     */
    private double range = -1;

    /**
     * Whether or not this is an exact position search. (For the Inspector)
     */
    private boolean useExactPosition = false;

    /**
     * Whether or not to limit records. -1 means no limit.
     */
    private int limit = -1;

    public SnitchQuery() {
        this.since = System.currentTimeMillis() - SnitchPlugin.getInstance().getConfiguration().getDefaultTime();
        this.before = System.currentTimeMillis() + 1000;
    }

    /**
     * Specifies a result limit
     *
     * @param limit the result limit
     * @return the {@link #SnitchQuery()}
     */
    public SnitchQuery limit(int limit) {
        this.limit = limit;
        return this;
    }

    /**
     *
     * @return whether or not a limit is defined
     */
    public boolean hasLimit() {
        return this.limit > 0;
    }

    /**
     *
     * @return the result limit
     */
    public int getLimit() {
        return limit;
    }

    /**
     * Instruct the Query to use an exact position for lookup
     * @return the {@link SnitchQuery}
     */
    public SnitchQuery exactPosition() {
        this.useExactPosition = true;
        return this;
    }

    /**
     *
     * @return whether or not this is an exact position lookup
     */
    public boolean isUseExactPosition() {
        return useExactPosition;
    }

    /**
     * Specify the time to search records since
     * @param time      the minimum time
     * @return the {@link SnitchQuery}
     */
    public SnitchQuery since(long time) {
        this.since = time;
        return this;
    }

    /**
     * Specify the radius to search around. You must also specify a {@link SnitchWorld} and a {@link SnitchPosition}
     * @param range     the range to search around
     * @return the {@link SnitchQuery}
     */
    public SnitchQuery range(double range) {
        this.range = range;
        return this;
    }

    /**
     * Specify a position that this query is based on
     * @param position      the position to search from
     * @return the {@link SnitchQuery}
     */
    public SnitchQuery relativeTo(SnitchPosition position) {
        this.position = position;
        return this;
    }

    /**
     * Specify a world to search within
     * @param world         the world to search within
     * @return this {@link SnitchQuery}
     * @throws SQLException if this world can't be matched to a {@link SnitchWorld}
     */
    public SnitchQuery inWorld(World world) throws SQLException {
        return inWorld(SnitchPlugin.getInstance().getStorage().register(world));
    }


    /**
     * Specify a world to search within
     * @param world     the world to search within
     * @return this {@link SnitchQuery}
     */
    public SnitchQuery inWorld(SnitchWorld world) {
        this.world = world;
        return this;
    }

    /**
     * Specify the maximum time
     * @param time      the time to search before
     * @return this {@link SnitchQuery}
     */
    public SnitchQuery before(long time) {
        this.before = time;
        return this;
    }

    /**
     * Returns a human readable string to explain this query
     * @return the explanation string
     */
    public String getSearchSummary() {
        StringBuilder d = new StringBuilder();
        List<EnumAction> excludedActions = getExcludedActions();
        if (getActions().isEmpty()) {
            d.append("All actions");
            if (!excludedActions.isEmpty()) {
                d.append(" except ");
                List<String> names = new ArrayList<>();
                for (EnumAction action : excludedActions) {
                    names.add(action.getFriendlyFullName());
                }
                d.append(String.join(", ", names));
            }
            d.append(" from ");
        } else {
            List<String> names = new ArrayList<>();
            for (EnumAction action : getActions()) {
                names.add(action.getFriendlyFullName());
            }
            d.append(String.join(", ", names));
            if (!excludedActions.isEmpty()) {
                d.append(", excluding ");
                names = new ArrayList<>();
                for (EnumAction action : excludedActions) {
                    names.add(action.getFriendlyFullName());
                }
                d.append(String.join(", ", names));
            }
            d.append(" from ");
        }
        List<SnitchPlayer> excludedPlayers = getExcludedPlayers();
        if (getPlayers().isEmpty()) {
            d.append("all players");
            if (!excludedPlayers.isEmpty()) {
                d.append(", except ");
                List<String> names = new ArrayList<>();
                for (SnitchPlayer pl : excludedPlayers) {
                    names.add(pl.getPlayerName());
                }
                d.append(String.join(", ", names));
            }
        } else {
            List<String> names = new ArrayList<>();
            for (SnitchPlayer pl : getPlayers()) {
                names.add(pl.getPlayerName());
            }
            d.append(String.join(", ", names));
            if (!excludedPlayers.isEmpty()) {
                d.append(", excluding ");
                names = new ArrayList<>();
                for (SnitchPlayer pl : excludedPlayers) {
                    names.add(pl.getPlayerName());
                }
                d.append(String.join(", ", names));
            }
        }
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yy hh:mm a");
        if (since > 0) {
            d.append(" since ").append(df.format(since));
        }
        if (before > 0 && before < System.currentTimeMillis()) {
            d.append(" before ").append(df.format(before));
        }

        if (this.range > 0) {
            d.append(" within ").append(new DecimalFormat("#.#").format(range)).append(" blocks");
            if (this.position != null) {
                d.append(" of ").append(this.position.getX()).append("x, ").append(this.position.getY()).append("y, ").append(this.position.getZ()).append("z");
            }
        } else if (this.isUseExactPosition() && this.position != null) {
            d.append(" at ").append(this.position.getX()).append("x, ").append(this.position.getY()).append("y, ").append(this.position.getZ()).append("z");
        } else {
            d.append(" everywhere");
        }

        if (this.world != null) {
            d.append(" in ").append(this.world.getWorldName());
        } else {
            d.append(" in all worlds");
        }
        if (hasLimit() && getLimit() != 1000) {
            d.append(", limited to ").append(getLimit()).append(" results");
        }

        return d.toString();
    }

    /**
     * Determines if this is a valid area selection (requires {@link #world}, {@link #position} and {@link #range} to be set
     * @return if this is a valid area query
     */
    public boolean isAreaSelection() {
        return world != null && position != null && range > 0;
    }

    /**
     * Get the values following a keyword from a snitch command
     * @param args      the list of arguments
     * @param offset    the offset to start from
     * @return an array of valid values for a parameter
     */
    private String[] getValues(List<String> args, int offset) {
        int i;
        for (i = offset; i < args.size(); i++) {
            if (isKeyWord(args.get(i))) {
                break;
            }
        }
        if (i == offset) {
            return new String[0];
        }

        final String[] values = new String[i - offset];
        for (int j = offset; j < i; j++) {
            String value = args.get(j);
            if (value.startsWith("\"") && value.endsWith("\"")) {
                value = value.substring(1, value.length() - 1);
            }
            values[j - offset] = value;
        }
        return values;
    }

    /**
     * Checks if a string is a param keyword
     * @param s     the string to check
     * @return if this is a param keyword
     */
    private boolean isKeyWord(String s) {
        return EnumParam.getByKeyword(s) != null;
    }

    /**
     * Interprets the provided arguments as parameters and their values
     * @param player            the player performing the search
     * @param arguments         the arguments from {@link co.melondev.Snitch.commands.SnitchCommand}
     * @return true if the params were set successfully
     * @throws SQLException     if there's any database errors
     */
    public boolean parseParams(Player player, List<String> arguments) throws SQLException {

        if (arguments.isEmpty()) {
            throw new IllegalArgumentException("No params specified.");
        }

        boolean paramParsed = false;
        for (int i = 0; i < arguments.size(); i++) {
            String paramName = arguments.get(i);
            String[] values = getValues(arguments, i + 1);
            EnumParam param = EnumParam.getByKeyword(paramName);
            if (param != null) {
                param.parse(player, this, values);
                paramParsed = true;
            }
        }

        Validate.isTrue(paramParsed, "No valid parameters specified.");

        analyzePermissions(player);
        return true;
    }

    /**
     * Ensures that the provided player has permission to use the parameters within this query
     * @param player            the player to check the permissions of
     * @return whether or not the player has permissions to continue
     */
    public boolean analyzePermissions(Player player){

        if (!player.hasPermission("snitch.actions.all")){
            if (this.actions.isEmpty()){
                for(EnumAction action : EnumAction.values()){
                    if (player.hasPermission(action.getNode())){
                        this.actions.put(action, true);
                    }
                }
            }
            for (EnumAction action : actions.keySet()) {
                if (!player.hasPermission(action.getNode())){
                    throw new IllegalArgumentException("You don't have permission for action [" + action.name() + "].");
                }
            }
        }

        if (!player.hasPermission("snitch.range.global")){
            if (this.range == -1){
                throw new IllegalArgumentException("You don't have permission to perform a global lookup. You need to specify a radius.");
            }
        }

        return true;
    }

    public void setRadius(SnitchPosition position, double radius){
        this.position = position;
        this.range = radius;
    }

    public void setSinceTime(long since){
        this.since = since;
    }

    public void setBeforeTime(long before){
        this.before = before;
    }

    /**
     * Adds an excluded action. Excluded actions are removed from search results.
     * @param actions       the actions to exclude
     * @return this {@link SnitchQuery}
     */
    public SnitchQuery addExcludedAction(EnumAction... actions) {
        for (EnumAction action : actions) {
            if (!getActions().contains(action)) {
                this.actions.put(action, false);
            }
        }
        return this;
    }

    /**
     * Adds an action. If no actions are specified, then ALL actions are searched.
     * @param actions       the actions to search for
     * @return thos {@link SnitchQuery}
     */
    public SnitchQuery addActions(EnumAction... actions) {
        for(EnumAction action : actions){
            if (!getActions().contains(action)){
                this.actions.put(action, true);
            }
        }
        return this;
    }

    /**
     * Adds an excluded player. If specified, these players will be removed from search results.
     * @param players       the players to exclude
     * @return this {@link SnitchQuery}
     */
    public SnitchQuery addExcludedPlayer(SnitchPlayer... players) {
        for (SnitchPlayer pl : players) {
            if (!getPlayers().contains(pl)) {
                this.players.put(pl, false);
            }
        }
        return this;
    }

    /**
     * Adds a player to search for. If none is specified, ALL players are searched.
     * @param players       the players to search for
     * @return this {@link SnitchQuery}
     */
    public SnitchQuery addPlayers(SnitchPlayer... players) {
        for(SnitchPlayer pl : players){
            if (!getPlayers().contains(pl)){
                this.players.put(pl, true);
            }
        }
        return this;
    }

    /**
     *
     * @return a list of excluded actions
     */
    public List<EnumAction> getExcludedActions() {
        List<EnumAction> list = new ArrayList<>();
        for (Map.Entry<EnumAction, Boolean> entry : this.actions.entrySet()) {
            if (!entry.getValue()) {
                list.add(entry.getKey());
            }
        }
        return list;
    }

    /**
     *
     * @return a list of excluded players
     */
    public List<SnitchPlayer> getExcludedPlayers() {
        List<SnitchPlayer> list = new ArrayList<>();
        for (Map.Entry<SnitchPlayer, Boolean> entry : this.players.entrySet()) {
            if (!entry.getValue()) {
                list.add(entry.getKey());
            }
        }
        return list;
    }

    /**
     *
     * @return a list of players to search for
     */
    public List<SnitchPlayer> getPlayers() {
        List<SnitchPlayer> list = new ArrayList<>();
        for (Map.Entry<SnitchPlayer, Boolean> entry : this.players.entrySet()) {
            if (entry.getValue()) {
                list.add(entry.getKey());
            }
        }
        return list;
    }

    /**
     *
     * @return a list of actions to search for
     */
    public List<EnumAction> getActions() {
        List<EnumAction> list = new ArrayList<>();
        for (Map.Entry<EnumAction, Boolean> entry : this.actions.entrySet()) {
            if (entry.getValue()) {
                list.add(entry.getKey());
            }
        }
        return list;
    }

    public long getSince() {
        return since;
    }

    public long getBefore() {
        return before;
    }

    public SnitchWorld getWorld() {
        return world;
    }

    public void setWorld(SnitchWorld world) {
        this.world = world;
    }

    public SnitchPosition getPosition() {
        return position;
    }

    public void setPosition(SnitchPosition position) {
        this.position = position;
    }

    public double getRange() {
        return range;
    }

}
