package co.melondev.Snitch;

import co.melondev.Snitch.commands.SnitchCommand;
import co.melondev.Snitch.listeners.BlockListener;
import co.melondev.Snitch.listeners.ConnectionListener;
import co.melondev.Snitch.listeners.EntityListener;
import co.melondev.Snitch.listeners.InventoryListener;
import co.melondev.Snitch.managers.PlayerManager;
import co.melondev.Snitch.storage.MySQLStorage;
import co.melondev.Snitch.storage.StorageMethod;
import co.melondev.Snitch.util.Config;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class SnitchPlugin extends JavaPlugin {

    private static SnitchPlugin instance;
    private StorageMethod storage;
    private PlayerManager playerManager;
    private Config config;

    public static SnitchPlugin getInstance() {
        return instance;
    }

    @Override
    public void onDisable() {
        if (this.storage != null) {
            try {
                this.storage.closeConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onEnable() {
        instance = this;

        File dir = getDataFolder();
        if (!dir.exists()) {
            dir.mkdir();
        }
        File configFile = new File(dir, "config.yml");
        if (!configFile.exists()) {
            saveDefaultConfig();
        }
        try {
            this.config = new Config(getConfig());
        } catch (Exception e) {
            getLogger().severe("Error reading configuration file: " + e.getMessage());
            e.printStackTrace();
        }

        switch (config.getMethod().toLowerCase()) {
            case "mysql":
                try {
                    this.storage = new MySQLStorage(config.getMysqlHost(), config.getMysqlPort(), config.getMysqlUsername(), config.getMysqlPassword(), config.getMysqlDatabase(), config.getMysqlPrefix());
                } catch (SQLException e) {
                    getLogger().severe("Error initializing database: " + e.getMessage());
                    e.printStackTrace();
                }
                break;
            default:
                getLogger().severe("Error initializing database: unknown storage method");
                break;
        }

        this.playerManager = new PlayerManager(this);
        getServer().getPluginManager().registerEvents(new ConnectionListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityListener(this), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(this), this);

        getCommand("snitch").setExecutor(new SnitchCommand(this));
    }

    public Config getConfiguration() {
        return config;
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
