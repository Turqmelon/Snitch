package co.melondev.Snitch.entities;

import org.bukkit.entity.Player;

/**
 * Created by Devon on 7/14/18.
 *
 * Used to return data to the user after a completed activity.
 */
public interface SnitchCallback {

    void handle(Player player, SnitchResult result);

}
