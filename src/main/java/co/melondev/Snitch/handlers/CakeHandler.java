package co.melondev.Snitch.handlers;

import co.melondev.Snitch.entities.SnitchEntry;
import co.melondev.Snitch.entities.SnitchProcessHandler;
import co.melondev.Snitch.entities.SnitchSession;
import co.melondev.Snitch.enums.EnumSnitchActivity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Created by Devon on 7/17/18.
 */
public class CakeHandler implements SnitchProcessHandler {
    @Override
    public boolean handleRollback(SnitchSession session, SnitchEntry entry) {
        Location location = entry.getSnitchPosition().toLocation(entry.getSnitchWorld());
        location.getBlock().setTypeIdAndData(Material.CAKE_BLOCK.getId(), (byte) 0, false);
        return true;
    }

    @Override
    public boolean handlePreview(SnitchSession session, SnitchEntry entry) {
        Player player = session.getPlayer();
        Location location = entry.getSnitchPosition().toLocation(entry.getSnitchWorld());
        player.sendBlockChange(location, Material.CAKE_BLOCK, (byte) 0);
        return true;
    }

    @Override
    public boolean handleRestore(SnitchSession session, SnitchEntry entry) {
        Location location = entry.getSnitchPosition().toLocation(entry.getSnitchWorld());
        location.getBlock().setType(Material.AIR);
        return true;
    }

    @Override
    public boolean can(EnumSnitchActivity activity) {
        return true;
    }
}
