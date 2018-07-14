package co.melondev.Snitch;

import co.melondev.Snitch.managers.PlayerManager;
import co.melondev.Snitch.storage.StorageMethod;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class SnitchPlugin extends JavaPlugin {

    private static SnitchPlugin instance;
    private StorageMethod storage;
    private PlayerManager playerManager;

    public static SnitchPlugin getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public StorageMethod getStorage() {
        return storage;
    }

    public void async(Runnable r){
        Bukkit.getServer().getScheduler().runTaskAsynchronously(this, r);
    }
}
