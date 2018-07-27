package co.melondev.Snitch.entities;

import co.melondev.Snitch.util.AdjustedBlock;
import org.bukkit.entity.Entity;

import java.util.List;
import java.util.Map;

/**
 * Created by Devon on 7/14/18.
 *
 * Contains results for a completed activity
 */
public class SnitchResult {

    /**
     * The stats relating to the last activity
     */
    private int applied, failed, planned;

    /**
     * Whether or not this was a preview
     */
    private boolean preview;

    /**
     * A map of any moved entities
     */
    private Map<Entity, Integer> movedEntities;

    /**
     * The query that was used for the last activity
     */
    private SnitchQuery query;

    /**
     * A list of adjusted blocks
     */
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
