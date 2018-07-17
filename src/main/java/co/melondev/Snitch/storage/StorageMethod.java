package co.melondev.Snitch.storage;

import co.melondev.Snitch.entities.*;
import co.melondev.Snitch.enums.EnumAction;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import org.bukkit.World;

import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

public interface StorageMethod {

    ImmutableList<SnitchWorld> getWorlds();

    SnitchWorld register(World world) throws Exception;

    SnitchPlayer registerPlayer(String playerName, UUID uuid) throws Exception;

    SnitchPlayer getPlayer(UUID uuid) throws Exception;

    SnitchPlayer getPlayer(String playerName) throws Exception;

    SnitchEntry record(EnumAction action, SnitchPlayer player, SnitchWorld world, SnitchPosition position, JsonObject data, long time) throws Exception;

    ImmutableList<SnitchEntry> performLookup(SnitchQuery query) throws Exception;

    SnitchPlayer getPlayer(int playerID) throws Exception;

    SnitchWorld getWorld(int worldID);

    void markReverted(SnitchEntry entry, boolean reverted) throws Exception;

    void closeConnection() throws IOException;
}
