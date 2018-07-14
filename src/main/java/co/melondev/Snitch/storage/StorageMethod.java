package co.melondev.Snitch.storage;

import co.melondev.Snitch.entities.*;
import co.melondev.Snitch.enums.EnumAction;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import org.bukkit.World;

import java.sql.SQLException;
import java.util.UUID;

public interface StorageMethod {

    ImmutableList<SnitchWorld> getWorlds();

    SnitchWorld register(World world) throws SQLException;

    SnitchPlayer registerPlayer(String playerName, UUID uuid);

    SnitchPlayer getPlayer(UUID uuid) throws SQLException;

    SnitchPlayer getPlayer(String playerName) throws SQLException;

    SnitchEntry record(EnumAction action, SnitchPlayer player, SnitchWorld world, SnitchPosition position, JsonObject data, long time) throws SQLException;

    ImmutableList<SnitchEntry> performLookup(SnitchQuery query) throws SQLException;

    SnitchPlayer getPlayer(int playerID);

    SnitchWorld getWorld(int worldID);
}
