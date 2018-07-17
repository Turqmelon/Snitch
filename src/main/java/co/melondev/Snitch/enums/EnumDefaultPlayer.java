package co.melondev.Snitch.enums;

import co.melondev.Snitch.SnitchPlugin;
import co.melondev.Snitch.entities.SnitchPlayer;

import java.sql.SQLException;
import java.util.UUID;

public enum EnumDefaultPlayer {

    DRAGON("Dragon", "123e4567-e89b-42d3-a456-556642440001"),
    ENDERMAN("Enderman", "123e4567-e89b-42d3-a456-556642440002"),
    LAVA("Lava", "123e4567-e89b-42d3-a456-556642440003"),
    WATER("Water", "123e4567-e89b-42d3-a456-556642440004"),
    FIRE("Fire", "123e4567-e89b-42d3-a456-556642440005"),
    BLOCK("Block", "123e4567-e89b-42d3-a456-556642440006"),
    TNT("TNT", "123e4567-e89b-42d3-a456-556642440007"),
    CREEPER("Creeper", "123e4567-e89b-42d3-a456-556642440008");

    private String name;
    private UUID uuid;

    EnumDefaultPlayer(String name, String uuid) {
        this.name = name;
        this.uuid = UUID.fromString(uuid);
    }

    public SnitchPlayer getSnitchPlayer() throws Exception {
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
