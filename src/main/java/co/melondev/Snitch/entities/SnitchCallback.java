package co.melondev.Snitch.entities;

import org.bukkit.entity.Player;

/**
 * Created by Devon on 7/14/18.
 */
public interface SnitchCallback {

    void handle(Player player, SnitchResult result);

}
