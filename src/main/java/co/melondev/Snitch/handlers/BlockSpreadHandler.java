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
public class BlockSpreadHandler implements SnitchProcessHandler {
    @Override
    public boolean handleRollback(SnitchSession session, SnitchEntry entry) {
        JsonObject blockData = entry.getData().get("block").getAsJsonObject();
        Location loc = entry.getSnitchPosition().toLocation(entry.getSnitchWorld());
        Block b = loc.getBlock();
        Material material = Material.valueOf(blockData.get("type").getAsString());
        byte data = blockData.get("data").getAsByte();
        Biome biome = Biome.valueOf(blockData.get("biome").getAsString());
        b.setTypeIdAndData(material.getId(), data, false);
        b.setBiome(biome);
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
        Material material = Material.valueOf(sourceData.get("type").getAsString());
        byte data = sourceData.get("data").getAsByte();
        Biome biome = Biome.valueOf(sourceData.get("biome").getAsString());
        b.setTypeIdAndData(material.getId(), data, false);
        b.setBiome(biome);
        return true;
    }

    @Override
    public boolean can(EnumSnitchActivity activity) {
        return true;
    }
}
