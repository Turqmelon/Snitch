package co.melondev.Snitch.handlers;

import co.melondev.Snitch.entities.SnitchEntry;
import co.melondev.Snitch.entities.SnitchProcessHandler;
import co.melondev.Snitch.entities.SnitchSession;
import co.melondev.Snitch.enums.EnumSnitchActivity;
import co.melondev.Snitch.util.BlockUtil;
import com.google.gson.JsonObject;
import net.minecraft.server.v1_12_R1.MojangsonParseException;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 * Created by Devon on 7/14/18.
 */
public class BlockSpreadHandler implements SnitchProcessHandler {
    @Override
    public boolean handleRollback(SnitchSession session, SnitchEntry entry) {
        JsonObject blockData = entry.getData().get("block").getAsJsonObject();
        Location loc = entry.getSnitchPosition().toLocation(entry.getSnitchWorld());
        Block b = loc.getBlock();
        try {
            BlockUtil.rebuildBlock(b, blockData);
        } catch (MojangsonParseException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean handlePreview(SnitchSession session, SnitchEntry entry) {
        JsonObject blockData = entry.getData().get("block").getAsJsonObject();
        Location loc = entry.getSnitchPosition().toLocation(entry.getSnitchWorld());
        Block b = loc.getBlock();
        Material material = Material.valueOf(blockData.get("type").getAsString());
        byte data = blockData.get("data").getAsByte();
        session.getPlayer().sendBlockChange(loc, material, data);
        session.recordAdjustedBlock(b.getLocation());
        return true;
    }

    @Override
    public boolean handleRestore(SnitchSession session, SnitchEntry entry) {
        JsonObject sourceData = entry.getData().get("source").getAsJsonObject();
        Location loc = entry.getSnitchPosition().toLocation(entry.getSnitchWorld());
        Block b = loc.getBlock();
        try {
            BlockUtil.rebuildBlock(b, sourceData);
        } catch (MojangsonParseException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean can(EnumSnitchActivity activity) {
        return true;
    }
}
