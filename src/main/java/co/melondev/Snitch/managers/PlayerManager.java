package co.melondev.Snitch.managers;

import co.melondev.Snitch.SnitchPlugin;
import co.melondev.Snitch.entities.SnitchSession;
import co.melondev.Snitch.enums.EnumDefaultPlayer;
import co.melondev.Snitch.util.SnitchDatabaseException;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PlayerManager {

    private SnitchPlugin i;
    private Cache<UUID, SnitchSession> sessionCache = CacheBuilder.newBuilder().concurrencyLevel(4).expireAfterAccess(5, TimeUnit.MINUTES).build();

    public PlayerManager(SnitchPlugin instance) {
        this.i = instance;
        try {
            this.registerDefaultPlayers();
        } catch (SnitchDatabaseException e) {
            e.printStackTrace();
        }
    }

    public void setSession(Player player, SnitchSession snitchSession) {
        sessionCache.put(player.getUniqueId(), snitchSession);
    }

    public SnitchSession getSession(Player player) {
        return sessionCache.getIfPresent(player.getUniqueId());
    }

    private void registerDefaultPlayers() throws SnitchDatabaseException {
        for(EnumDefaultPlayer defaultPlayer : EnumDefaultPlayer.values()){
            i.getStorage().registerPlayer(defaultPlayer.getStorageName(), defaultPlayer.getUuid());
        }
    }


}
