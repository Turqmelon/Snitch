package co.melondev.Snitch.entities;

import org.bukkit.Location;
import org.bukkit.block.Block;

public class SnitchPosition {

    private int x;
    private int y;
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
