package co.melondev.Snitch.handlers;

import co.melondev.Snitch.entities.SnitchEntry;
import co.melondev.Snitch.entities.SnitchProcessHandler;
import co.melondev.Snitch.entities.SnitchSession;
import co.melondev.Snitch.enums.EnumSnitchActivity;
import com.google.gson.JsonObject;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

/**
 * Created by Devon on 7/16/18.
 */
public class SignChangeHandler implements SnitchProcessHandler {
    @Override
    public boolean handleRollback(SnitchSession session, SnitchEntry entry) {

        Location location = entry.getSnitchPosition().toLocation(entry.getSnitchWorld());
        Block block = location.getBlock();
        if ((block.getState() instanceof Sign)) {
            Sign sign = (Sign) block.getState();
            JsonObject oldLines = entry.getData().getAsJsonObject("old");
            for (int i = 0; i < 4; i++) {
                sign.setLine(i, oldLines.get("line" + i).getAsString());
            }
            sign.update();
        }

        return true;
    }

    @Override
    public boolean handlePreview(SnitchSession session, SnitchEntry entry) {
        return false;
    }

    @Override
    public boolean handleRestore(SnitchSession session, SnitchEntry entry) {
        Location location = entry.getSnitchPosition().toLocation(entry.getSnitchWorld());
        Block block = location.getBlock();
        if ((block.getState() instanceof Sign)) {
            Sign sign = (Sign) block.getState();
            JsonObject newLines = entry.getData().getAsJsonObject("new");
            for (int i = 0; i < 4; i++) {
                sign.setLine(i, newLines.get("line" + i).getAsString());
            }
            sign.update();
        }
        return true;
    }

    @Override
    public boolean can(EnumSnitchActivity activity) {
        return activity == EnumSnitchActivity.ROLLBACK || activity == EnumSnitchActivity.RESTORE;
    }
}
