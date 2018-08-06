package co.melondev.Snitch.entities;

import co.melondev.Snitch.SnitchPlugin;
import co.melondev.Snitch.enums.EnumSnitchActivity;
import co.melondev.Snitch.util.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

/**
 * Created by Devon on 7/14/18.
 */

/**
 * Contains primary logic for rollbacks and restores.
 */
public class SnitchPreview implements Previewable {

    /**
     * The active session associated with this activity.
     */
    protected SnitchSession session;

    /**
     * The type of activity.
     */
    protected EnumSnitchActivity activity;

    /**
     * A map of the entities that were moved by this activity
     */
    protected Map<Entity, Integer> movedEntities = new HashMap<>();

    /**
     * A summary of activity statistics
     */
    protected int failed, applied, planned, changesIndex = 0;

    /**
     * A list of entries that still need to be processed
     */
    private List<SnitchEntry> pendingEntries;

    /**
     * Code to call at the end of this activity
     */
    private SnitchCallback callback;

    /**
     * We initialize a preview with a session and a callback
     *
     * @param session  the session associated with this activity. This contains the player, query, last action etc.
     * @param callback the callback to run upon completion of this activity
     */
    public SnitchPreview(SnitchSession session, SnitchCallback callback) {
        this.session = session;
        this.activity = EnumSnitchActivity.PREVIEW;
        this.pendingEntries = new ArrayList<>();
        this.pendingEntries.addAll(session.getEntries());
        this.callback = callback;
    }

    /**
     * Cancels the visualization and reverts it to those as decided by the server.
     */
    @Override
    public void cancelPreview() {
        for (Location loc : session.getAdjustedBlocks()) {
            Block block = loc.getBlock();
            session.getPlayer().sendBlockChange(block.getLocation(), block.getType(), block.getData());
        }
        MsgUtil.success("Preview cancelled.");
    }

    /**
     * Converts this preview to an actual rollback
     * Resets the statistics and redefines the callback to be {@link co.melondev.Snitch.entities.SnitchRollback.DefaultRollbackCallback}
     */
    @Override
    public void applyPreview() {
        session.getPlayer().sendMessage(MsgUtil.info("Applying rollback: " + session.getQuery().getSearchSummary().toLowerCase() + "..."));
        this.activity = EnumSnitchActivity.ROLLBACK;
        this.applied = 0;
        this.failed = 0;
        this.planned = 0;
        this.callback = new SnitchRollback.DefaultRollbackCallback(System.currentTimeMillis());
        apply();
    }

    /**
     * @return whether or not this is a preview
     */
    public boolean isPreview() {
        return this.activity == EnumSnitchActivity.PREVIEW;
    }

    /**
     * Applies this query to this world.
     * If doing this from a preview, call {@link #applyPreview()} first
     */
    @Override
    public void apply() {

        // Do we have entries to process?
        if (!pendingEntries.isEmpty()) {
            session.getPlayer().sendMessage(MsgUtil.record("Planned rollback entries: §l" + pendingEntries.size()));
            changesIndex = 0;

            // We process these entries in batches per tick. By doing this we don't overload
            // the server with a mass amount of adjustments.
            new BukkitRunnable() {
                @Override
                public void run() {
                    // There are no pending entries to rollback. Cancel.
                    if (pendingEntries.isEmpty()) {
                        session.getPlayer().sendMessage(MsgUtil.error("No changes found matching your search: " + session.getQuery().getSearchSummary().toLowerCase()));
                        this.cancel();
                        return;
                    }

                    // Process our current batch of changes
                    int iteration = 0;
                    final int offset = changesIndex;
                    if (offset < pendingEntries.size()) {
                        primaryloop:

                        // Loop through the entries associated with this patch
                        for (final Iterator<SnitchEntry> iterator = pendingEntries.listIterator(offset); iterator.hasNext(); ) {
                            SnitchEntry entry = iterator.next();
                            if (isPreview()) // If this is a preview, we're just going to increment the changesIndex. We're not actually removing entries from the queue.
                                changesIndex++;
                            iteration++; // If we've processed 1,000 changes, break and leave the rest to be continued on.
                            if (iteration >= 1000) {
                                break;
                            }

                            // Retrieve the process handler for the action of this entry.
                            SnitchProcessHandler handler = entry.getAction().getProcessHandler();
                            if (!handler.can(activity)) { // can this action be altered by this activity? if not we remove it from our queue
                                iterator.remove();
                                continue;
                            }
                            try {

                                boolean result;

                                // Apply the necessary actions to this activity, depending on what we're doing
                                switch (activity) {
                                    case ROLLBACK:
                                        if (entry.isReverted()) {
                                            iterator.remove();
                                            continue primaryloop;
                                        }
                                        result = handler.handleRollback(session, entry);
                                        break;
                                    case RESTORE:
                                        if (!entry.isReverted()) {
                                            iterator.remove();
                                            continue;
                                        }
                                        result = handler.handleRestore(session, entry);
                                        break;
                                    case PREVIEW:
                                        if (entry.isReverted()) {
                                            iterator.remove();
                                            continue;
                                        }
                                        result = handler.handlePreview(session, entry);
                                        break;
                                    default:
                                        throw new IllegalArgumentException("Unsupported activity type.");
                                }

                                // If our adjustment was successful, we'll mark it accordimgly
                                if (result) {
                                    if (activity == EnumSnitchActivity.ROLLBACK) { // Mark a rolled back action as reverted
                                        SnitchPlugin.getInstance().async(() -> {
                                            try {
                                                SnitchPlugin.getInstance().getStorage().markReverted(entry, true);
                                            } catch (SnitchDatabaseException e) {
                                                e.printStackTrace();
                                            }
                                        });
                                    } else if (activity == EnumSnitchActivity.RESTORE) { // Mark a rolled back action as restored
                                        SnitchPlugin.getInstance().async(() -> {
                                            try {
                                                SnitchPlugin.getInstance().getStorage().markReverted(entry, false);
                                            } catch (SnitchDatabaseException e) {
                                                e.printStackTrace();
                                            }
                                        });
                                    }
                                    applied++;
                                } else {
                                    failed++;
                                }

                                // If this wasn't a preview, remove it from the queue for real
                                if (!isPreview()) {
                                    iterator.remove();
                                }

                            } catch (Exception ex) { // We have to catch all exception as to not interrupt the activity. We'll log to cancel and mark it as a failure.
                                ex.printStackTrace();
                                failed++;
                                iterator.remove();
                            }
                        }
                    }
                    // When we've completed the queue, cancel this task and run post-processing code
                    if (pendingEntries.isEmpty() || changesIndex >= pendingEntries.size()) {
                        this.cancel();
                        postProcess();
                    }
                }
            }.runTaskTimer(SnitchPlugin.getInstance(), 1L, 1L);
        } else {
            session.getPlayer().sendMessage(MsgUtil.error("No changes found matching your search: " + session.getQuery().getSearchSummary().toLowerCase()));
        }
    }

    private void postProcess() {
        if (isPreview()) { // If this is a preview, update the session so we can interact with it with the apply and cancel commands.
            session.setActivePreview(this);
        } else {
            // If this was a rollback or restore with an area defined, we'll remove any nearby fires
            if (session.getQuery().isAreaSelection()) {
                List<AdjustedBlock> changed = BlockUtil.removeNear(Arrays.asList(Material.FIRE), session.getQuery().getPosition().toLocation(session.getQuery().getWorld()), (int) session.getQuery().getRange());
                if (!changed.isEmpty()) {
                    session.getPlayer().sendMessage(MsgUtil.info("Extinguished " + changed.size() + " fires."));
                }
            }
        }
        // To support the undo command, we log what the last activity was so we can revert it
        session.setLastActivity(this.activity);

        // run the callback
        this.callback.handle(session.getPlayer(), new SnitchResult(applied, failed, planned, isPreview(), movedEntities, session.getQuery(), new ArrayList<>()));
    }

    /**
     * Contains the default logic for the PreviewCallback
     */
    public static class DefaultPreviewCallback implements SnitchCallback {

        private SnitchQuery query;

        public DefaultPreviewCallback(SnitchQuery query) {
            this.query = query;
        }

        @Override
        public void handle(Player player, SnitchResult result) {
            player.sendMessage(MsgUtil.success("Previewing rollback for " + query.getSearchSummary().toLowerCase()));
            player.sendMessage(MsgUtil.record("Showing " + result.getApplied() + " planned changes"));
            if (!result.getMovedEntities().isEmpty()) {
                player.sendMessage(MsgUtil.record(result.getMovedEntities().size() + "+ entities will be moved to safety"));
            }
            player.sendMessage(MsgUtil.record("Type §a/snitch pv apply§7 or §c/snitch pv cancel§7 to continue."));
        }
    }
}
