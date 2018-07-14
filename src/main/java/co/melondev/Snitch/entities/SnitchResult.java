package co.melondev.Snitch.entities;

import co.melondev.Snitch.util.AdjustedBlock;
import org.bukkit.entity.Entity;

import java.util.List;
import java.util.Map;

/**
 * Created by Devon on 7/14/18.
 */
public class SnitchResult {

    private int applied, failed, planned;
    private boolean preview;
    private Map<Entity, Integer> movedEntities;
    private SnitchQuery query;
    private List<AdjustedBlock> changedBlocks;

    public SnitchResult(int applied, int failed, int planned, boolean preview, Map<Entity, Integer> movedEntities, SnitchQuery query, List<AdjustedBlock> changedBlocks) {
        this.applied = applied;
        this.failed = failed;
        this.planned = planned;
        this.preview = preview;
        this.movedEntities = movedEntities;
        this.query = query;
        this.changedBlocks = changedBlocks;
    }

    public int getApplied() {
        return applied;
    }

    public int getFailed() {
        return failed;
    }

    public int getPlanned() {
        return planned;
    }

    public boolean isPreview() {
        return preview;
    }

    public Map<Entity, Integer> getMovedEntities() {
        return movedEntities;
    }

    public SnitchQuery getQuery() {
        return query;
    }

    public List<AdjustedBlock> getChangedBlocks() {
        return changedBlocks;
    }
}
