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
import java.util.List;

public class SnitchQuery {

    private List<SnitchPlayer> players = new ArrayList<>();
    private List<EnumAction> actions = new ArrayList<>();
    private long since;
    private long before;
    private SnitchWorld world = null;
    private SnitchPosition position = null;
    private double range = -1;

    public SnitchQuery() {
        this.since = System.currentTimeMillis() - SnitchPlugin.getInstance().getConfiguration().getDefaultTime();
        this.before = System.currentTimeMillis() + 1000;
    }

    public SnitchQuery since(long time) {
        this.since = time;
        return this;
    }

    public SnitchQuery range(double range) {
        this.range = range;
        return this;
    }

    public SnitchQuery relativeTo(SnitchPosition position) {
        this.position = position;
        return this;
    }

    public SnitchQuery inWorld(World world) throws SQLException {
        return inWorld(SnitchPlugin.getInstance().getStorage().register(world));
    }


    public SnitchQuery inWorld(SnitchWorld world) {
        this.world = world;
        return this;
    }

    public SnitchQuery before(long time) {
        this.before = time;
        return this;
    }

    public String getSearchSummary() {
        StringBuilder d = new StringBuilder();
        if (actions.isEmpty()) {
            d.append("All actions from ");
        } else {
            List<String> names = new ArrayList<>();
            for (EnumAction action : this.actions) {
                names.add(action.getFriendlyFullName());
            }
            d.append(String.join(", ", names)).append("from ");
        }
        if (players.isEmpty()) {
            d.append("all players");
        } else {
            List<String> names = new ArrayList<>();
            for (SnitchPlayer pl : this.players) {
                names.add(pl.getPlayerName());
            }
            d.append(String.join(", ", names));
        }
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yy hh:mm a");
        if (since > 0) {
            d.append(" since ").append(df.format(since));
        }
        if (before > 0) {
            d.append(" before ").append(df.format(before));
        }

        if (this.range > 0) {
            d.append(" within ").append(new DecimalFormat("#.#").format(range)).append(" blocks");
            if (this.position != null) {
                d.append(" of ").append(this.position.getX()).append("x, ").append(this.position.getY()).append("y, ").append(this.position.getZ()).append("z");
            }
        } else {
            d.append(" everywhere");
        }

        if (this.world != null) {
            d.append(" in ").append(this.world.getWorldName());
        } else {
            d.append(" in all worlds");
        }

        return d.toString();
    }

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

    private boolean isKeyWord(String s) {
        return EnumParam.getByKeyword(s) != null;
    }

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

    public boolean analyzePermissions(Player player){

        if (!player.hasPermission("snitch.actions.all")){
            if (this.actions.isEmpty()){
                for(EnumAction action : EnumAction.values()){
                    if (player.hasPermission(action.getNode())){
                        this.actions.add(action);
                    }
                }
            }
            for(EnumAction action : this.actions){
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

    public SnitchQuery addActions(EnumAction... actions) {
        for(EnumAction action : actions){
            if (!getActions().contains(action)){
                this.actions.add(action);
            }
        }
        return this;
    }

    public SnitchQuery addPlayers(SnitchPlayer... players) {
        for(SnitchPlayer pl : players){
            if (!getPlayers().contains(pl)){
                this.players.add(pl);
            }
        }
        return this;
    }

    public List<SnitchPlayer> getPlayers() {
        return players;
    }

    public List<EnumAction> getActions() {
        return actions;
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
