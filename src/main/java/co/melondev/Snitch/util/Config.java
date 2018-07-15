package co.melondev.Snitch.util;

import co.melondev.Snitch.enums.EnumAction;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Devon on 7/15/18.
 */
public class Config {

    private String configVersion;

    private String method;
    private String mysqlHost;
    private int mysqlPort;
    private String mysqlUsername;
    private String mysqlPassword;
    private String mysqlDatabase;
    private String mysqlPrefix;

    private boolean autoClean;
    private List<String> autocleanParams;

    private int defaultArea;
    private long defaultTime;

    private Material wand;
    private Material wandBlock;

    private List<EnumAction> disabledLogging;

    public Config(FileConfiguration conf) throws Exception {
        this.configVersion = conf.getString("meta.version");
        this.method = conf.getString("data.method");
        this.mysqlHost = conf.getString("data.mysql.host");
        this.mysqlUsername = conf.getString("data.mysql.username");
        this.mysqlPassword = conf.getString("data.mysql.password");
        this.mysqlDatabase = conf.getString("data.mysql.database");
        this.mysqlPrefix = conf.getString("data.mysql.prefix");
        this.mysqlPort = conf.getInt("data.mysql.port");
        this.autoClean = conf.getBoolean("autoclean.enable");
        this.autocleanParams = conf.getStringList("autoclean.actions");
        this.defaultArea = conf.getInt("defaults.area");
        this.defaultTime = TimeUtil.parseDateDiff(conf.getString("defaults.time"), true) - System.currentTimeMillis();
        this.wand = Material.valueOf(conf.getString("tools.wand").toUpperCase());
        this.wandBlock = Material.valueOf(conf.getString("tools.wand-block").toUpperCase());
        this.disabledLogging = new ArrayList<>();
        for (String list : conf.getStringList("disabled-logging")) {
            EnumAction action = EnumAction.valueOf(list.toUpperCase());
            this.disabledLogging.add(action);
        }
    }

    public String getConfigVersion() {
        return configVersion;
    }

    public String getMethod() {
        return method;
    }

    public String getMysqlHost() {
        return mysqlHost;
    }

    public int getMysqlPort() {
        return mysqlPort;
    }

    public String getMysqlUsername() {
        return mysqlUsername;
    }

    public String getMysqlPassword() {
        return mysqlPassword;
    }

    public String getMysqlDatabase() {
        return mysqlDatabase;
    }

    public String getMysqlPrefix() {
        return mysqlPrefix;
    }

    public boolean isAutoClean() {
        return autoClean;
    }

    public List<String> getAutocleanParams() {
        return autocleanParams;
    }

    public int getDefaultArea() {
        return defaultArea;
    }

    public long getDefaultTime() {
        return defaultTime;
    }

    public Material getWand() {
        return wand;
    }

    public Material getWandBlock() {
        return wandBlock;
    }

    public List<EnumAction> getDisabledLogging() {
        return disabledLogging;
    }
}
