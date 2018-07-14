package co.melondev.Snitch.entities;

import co.melondev.Snitch.SnitchPlugin;
import co.melondev.Snitch.enums.EnumSnitchActivity;
import co.melondev.Snitch.util.MsgUtil;
import co.melondev.Snitch.util.Previewable;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

/**
 * Created by Devon on 7/14/18.
 */
public class SnitchPreview implements Previewable {

    protected SnitchSession session;
    protected EnumSnitchActivity activity;
    protected Map<Entity, Integer> movedEntities = new HashMap<>();
    protected int failed, applied, planned, changesIndex = 0;
    private List<SnitchEntry> pendingEntries;
    private SnitchCallback callback;

    public SnitchPreview(SnitchSession session, SnitchCallback callback) {
        this.session = session;
        this.activity = EnumSnitchActivity.PREVIEW;
        this.pendingEntries = new ArrayList<>();
        this.pendingEntries.addAll(session.getEntries());
        this.callback = callback;
    }

    @Override
    public void cancelPreview() {
        for (Location loc : session.getAdjustedBlocks()) {
            Block block = loc.getBlock();
            session.getPlayer().sendBlockChange(block.getLocation(), block.getType(), block.getData());
        }
        MsgUtil.success("Preview cancelled.");
    }

    @Override
    public void applyPreview() {
        session.getPlayer().sendMessage(MsgUtil.info("Applying rollback: " + session.getQuery().getSearchSummary().toLowerCase() + "..."));
        this.activity = EnumSnitchActivity.ROLLBACK;
        this.applied = 0;
        this.failed = 0;
        this.planned = 0;
        apply();
    }

    public boolean isPreview() {
        return this.activity == EnumSnitchActivity.PREVIEW;
    }

    @Override
    public void apply() {
        if (!pendingEntries.isEmpty()) {
            session.getPlayer().sendMessage(MsgUtil.record("Planned rollback entries: §l" + pendingEntries.size()));
            changesIndex = 0;
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (pendingEntries.isEmpty()) {
                        session.getPlayer().sendMessage(MsgUtil.error("No changes found matching your search: " + session.getQuery().getSearchSummary().toLowerCase()));
                        this.cancel();
                        return;
                    }
                    int iteration = 0;
                    final int offset = changesIndex;
                    if (offset < pendingEntries.size()) {
                        for (final Iterator<SnitchEntry> iterator = pendingEntries.listIterator(offset); iterator.hasNext(); ) {
                            SnitchEntry entry = iterator.next();
                            if (isPreview())
                                changesIndex++;
                            iteration++;
                            if (iteration >= 1000) {
                                break;
                            }
                            SnitchProcessHandler handler = entry.getAction().getProcessHandler();
                            if (!handler.can(activity)) {
                                iterator.remove();
                                continue;
                            }
                            try {

                                boolean result;

                                switch (activity) {
                                    case ROLLBACK:
                                        result = handler.handleRollback(session, entry);
                                        break;
                                    case RESTORE:
                                        result = handler.handleRestore(session, entry);
                                        break;
                                    case PREVIEW:
                                        result = handler.handlePreview(session, entry);
                                        break;
                                    default:
                                        throw new IllegalArgumentException("Unsupported activity type.");
                                }


                                if (result) {
                                    applied++;
                                } else {
                                    failed++;
                                }

                                if (!isPreview()) {
                                    iterator.remove();
                                }

                            } catch (Exception ex) {
                                ex.printStackTrace();
                                failed++;
                                iterator.remove();
                            }
                        }
                    }
                    if (pendingEntries.isEmpty() || changesIndex >= pendingEntries.size()) {
                        this.cancel();
                        postProcess();
                    }
                }
            }.runTaskTimer(SnitchPlugin.getInstance(), 1L, 1L);
        }
    }

    private void postProcess() {
        if (isPreview()) {
            session.setActivePreview(this);
        }
    }
}
