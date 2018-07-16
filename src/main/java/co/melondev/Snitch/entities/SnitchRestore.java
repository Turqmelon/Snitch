package co.melondev.Snitch.entities;

import co.melondev.Snitch.enums.EnumSnitchActivity;
import co.melondev.Snitch.util.MsgUtil;
import org.bukkit.entity.Player;

/**
 * Created by Devon on 7/14/18.
 */
public class SnitchRestore extends SnitchPreview {
    public SnitchRestore(SnitchSession session, SnitchCallback callback) {
        super(session, callback);
        activity = EnumSnitchActivity.RESTORE;
    }

    public static class DefaultRestoreCallback implements SnitchCallback {

        private long startTime;

        public DefaultRestoreCallback(long startTime) {
            this.startTime = startTime;
        }

        @Override
        public void handle(Player player, SnitchResult result) {
            long diff = System.currentTimeMillis() - startTime;
            player.sendMessage(MsgUtil.success("Restore successfully completed in " + diff + "ms."));
            player.sendMessage(MsgUtil.record("Total Changes: " + result.getApplied() + "§c§o (" + result.getFailed() + " Failed)"));
            if (!result.getMovedEntities().isEmpty()) {
                player.sendMessage(MsgUtil.record(result.getMovedEntities().size() + "+ entities were moved to safety"));
            }
            MsgUtil.staff("§f" + player.getName() + "§b performed a restore: §f" + result.getQuery().getSearchSummary());
        }
    }
}
