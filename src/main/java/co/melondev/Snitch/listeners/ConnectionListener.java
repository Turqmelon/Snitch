package co.melondev.Snitch.listeners;

import co.melondev.Snitch.SnitchPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

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
