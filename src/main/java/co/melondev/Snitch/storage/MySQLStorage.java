package co.melondev.Snitch.storage;

import co.melondev.Snitch.SnitchPlugin;
import co.melondev.Snitch.entities.*;
import co.melondev.Snitch.enums.EnumAction;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.World;

import java.io.Closeable;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by Devon on 7/13/18.
 */
@SuppressWarnings("Duplicates")
public class MySQLStorage implements StorageMethod {

    private final String SCHEMA_VERSION = "ALPHA-1.0.0";

    private MySQLDataSource dataSource;

    private String host;
    private int port;
    private String username;
    private String password;
    private String database;
    private String tablePrefix;

    private Map<String, SnitchWorld> worldNameMap = new HashMap<>();
    private Map<Integer, SnitchWorld> worldIdMap = new HashMap<>();

    private Cache<Integer, SnitchPlayer> playerIDCache = CacheBuilder.newBuilder().concurrencyLevel(4).expireAfterAccess(1, TimeUnit.MINUTES).build();
    private Cache<String, SnitchPlayer> playerNameCache = CacheBuilder.newBuilder().concurrencyLevel(4).expireAfterAccess(1, TimeUnit.MINUTES).build();
    private Cache<UUID, SnitchPlayer> playerUUIDCache = CacheBuilder.newBuilder().concurrencyLevel(4).expireAfterAccess(1, TimeUnit.MINUTES).build();

    public MySQLStorage(String host, int port, String username, String password, String database, String tablePrefix) throws SQLException {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.database = database;
        this.tablePrefix = tablePrefix;
        this.dataSource = new MySQLDataSource();
        this.setupTables();
        this.updateSchemaVersion();
        this.loadWorlds();
    }

    private String tble(String tableName) {
        return this.database + "." + this.tablePrefix + tableName;
    }

    private String buildLookupQuery(SnitchQuery query) {
        StringBuilder q = new StringBuilder("SELECT * FROM " + tble("logs") + " WHERE ");
        StringBuilder conditions = new StringBuilder();
        if (!query.getPlayers().isEmpty()) {
            List<SnitchPlayer> players = query.getPlayers();
            if (players.size() == 1) {
                conditions.append("player_id = '").append(players.get(0).getId()).append("'");
            } else {
                StringBuilder playerQuery = new StringBuilder("(");
                for (SnitchPlayer player : players) {
                    if (playerQuery.length() > 1) {
                        playerQuery.append(" OR ");
                    }
                    playerQuery.append("player_id = '").append(player.getId()).append("'");
                }
                playerQuery.append(")");
                if (playerQuery.length() > 2) {
                    conditions.append(playerQuery);
                }
            }
        }
        if (!query.getActions().isEmpty()) {
            if (conditions.length() != 0) {
                conditions.append("AND ");
            }
            List<EnumAction> actions = query.getActions();
            if (actions.size() == 1) {
                conditions.append("action_id = '").append(actions.get(0).getId()).append("'");
            } else {
                StringBuilder actionQuery = new StringBuilder("(");
                for (EnumAction action : actions) {
                    if (actionQuery.length() > 1) {
                        actionQuery.append(" OR ");
                    }
                    actionQuery.append("action_id = '").append(action.getId()).append("'");
                }
                actionQuery.append(")");
                if (actionQuery.length() > 2) {
                    conditions.append(actionQuery);
                }
            }
        }
        if (query.getSince() > 0) {
            conditions.append(conditions.length() > 0 ? "AND " : "").append("timestamp >= '").append(query.getSince()).append("'").append(" ");
        }
        if (query.getBefore() > 0) {
            conditions.append(conditions.length() > 0 ? "AND " : "").append("timestamp <= '").append(query.getBefore()).append("'").append(" ");
        }
        if (query.getWorld() != null) {
            conditions.append(conditions.length() > 0 ? "AND " : "").append("world_id = '").append(query.getWorld().getId()).append("' ");
            if (query.getPosition() != null && query.getRange() > 0) {

                int minX = (int) (query.getPosition().getX() - query.getRange());
                int minY = (int) (query.getPosition().getY() - query.getRange());
                int minZ = (int) (query.getPosition().getZ() - query.getRange());

                int maxX = (int) (query.getPosition().getX() + query.getRange());
                int maxY = (int) (query.getPosition().getY() + query.getRange());
                int maxZ = (int) (query.getPosition().getZ() + query.getRange());

                conditions.append(conditions.length() > 0 ? "AND " : "").append("pos_x >= '").append(minX)
                        .append("' AND pos_x <= '").append(maxX).append("' AND pos_y >= '").append(minY)
                        .append("' AND pos_y <= '").append(maxY).append("' AND pos_z >= '").append(minZ)
                        .append("' AND pos_z <= '").append(maxZ).append("' ");
            } else if (query.getPosition() != null && query.isUseExactPosition()) {
                SnitchPosition p = query.getPosition();
                conditions.append(conditions.length() > 0 ? "AND " : "").append("pos_x = '").append(p.getX())
                        .append("' AND pos_y ='").append(p.getY()).append("' AND pos_z = '").append(p.getZ())
                        .append("' ");
            }
        }
        if (conditions.length() > 0) {
            q.append(conditions.toString());
        } else {
            q.append("1 ");
        }
        q.append("ORDER BY timestamp DESC");
        if (query.hasLimit()) {
            q.append(" LIMIT ").append(query.getLimit());
        }
        System.out.println("[DEBUG] Query: \"" + query.getSearchSummary() + "\" -> \"" + q.toString() + "\".");
        return q.toString();
    }

    private void loadWorlds() throws SQLException {
        try (Connection conn = getConnection()) {
            try (PreparedStatement sel = conn.prepareStatement("SELECT * FROM " + tble("worlds") + " ORDER BY id DESC")) {
                try (ResultSet set = sel.executeQuery()) {
                    while (set.next()) {
                        int id = set.getInt("id");
                        String name = set.getString("world_name");
                        SnitchWorld world = new SnitchWorld(id, name);
                        this.worldIdMap.put(id, world);
                        this.worldNameMap.put(name.toLowerCase(), world);
                    }
                }
            }
        }
    }

    private Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public ImmutableList<SnitchWorld> getWorlds() {
        return ImmutableList.copyOf(worldIdMap.values());
    }

    @Override
    public SnitchWorld register(World world) throws SQLException {
        if (worldNameMap.containsKey(world.getName().toLowerCase())) {
            return worldNameMap.get(world.getName().toLowerCase());
        }
        try (Connection conn = getConnection(); PreparedStatement ins = conn.prepareStatement("INSERT INTO " + tble("worlds") + " (world_name) VALUES (?)", Statement.RETURN_GENERATED_KEYS)) {
            ins.setString(1, world.getName());
            ins.execute();
            try (ResultSet set = ins.getGeneratedKeys()) {
                if (set.next()) {
                    int id = set.getInt(1);
                    SnitchWorld w = new SnitchWorld(id, world.getName());
                    this.worldNameMap.put(w.getWorldName().toLowerCase(), w);
                    this.worldIdMap.put(w.getId(), w);
                    return w;
                }
            }
        }
        return null;
    }

    @Override
    public void closeConnection() throws IOException {
        this.dataSource.close();
    }

    @Override
    public SnitchPlayer registerPlayer(String playerName, UUID uuid) throws SQLException {

        SnitchPlayer cached = playerUUIDCache.getIfPresent(uuid);
        if (cached != null) {
            if (!cached.getPlayerName().equals(playerName)) {
                cached.setPlayerName(playerName);
                try (Connection conn = getConnection(); PreparedStatement upd = conn.prepareStatement("UPDATE " + tble("players") + " SET player_name = ? WHERE uuid = ?")) {
                    upd.setString(1, playerName);
                    upd.setString(2, upd.toString());
                    upd.execute();
                }
            }
            return cached;
        }

        cached = getPlayer(uuid);
        if (cached != null) {
            return cached;
        }

        try (Connection conn = getConnection(); PreparedStatement ins = conn.prepareStatement("INSERT INTO " + tble("players") + " (uuid, player_name, first_seen, last_seen) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            ins.setString(1, uuid.toString());
            ins.setString(2, playerName);
            ins.setLong(3, System.currentTimeMillis());
            ins.setLong(4, System.currentTimeMillis());
            ins.execute();
            try (ResultSet set = ins.getGeneratedKeys()) {
                if (set.next()) {
                    int id = set.getInt(1);
                    SnitchPlayer snitchPlayer = new SnitchPlayer(id, uuid, playerName);
                    cache(snitchPlayer);
                    return snitchPlayer;
                }
            }
        }

        return null;
    }

    private void cache(SnitchPlayer snitchPlayer) {
        this.playerNameCache.put(snitchPlayer.getPlayerName().toLowerCase(), snitchPlayer);
        this.playerIDCache.put(snitchPlayer.getId(), snitchPlayer);
        this.playerUUIDCache.put(snitchPlayer.getUuid(), snitchPlayer);
    }

    @Override
    public SnitchPlayer getPlayer(UUID uuid) throws SQLException {
        SnitchPlayer pl = playerUUIDCache.getIfPresent(uuid);
        if (pl != null) {
            return pl;
        }
        try (Connection conn = getConnection(); PreparedStatement sel = conn.prepareStatement("SELECT * FROM " + tble("players") + " WHERE uuid = ? LIMIT 1")) {
            sel.setString(1, uuid.toString());
            try (ResultSet set = sel.executeQuery()) {
                if (set.next()) {
                    pl = new SnitchPlayer(set);
                    cache(pl);
                    return pl;
                }
            }
        }
        return null;
    }

    @Override
    public SnitchPlayer getPlayer(String playerName) throws SQLException {
        SnitchPlayer pl = playerUUIDCache.getIfPresent(playerName.toLowerCase());
        if (pl != null) {
            return pl;
        }
        try (Connection conn = getConnection(); PreparedStatement sel = conn.prepareStatement("SELECT * FROM " + tble("players") + " WHERE player_name = ? LIMIT 1")) {
            sel.setString(1, playerName);
            try (ResultSet set = sel.executeQuery()) {
                if (set.next()) {
                    pl = new SnitchPlayer(set);
                    cache(pl);
                    return pl;
                }
            }
        }
        return null;
    }

    @Override
    public SnitchEntry record(EnumAction action, SnitchPlayer player, SnitchWorld world, SnitchPosition position, JsonObject data, long time) throws SQLException {
        try (Connection conn = getConnection(); PreparedStatement ins = conn.prepareStatement("" +
                "INSERT INTO " + tble("logs") + " (action_id, player_id, world_id, pos_x, pos_y, pos_z, timestamp, data) VALUES (?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            ins.setInt(1, action.getId());
            ins.setInt(2, player.getId());
            ins.setInt(3, world.getId());
            ins.setInt(4, position.getX());
            ins.setInt(5, position.getY());
            ins.setInt(6, position.getZ());
            ins.setLong(7, time);
            ins.setString(8, data.toString());
            ins.execute();
            try (ResultSet set = ins.getGeneratedKeys()) {
                if (set.next()) {
                    int id = set.getInt(1);
                    return new SnitchEntry(id, action, player, world, position, time, data, false);
                }
            }
        }
        return null;
    }

    @Override
    public ImmutableList<SnitchEntry> performLookup(SnitchQuery query) throws SQLException {
        List<SnitchEntry> results = new ArrayList<>();
        String sql = buildLookupQuery(query);
        try (Connection conn = getConnection(); PreparedStatement sel = conn.prepareStatement(sql)) {
            try (ResultSet set = sel.executeQuery()) {
                while (set.next()) {
                    results.add(new SnitchEntry(set));
                }
            }
        }
        return ImmutableList.copyOf(results);
    }

    @Override
    public SnitchPlayer getPlayer(int playerID) throws SQLException {
        SnitchPlayer cached = playerIDCache.getIfPresent(playerID);
        if (cached != null) {
            return cached;
        }
        try (Connection conn = getConnection(); PreparedStatement sel = conn.prepareStatement("SELECT * FROM " + tble("players") + " WHERE id = ? LIMIT 1")) {
            sel.setInt(1, playerID);
            try (ResultSet set = sel.executeQuery()) {
                if (set.next()) {
                    SnitchPlayer snitchPlayer = new SnitchPlayer(set);
                    cache(snitchPlayer);
                    return snitchPlayer;
                }
            }
        }
        return null;
    }

    @Override
    public SnitchWorld getWorld(int worldID) {
        return worldIdMap.getOrDefault(worldID, null);
    }

    private void setupTables() throws SQLException {
        try (Connection conn = getConnection()) {
            try (PreparedStatement prep = conn.prepareStatement("CREATE TABLE IF NOT EXISTS " + tble("logs") + " (`id` INT(11) NOT NULL AUTO_INCREMENT,`action_id` INT(11) DEFAULT NULL,`player_id` INT(11) DEFAULT NULL,`world_id` INT(11) DEFAULT NULL,`pos_x` INT(11) DEFAULT NULL,`pos_y` INT(11) DEFAULT NULL,`pos_z` INT(11) DEFAULT NULL,`timestamp` BIGINT(20) DEFAULT NULL,`data` BLOB,`is_reverted` INT(11) DEFAULT '0', PRIMARY KEY (`id`)) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1")) {
                prep.execute();
            }
            try (PreparedStatement prep = conn.prepareStatement("CREATE TABLE IF NOT EXISTS " + tble("meta") + " (\n" +
                    "  `id` int(11) NOT NULL AUTO_INCREMENT,\n" +
                    "  `setting` varchar(255) NOT NULL,\n" +
                    "  `setting_value` varchar(255) NOT NULL,\n" +
                    "  PRIMARY KEY (`id`)\n" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=latin1\n" +
                    "\n")) {
                prep.execute();
            }
            try (PreparedStatement prep = conn.prepareStatement("CREATE TABLE IF NOT EXISTS " + tble("players") + " (\n" +
                    "  `id` INT(11) NOT NULL AUTO_INCREMENT,\n" +
                    "  `player_name` VARCHAR(255) DEFAULT NULL,\n" +
                    "  `uuid` VARCHAR(255) DEFAULT NULL,\n" +
                    "  `first_seen` BIGINT(20) DEFAULT NULL,\n" +
                    "  `last_seen` BIGINT(20) DEFAULT NULL,\n" +
                    "  PRIMARY KEY (`id`)\n" +
                    ") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1\n" +
                    "\n")) {
                prep.execute();
            }
            try (PreparedStatement prep = conn.prepareStatement("" +
                    "CREATE TABLE IF NOT EXISTS " + tble("worlds") + " (\n" +
                    "  `id` int(11) NOT NULL AUTO_INCREMENT,\n" +
                    "  `world_name` varchar(255) DEFAULT NULL,\n" +
                    "  PRIMARY KEY (`id`)\n" +
                    ") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1\n" +
                    "\n")) {
                prep.execute();
            }
        }

    }

    private void updateSchemaVersion() throws SQLException {
        String v = getRemoteSchemaVersion();
        if (v == null || !v.equals(SCHEMA_VERSION)) {
            if (v != null) {
                try (Connection conn = getConnection(); PreparedStatement upd = conn.prepareStatement("" +
                        "UPDATE " + tble("meta") + " SET setting_value = ? WHERE setting = ?")) {
                    upd.setString(1, SCHEMA_VERSION);
                    upd.setString(2, "schema_version");
                    upd.execute();
                }
            } else {
                try (Connection conn = getConnection(); PreparedStatement ins = conn.prepareStatement("" +
                        "INSERT INTO " + tble("meta") + " (setting, setting_value) VALUES (?, ?)")) {
                    ins.setString(1, "schema_version");
                    ins.setString(2, SCHEMA_VERSION);
                    ins.execute();
                }
            }
            SnitchPlugin.getInstance().getLogger().info("Updated schema version to " + SCHEMA_VERSION);
        }
    }

    private String getRemoteSchemaVersion() throws SQLException {
        try (Connection conn = getConnection(); PreparedStatement sel = conn.prepareStatement("" +
                "SELECT setting_value FROM " + tble("meta") + " WHERE setting = ? LIMIT 1")) {
            sel.setString(1, "schema_version");
            try (ResultSet set = sel.executeQuery()) {
                if (set.next()) {
                    return set.getString("setting_value");
                }
            }
        }
        return null;
    }

    @Override
    public void markReverted(SnitchEntry entry, boolean reverted) throws SQLException {
        entry.setReverted(reverted);
        try (Connection conn = getConnection(); PreparedStatement upd = conn.prepareStatement("UPDATE " + tble("logs" + " SET is_reverted = ? WHERE id = ?;"))) {
            upd.setInt(1, reverted ? 1 : 0);
            upd.setInt(2, entry.getId());
            upd.execute();
        }
    }

    public class MySQLDataSource implements Closeable {

        private final HikariDataSource ds;

        public MySQLDataSource() {
            this.ds = new HikariDataSource();
            ds.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
            ds.setUsername(username);
            ds.setPassword(password);
            ds.setDriverClassName("com.mysql.jdbc.Driver");

            ds.setMinimumIdle(2);
            ds.setPoolName("Snitch-Connections");

            ds.addDataSourceProperty("useUnocode", "true");
            ds.addDataSourceProperty("characterEncoding", "utf-8");
            ds.addDataSourceProperty("rewriteBatchedStatements", "true");
            ds.addDataSourceProperty("cachePrepStmts", "true");
            ds.addDataSourceProperty("prepStmtCacheSize", "250");
            ds.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        }

        public Connection getConnection() throws SQLException {
            return ds.getConnection();
        }

        @Override
        public void close() throws IOException {
            ds.close();
        }
    }
}
