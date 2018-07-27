package co.melondev.Snitch.enums;

import co.melondev.Snitch.SnitchPlugin;
import co.melondev.Snitch.entities.SnitchPlayer;

import java.sql.SQLException;
import java.util.UUID;

/**
 * A list of default actors. These are prefixed by "S-" in storage to prevent collisions with actual players.
 */
public enum EnumDefaultPlayer {

    DRAGON("Dragon", "123e4567-e89b-42d3-a456-556642440001"),
    ENDERMAN("Enderman", "123e4567-e89b-42d3-a456-556642440002"),
    LAVA("Lava", "123e4567-e89b-42d3-a456-556642440003"),
    WATER("Water", "123e4567-e89b-42d3-a456-556642440004"),
    FIRE("Fire", "123e4567-e89b-42d3-a456-556642440005"),
    BLOCK("Block", "123e4567-e89b-42d3-a456-556642440006"),
    TNT("TNT", "123e4567-e89b-42d3-a456-556642440007"),
    CREEPER("Creeper", "123e4567-e89b-42d3-a456-556642440008"),
    HOPPER("Hopper", "123e4567-e89b-42d3-a456-556642440009");

    private String name;
    private UUID uuid;

    EnumDefaultPlayer(String name, String uuid) {
        this.name = name;
        this.uuid = UUID.fromString(uuid);
    }

    /**
     * Get this actor as a Snitch Player.
     *
     * @return the SnitchPlayer object for this player
     * @throws SQLException if there are issues retrieving the data
     */
    public SnitchPlayer getSnitchPlayer() throws SQLException {
        return SnitchPlugin.getInstance().getStorage().getPlayer(getStorageName());
    }

    public String getStorageName(){
        return "S-" + getName();
    }

    public String getName() {
        return name;
    }

    public UUID getUuid() {
        return uuid;
    }
}
