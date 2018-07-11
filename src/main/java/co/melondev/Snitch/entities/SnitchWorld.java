package co.melondev.Snitch.entities;

import org.bukkit.Bukkit;
import org.bukkit.World;

public class SnitchWorld {

    private int id;
    private String worldName;

    public SnitchWorld(int id, String worldName) {
        this.id = id;
        this.worldName = worldName;
    }

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
