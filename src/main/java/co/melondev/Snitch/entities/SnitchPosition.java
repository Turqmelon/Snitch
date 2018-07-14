package co.melondev.Snitch.entities;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SnitchPosition {

    private int x;
    private int y;
    private int z;

    public SnitchPosition(ResultSet set) throws SQLException {
        this.x = set.getInt("pos_x");
        this.y = set.getInt("pos_y");
        this.z = set.getInt("pos_z");
    }

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
