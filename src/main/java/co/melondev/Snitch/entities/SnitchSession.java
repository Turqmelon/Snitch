package co.melondev.Snitch.entities;

import co.melondev.Snitch.util.Previewable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Devon on 7/14/18.
 */
public class SnitchSession {

    private UUID playerUUID;
    private SnitchQuery query;
    private List<SnitchEntry> entries;
    private int page;

    private Previewable activePreview = null;
    private List<Location> adjustedBlocks = new ArrayList<>();

    public SnitchSession(Player player, SnitchQuery query, List<SnitchEntry> entries, int page) {
        this.playerUUID = player.getUniqueId();
        this.query = query;
        this.entries = entries;
        this.page = page;
    }

    public List<Location> getAdjustedBlocks() {
        return adjustedBlocks;
    }

    public void setAdjustedBlocks(List<Location> adjustedBlocks) {
        this.adjustedBlocks = adjustedBlocks;
    }

    public void recordAdjustedBlock(Location location) {
        this.adjustedBlocks.add(location);
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(this.playerUUID);
    }

    public Previewable getActivePreview() {
        return activePreview;
    }

    public void setActivePreview(Previewable activePreview) {
        this.activePreview = activePreview;
    }

    public SnitchQuery getQuery() {
        return query;
    }

    public void setQuery(SnitchQuery query) {
        this.query = query;
    }

    public List<SnitchEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<SnitchEntry> entries) {
        this.entries = entries;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
