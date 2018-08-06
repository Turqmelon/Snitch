package co.melondev.Snitch.storage;

import co.melondev.Snitch.entities.*;
import co.melondev.Snitch.enums.EnumAction;
import co.melondev.Snitch.util.SnitchDatabaseException;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import org.bukkit.World;

import java.io.IOException;
import java.util.UUID;

public interface StorageMethod {

    ImmutableList<SnitchWorld> getWorlds();

    SnitchWorld register(World world) throws SnitchDatabaseException;

    SnitchPlayer registerPlayer(String playerName, UUID uuid) throws SnitchDatabaseException;

    SnitchPlayer getPlayer(UUID uuid) throws SnitchDatabaseException;

    SnitchPlayer getPlayer(String playerName) throws SnitchDatabaseException;

    SnitchEntry record(EnumAction action, SnitchPlayer player, SnitchWorld world, SnitchPosition position, JsonObject data, long time) throws SnitchDatabaseException;

    ImmutableList<SnitchEntry> performLookup(SnitchQuery query) throws SnitchDatabaseException;

    SnitchPlayer getPlayer(int playerID) throws SnitchDatabaseException;

    SnitchWorld getWorld(int worldID);

    void markReverted(SnitchEntry entry, boolean reverted) throws SnitchDatabaseException;

    void closeConnection() throws IOException;
}
