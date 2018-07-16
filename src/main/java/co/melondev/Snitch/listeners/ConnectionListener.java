package co.melondev.Snitch.listeners;

import co.melondev.Snitch.SnitchPlugin;
import co.melondev.Snitch.entities.SnitchPlayer;
import co.melondev.Snitch.entities.SnitchPosition;
import co.melondev.Snitch.entities.SnitchWorld;
import co.melondev.Snitch.enums.EnumAction;
import co.melondev.Snitch.util.JsonUtil;
import com.google.gson.JsonObject;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.sql.SQLException;
import java.util.UUID;

/**
 * Created by Devon on 7/15/18.
 */
public class ConnectionListener implements Listener {

    private SnitchPlugin i;

    public ConnectionListener(SnitchPlugin i) {
        this.i = i;
    }

    private void logAction(Player player, Location location, EnumAction action, JsonObject data) {
        i.async(() -> {
            try {
                SnitchPlayer snitchPlayer = i.getStorage().getPlayer(player.getUniqueId());
                SnitchWorld world = i.getStorage().register(location.getWorld());
                SnitchPosition position = new SnitchPosition(location);
                i.getStorage().record(action, snitchPlayer, world, position, data, System.currentTimeMillis());
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        PlayerTeleportEvent.TeleportCause cause = event.getCause();
        Location to = event.getTo();
        if (EnumAction.PLAYER_TELEPORT.isEnabled()) {
            JsonObject data = new JsonObject();
            data.addProperty("cause", cause.name());
            data.add("location", JsonUtil.jsonify(to));
            logAction(player, player.getLocation(), EnumAction.PLAYER_TELEPORT, data);
        }
    }

    @EventHandler
    public void onJoin(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (EnumAction.PLAYER_QUIT.isEnabled()) {
            logAction(player, player.getLocation(), EnumAction.PLAYER_QUIT, new JsonObject());
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (EnumAction.PLAYER_JOIN.isEnabled()) {
            JsonObject obj = new JsonObject();
            obj.addProperty("io", player.getAddress().getAddress().getHostAddress());
            logAction(player, player.getLocation(), EnumAction.PLAYER_JOIN, obj);
        }
    }

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        String playerName = event.getName();
        UUID uuid = event.getUniqueId();
        try {
            i.getStorage().registerPlayer(playerName, uuid);
        } catch (SQLException e) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "Error creating your Snitch data!");
            e.printStackTrace();
        }
    }
}
