package co.melondev.Snitch.listeners;

import co.melondev.Snitch.SnitchPlugin;
import co.melondev.Snitch.entities.SnitchPlayer;
import co.melondev.Snitch.entities.SnitchPosition;
import co.melondev.Snitch.entities.SnitchWorld;
import co.melondev.Snitch.enums.EnumAction;
import co.melondev.Snitch.enums.EnumDefaultPlayer;
import co.melondev.Snitch.util.JsonUtil;
import com.google.gson.JsonObject;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.event.player.PlayerUnleashEntityEvent;

import java.sql.SQLException;

public class EntityListener implements Listener {

    private SnitchPlugin i;

    public EntityListener(SnitchPlugin i) {
        this.i = i;
    }

    private void logAction(EnumDefaultPlayer defaultPlayer, Entity entity, Location location, EnumAction action){
        logAction(defaultPlayer, entity, location, action, null);
    }

    private void logAction(EnumDefaultPlayer defaultPlayer, Entity entity, Location location, EnumAction action, JsonObject data){
        i.async(()->{
            try {
                SnitchPlayer snitchPlayer = defaultPlayer.getSnitchPlayer();
                SnitchWorld world = i.getStorage().register(location.getWorld());
                SnitchPosition position = new SnitchPosition(location);
                JsonObject d = data;
                if (d == null) {
                    d = new JsonObject();
                    d.add("entity", JsonUtil.jsonify(entity));
                }
                i.getStorage().record(action, snitchPlayer, world, position, d, System.currentTimeMillis());
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    private void logAction(Player player, Entity entity, Location location, EnumAction action){
        logAction(player, entity, location, action, null);
    }

    private void logAction(Player player, Entity entity, Location location, EnumAction action, JsonObject data){
        i.async(()->{
            try {
                SnitchPlayer snitchPlayer = i.getStorage().getPlayer(player.getUniqueId());
                SnitchWorld world = i.getStorage().register(location.getWorld());
                SnitchPosition position = new SnitchPosition(location);
                JsonObject d = data;
                if (d == null) {
                    d = new JsonObject();
                    d.add("entity", JsonUtil.jsonify(entity));
                }
                i.getStorage().record(action, snitchPlayer, world, position, d, System.currentTimeMillis());
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    private void logBlockAction(Player player, Block block, EnumAction action){
        logBlockAction(player, block, action, null);
    }

    private void logBlockAction(Player player, Block block, EnumAction action, JsonObject data){
        i.async(()->{
            try {
                SnitchPlayer snitchPlayer = i.getStorage().getPlayer(player.getUniqueId());
                SnitchWorld world = i.getStorage().register(block.getWorld());
                SnitchPosition position = new SnitchPosition(block);
                JsonObject d = data;
                if (d == null) {
                    d = new JsonObject();
                    d.add("block", JsonUtil.jsonify(block));
                }
                i.getStorage().record(action, snitchPlayer, world, position, d, System.currentTimeMillis());
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onShear(PlayerShearEntityEvent event){
        if (!EnumAction.ENTITY_SHEAR.isEnabled()){
            return;
        }
        Player player = event.getPlayer();
        Entity entity = event.getEntity();
        logAction(player, entity, entity.getLocation(), EnumAction.ENTITY_SHEAR);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onUnleash(PlayerUnleashEntityEvent event){
        if (!EnumAction.ENTITY_UNLEASH.isEnabled()){
            return;
        }
        Player player = event.getPlayer();
        Entity entity = event.getEntity();
        logAction(player, entity, entity.getLocation(), EnumAction.ENTITY_UNLEASH);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onLeash(PlayerLeashEntityEvent event){
        if (!EnumAction.ENTITY_LEASH.isEnabled()){
            return;
        }
        Player player = event.getPlayer();
        Entity entity = event.getEntity();
        logAction(player, entity, entity.getLocation(), EnumAction.ENTITY_LEASH);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDeath(EntityDeathEvent event){
        if (!EnumAction.ENTITY_KILL.isEnabled()){
            return;
        }
        LivingEntity entity = event.getEntity();
        if (entity.getKiller() != null){
            logAction(entity.getKiller(), entity, entity.getLocation(), EnumAction.ENTITY_KILL);
        }
        else{
            EntityDamageEvent.DamageCause cause = entity.getLastDamageCause().getCause();
            EnumDefaultPlayer actor = null;
            switch(cause){
                case LAVA:
                    actor = EnumDefaultPlayer.LAVA;
                    break;
                case FIRE:
                    actor = EnumDefaultPlayer.FIRE;
                    break;
                case DROWNING:
                    actor = EnumDefaultPlayer.WATER;
                    break;
                case ENTITY_EXPLOSION:
                    actor = EnumDefaultPlayer.CREEPER;
                    break;
                case BLOCK_EXPLOSION:
                    actor = EnumDefaultPlayer.TNT;
                    break;
                case SUFFOCATION:
                    actor = EnumDefaultPlayer.BLOCK;
                    break;
            }
            if (actor != null){
                logAction(actor, entity, entity.getLocation(), EnumAction.ENTITY_KILL);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onForm(EntityBlockFormEvent event){
        if (!EnumAction.ENTITY_FORMED.isEnabled()){
            return;
        }
        Entity entity = event.getEntity();
        Block block = event.getNewState().getBlock();
        JsonObject obj = new JsonObject();
        obj.add("entity", JsonUtil.jsonify(entity));
        obj.add("block", JsonUtil.jsonify(block));
        obj.add("oldBlock", JsonUtil.jsonify(event.getBlock()));

        logAction(EnumDefaultPlayer.BLOCK, entity, block.getLocation(), EnumAction.ENTITY_FORMED, obj);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTarget(EntityTargetLivingEntityEvent event){
        if (!EnumAction.ENTITY_FOLLOW.isEnabled()){
            return;
        }
        Entity entity = event.getEntity();
        Entity target = event.getTarget();
        if ((target instanceof Player)){
            Player player = (Player) target;
            logAction(player, entity, entity.getLocation(), EnumAction.ENTITY_FOLLOW);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onExplode(EntityExplodeEvent event){
        if (!EnumAction.ENTITY_EXPLODE.isEnabled())
            return;
        Player actor = null;
        EnumDefaultPlayer defaultActor = EnumDefaultPlayer.BLOCK;
        Entity entity = event.getEntity();
        if ((entity instanceof TNTPrimed)){
            TNTPrimed tntPrimed = (TNTPrimed) entity;
            defaultActor = EnumDefaultPlayer.TNT;
            Entity source = tntPrimed.getSource();
            if ((source instanceof Player)){
                actor = (Player) source;
            }
        }
        else if ((entity instanceof Creeper)){
            Creeper creeper = (Creeper)entity;
            defaultActor = EnumDefaultPlayer.CREEPER;
            Entity target = creeper.getTarget();
            if ((target instanceof Player)){
                actor = (Player) target;
            }
        }
        if (actor != null && EnumAction.BLOCK_EXPLODE.isEnabled()){
            for(Block block : event.blockList()){
                logBlockAction(actor, block, EnumAction.BLOCK_EXPLODE);
            }
        }
        logAction(defaultActor, entity, entity.getLocation(), EnumAction.ENTITY_EXPLODE);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDye(SheepDyeWoolEvent event){
        if (!EnumAction.ENTITY_DYE.isEnabled()){
            return;
        }
        Player player = null;
        for(Entity entity : event.getEntity().getNearbyEntities(10, 10, 10)){
            if ((entity instanceof Player)){
                player = (Player) entity;
                break;
            }
        }
        Entity entity = event.getEntity();
        DyeColor color = event.getColor();

        JsonObject data = new JsonObject();
        data.add("entity", JsonUtil.jsonify(entity));
        data.add("dye", JsonUtil.jsonify(color));

        logAction(player, entity, entity.getLocation(), EnumAction.ENTITY_DYE, data);
    }

}
