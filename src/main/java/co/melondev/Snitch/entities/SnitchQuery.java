package co.melondev.Snitch.entities;

import co.melondev.Snitch.enums.EnumAction;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SnitchQuery {

    private List<SnitchPlayer> players = new ArrayList<>();
    private List<EnumAction> actions = new ArrayList<>();
    private long since;
    private long before;
    private SnitchWorld world = null;
    private SnitchPosition position = null;
    private double range = -1;
    private String search = null;

    public SnitchQuery() {
        this.since = System.currentTimeMillis() - TimeUnit.DAYS.toDays(3);
        this.before = System.currentTimeMillis() + 1000;
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

    public void setSearch(String search){
        this.search = search;
    }

    public void setRadius(SnitchPosition position, double radius){
        this.position = position;
        this.range = radius;
    }

    public void setWorld(SnitchWorld world){
        this.world = world;
    }

    public void setSinceTime(long since){
        this.since = since;
    }

    public void setBeforeTime(long before){
        this.before = before;
    }

    public void addActions(EnumAction... actions){
        for(EnumAction action : actions){
            if (!getActions().contains(action)){
                this.actions.add(action);
            }
        }
    }

    public void addPlayers(SnitchPlayer... players){
        for(SnitchPlayer pl : players){
            if (!getPlayers().contains(pl)){
                this.players.add(pl);
            }
        }
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

    public SnitchPosition getPosition() {
        return position;
    }

    public double getRange() {
        return range;
    }

    public String getSearch() {
        return search;
    }
}
