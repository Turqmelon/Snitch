package co.melondev.Snitch.handlers;

import co.melondev.Snitch.SnitchPlugin;
import co.melondev.Snitch.entities.SnitchPlayer;
import co.melondev.Snitch.entities.SnitchPosition;
import co.melondev.Snitch.entities.SnitchWorld;
import co.melondev.Snitch.enums.EnumAction;
import com.google.gson.JsonObject;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.sql.SQLException;

/**
 * Created by Devon on 7/16/18.
 */
public class ChatListener implements Listener {

    private SnitchPlugin i;

    public ChatListener(SnitchPlugin i) {
        this.i = i;
    }

    private void logAction(Player player, String message, Location location, EnumAction action) {
        logAction(player, message, location, action, null);
    }

    private void logAction(Player player, String message, Location location, EnumAction action, JsonObject data) {
        i.async(() -> {
            try {
                SnitchPlayer snitchPlayer = i.getStorage().getPlayer(player.getUniqueId());
                SnitchWorld world = i.getStorage().register(location.getWorld());
                SnitchPosition position = new SnitchPosition(location);
                JsonObject d = data;
                if (d == null) {
                    d = new JsonObject();
                    d.addProperty("message", ChatColor.stripColor(message));
                }
                i.getStorage().record(action, snitchPlayer, world, position, d, System.currentTimeMillis());
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChat(PlayerCommandPreprocessEvent event) {
        if (!EnumAction.PLAYER_COMMAND.isEnabled()) {
            return;
        }
        final Player player = event.getPlayer();
        final String message = event.getMessage();
        if (message.toLowerCase().startsWith("/snitch"))
            return;
        logAction(player, message, player.getLocation(), EnumAction.PLAYER_COMMAND);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        if (!EnumAction.PLAYER_CHAT.isEnabled()) {
            return;
        }
        final Player player = event.getPlayer();
        final String message = event.getMessage();
        logAction(player, message, player.getLocation(), EnumAction.PLAYER_CHAT);
    }
}
