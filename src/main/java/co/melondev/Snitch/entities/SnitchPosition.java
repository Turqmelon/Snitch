package co.melondev.Snitch.entities;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

/**
 * Represents a location within Snitch. We avoid use of {@link Location} as to not load chunks unnecessarily
 */
public class SnitchPosition {

    /**
     * The stored X value
     */
    private int x;
    /**
     * The stored Y value
     */
    private int y;
    /**
     * The stored Z value
     */
    private int z;

    public SnitchPosition(Location location){
        this(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public SnitchPosition(Block block){
        this(block.getLocation());
    }

    public SnitchPosition(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Location toLocation(SnitchWorld world) {
        return toLocation(world.getBukkitWorld());
    }


    public Location toLocation(World world) {
        return new Location(world, this.x, this.y, this.z);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }
}
