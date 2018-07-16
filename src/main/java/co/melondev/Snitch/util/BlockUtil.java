package co.melondev.Snitch.util;

import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Devon on 7/16/18.
 */
public class BlockUtil {

    public static List<AdjustedBlock> removeNear(List<Material> materialList, Location location, int range) {
        List<AdjustedBlock> changes = new ArrayList<>();
        Validate.notNull(location, "Location can't be null.");
        Validate.isTrue(range > 0, "Range must be bigger than 0.");
        Validate.notEmpty(materialList, "Material list can't be empty.");

        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        World world = location.getWorld();

        for (int xx = x - range; xx <= x + range; xx++) {
            for (int yy = y - range; yy <= y + range; yy++) {
                for (int zz = z - range; zz <= z + range; zz++) {
                    Location l = new Location(world, xx, yy, zz);
                    if (l.getBlock().getType() == Material.AIR)
                        continue;
                    if (materialList.contains(l.getBlock().getType())) {
                        final BlockState old = location.getBlock().getState();
                        l.getBlock().setType(Material.AIR);
                        final BlockState newState = location.getBlock().getState();
                        changes.add(new AdjustedBlock(old, newState));
                    }
                }
            }
        }
        return changes;
    }

}
