package co.melondev.Snitch.storage;

import co.melondev.Snitch.entities.*;
import co.melondev.Snitch.enums.EnumAction;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import org.bukkit.World;

import java.sql.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by Devon on 7/13/18.
 */
@SuppressWarnings("Duplicates")
public class MySQLQuery implements StorageMethod {

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

    public MySQLQuery(String host, int port, String username, String password, String database, String tablePrefix) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.database = database;
        this.tablePrefix = tablePrefix;
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
                conditions.append("player_id = ").append(players.get(0).getId());
            } else {
                StringBuilder playerQuery = new StringBuilder("(");
                for (SnitchPlayer player : players) {
                    if (playerQuery.length() > 1) {
                        playerQuery.append(" OR ");
                    }
                    playerQuery.append("player_id = ").append(player.getId());
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
                conditions.append("action_id = ").append(actions.get(0).getId());
            } else {
                StringBuilder actionQuery = new StringBuilder("(");
                for (EnumAction action : actions) {
                    if (actionQuery.length() > 1) {
                        actionQuery.append(" OR ");
                    }
                    actionQuery.append("action_id = ").append(action.getId());
                }
                actionQuery.append(")");
                if (actionQuery.length() > 2) {
                    conditions.append(actionQuery);
                }
            }
        }
        if (query.getSince() > 0) {
            conditions.append(conditions.length() > 0 ? "AND " : "").append("since >= ").append(query.getSince()).append(" ");
        }
        if (query.getBefore() > 0) {
            conditions.append(conditions.length() > 0 ? "AND " : "").append("before <= ").append(query.getBefore()).append(" ");
        }
        if (query.getWorld() != null) {
            conditions.append(conditions.length() > 0 ? "AND " : "").append("world_id = ").append(query.getWorld().getId()).append(" ");
            if (query.getPosition() != null && query.getRange() > 0) {

                int minX = (int) (query.getPosition().getX() - query.getRange());
                int minY = (int) (query.getPosition().getY() - query.getRange());
                int minZ = (int) (query.getPosition().getZ() - query.getRange());

                int maxX = (int) (query.getPosition().getX() - query.getRange());
                int maxY = (int) (query.getPosition().getY() - query.getRange());
                int maxZ = (int) (query.getPosition().getZ() - query.getRange());

                conditions.append(conditions.length() > 0 ? "AND " : "").append("pos_x BETWEEN ").append(minX).append(" AND ").append(maxX).append(" AND pos_y BETWEEN ").append(minY).append(" AND ").append(maxY).append(" AND pos_z BETWEEN ").append(minZ).append(" AND ").append(maxZ).append(" ");
            }
        }
        if (conditions.length() > 0) {
            q.append(conditions.toString());
        } else {
            q.append("1 ");
        }
        q.append("ORDER BY timestamp DESC");
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

    private Connection getConnection() {
        return null; // TODO handle this
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
    public SnitchPlayer registerPlayer(String playerName, UUID uuid) {
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
                    int id = set.getInt("id");
                    return new SnitchEntry(id, action, player, world, position, time, data);
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
    public SnitchPlayer getPlayer(int playerID) {
        return null;
    }

    @Override
    public SnitchWorld getWorld(int worldID) {
        return null;
    }
}
