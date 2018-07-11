package co.melondev.Snitch.managers;

import co.melondev.Snitch.SnitchPlugin;
import co.melondev.Snitch.enums.EnumDefaultPlayer;

import java.util.UUID;

public class PlayerManager {

    private SnitchPlugin i;

    public PlayerManager(SnitchPlugin instance) {
        this.i = instance;
        this.registerDefaultPlayers();
    }

    private void registerDefaultPlayers() {
        for(EnumDefaultPlayer defaultPlayer : EnumDefaultPlayer.values()){
            i.getStorage().registerPlayer(defaultPlayer.getStorageName(), defaultPlayer.getUuid());
        }
    }


}
