package co.melondev.Snitch.entities;

import co.melondev.Snitch.enums.EnumSnitchActivity;
import co.melondev.Snitch.util.MsgUtil;
import org.bukkit.entity.Player;

/**
 * Created by Devon on 7/14/18.
 */
public class SnitchRollback extends SnitchPreview {
    public SnitchRollback(SnitchSession session, SnitchCallback callback) {
        super(session, callback);
        activity = EnumSnitchActivity.ROLLBACK;
    }

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
            player.sendMessage(MsgUtil.record("If you made a mistake, you can §e/snitch restore <param>§7."));
        }
    }

}
