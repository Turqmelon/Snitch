package co.melondev.Snitch.handlers;

import co.melondev.Snitch.entities.SnitchEntry;
import co.melondev.Snitch.entities.SnitchProcessHandler;
import co.melondev.Snitch.entities.SnitchSession;
import co.melondev.Snitch.enums.EnumSnitchActivity;
import com.google.gson.JsonObject;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;

/**
 * Created by Devon on 7/14/18.
 */
public class BlockDestructionHandler implements SnitchProcessHandler {

    @Override
    public boolean handleRollback(SnitchSession session, SnitchEntry entry) {
        JsonObject blockData = entry.getData().getAsJsonObject("block");
        Location loc = entry.getSnitchPosition().toLocation(entry.getSnitchWorld());
        Block block = loc.getBlock();
        block.setTypeIdAndData(Material.valueOf(blockData.get("type").getAsString()).getId(), blockData.get("data").getAsByte(), false);
        block.setBiome(Biome.valueOf(blockData.get("biome").getAsString()));
        return true;
    }

    @Override
    public boolean handlePreview(SnitchSession session, SnitchEntry entry) {
        JsonObject blockData = entry.getData().getAsJsonObject("block");
        Location loc = entry.getSnitchPosition().toLocation(entry.getSnitchWorld());
        Block block = loc.getBlock();
        session.getPlayer().sendBlockChange(block.getLocation(), Material.valueOf(blockData.get("type").getAsString()).getId(), blockData.get("data").getAsByte());
        session.recordAdjustedBlock(loc);
        return true;
    }

    @Override
    public boolean handleRestore(SnitchSession session, SnitchEntry entry) {
        Location loc = entry.getSnitchPosition().toLocation(entry.getSnitchWorld());
        Block block = loc.getBlock();
        block.setTypeIdAndData(Material.AIR.getId(), (byte) 0, false);
        return true;
    }

    @Override
    public boolean can(EnumSnitchActivity activity) {
        return true;
    }
}
