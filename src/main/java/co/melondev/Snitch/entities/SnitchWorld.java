package co.melondev.Snitch.entities;

import org.bukkit.Bukkit;
import org.bukkit.World;

/**
 * Represents a world within Snitch
 */
public class SnitchWorld {

    /**
     * The ID of the world (decided by database)
     */
    private int id;

    /**
     * The name of the world
     */
    private String worldName;

    public SnitchWorld(int id, String worldName) {
        this.id = id;
        this.worldName = worldName;
    }

    /**
     * @return a {@link World} from this SnitchWorld
     */
    public World getBukkitWorld(){
        return Bukkit.getWorld(getWorldName());
    }

    public int getId() {
        return id;
    }

    public String getWorldName() {
        return worldName;
    }
}
