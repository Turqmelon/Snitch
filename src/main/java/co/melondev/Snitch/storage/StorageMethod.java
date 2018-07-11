package co.melondev.Snitch.storage;

import co.melondev.Snitch.SnitchPlugin;
import co.melondev.Snitch.entities.*;
import co.melondev.Snitch.enums.EnumAction;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import org.bukkit.World;

import java.util.UUID;

public interface StorageMethod {

    ImmutableList<SnitchWorld> getWorlds();

    SnitchWorld register(World world);

    SnitchPlayer registerPlayer(String playerName, UUID uuid);

    SnitchPlayer getPlayer(UUID uuid);

    SnitchPlayer getPlayer(String playerName);

    SnitchEntry record(EnumAction action, SnitchPlayer player, SnitchWorld world, SnitchPosition position, JsonObject data, long time);

    ImmutableList<SnitchEntry> performLookup(SnitchQuery query);

}
