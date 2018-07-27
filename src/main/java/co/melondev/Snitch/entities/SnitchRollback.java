package co.melondev.Snitch.entities;

import co.melondev.Snitch.enums.EnumSnitchActivity;
import co.melondev.Snitch.util.MsgUtil;
import org.bukkit.entity.Player;

/**
 * Created by Devon on 7/14/18.
 *
 * Undo changes to a the world by reverting entries.
 */
public class SnitchRollback extends SnitchPreview {

    /**
     * Start with a session and a callback.
     *
     * @param session  the session (player performing rollback, query, etc.)
     * @param callback the callback to run on completion
     */
    public SnitchRollback(SnitchSession session, SnitchCallback callback) {
        super(session, callback);
        activity = EnumSnitchActivity.ROLLBACK;
    }

    /**
     * The default callback for rollbacks
     */
    public static class DefaultRollbackCallback implements SnitchCallback {

        private long startTime;

        public DefaultRollbackCallback(long startTime) {
            this.startTime = startTime;
        }

        @Override
        public void handle(Player player, SnitchResult result) {
            long diff = System.currentTimeMillis() - startTime;
            player.sendMessage(MsgUtil.success("Rollback successfully completed in " + diff + "ms."));
            player.sendMessage(MsgUtil.record("Total Changes: " + result.getApplied() + "§c§o (" + result.getFailed() + " Failed)"));
            if (!result.getMovedEntities().isEmpty()) {
                player.sendMessage(MsgUtil.record(result.getMovedEntities().size() + "+ entities were moved to safety"));
            }
            player.sendMessage(MsgUtil.record("If you made a mistake, you can §e/snitch undo§7."));
            MsgUtil.staff("§f" + player.getName() + "§b performed a rollback: §f" + result.getQuery().getSearchSummary());
        }
    }

}
