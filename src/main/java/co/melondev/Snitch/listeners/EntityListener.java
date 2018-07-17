package co.melondev.Snitch.listeners;

import co.melondev.Snitch.SnitchPlugin;
import co.melondev.Snitch.entities.SnitchPlayer;
import co.melondev.Snitch.entities.SnitchPosition;
import co.melondev.Snitch.entities.SnitchWorld;
import co.melondev.Snitch.enums.EnumAction;
import co.melondev.Snitch.enums.EnumDefaultPlayer;
import co.melondev.Snitch.util.ItemUtil;
import co.melondev.Snitch.util.JsonUtil;
import com.google.gson.JsonObject;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.event.player.PlayerUnleashEntityEvent;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;

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
            } catch (Exception ex) {
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
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    private void logBlockAction(Player player, BlockState block, EnumAction action) {
        logBlockAction(player, block, action, null);
    }

    private void logBlockAction(Player player, BlockState block, EnumAction action, JsonObject data) {
        i.async(()->{
            try {
                SnitchPlayer snitchPlayer = i.getStorage().getPlayer(player.getUniqueId());
                SnitchWorld world = i.getStorage().register(block.getWorld());
                SnitchPosition position = new SnitchPosition(block.getLocation());
                JsonObject d = data;
                if (d == null) {
                    d = new JsonObject();
                    d.add("block", JsonUtil.jsonify(block));
                }
                i.getStorage().record(action, snitchPlayer, world, position, d, System.currentTimeMillis());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSplash(PotionSplashEvent event) {
        ThrownPotion potion = event.getPotion();
        if (potion.getShooter() instanceof Player) {
            if (EnumAction.POTION_SPLASH.isEnabled()) {
                logAction((Player) potion.getShooter(), potion, potion.getLocation(), EnumAction.POTION_SPLASH);
            }
        }
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
    public void onDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if ((entity instanceof ArmorStand)) {
            if ((event instanceof EntityDamageByEntityEvent)) {
                EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
                if ((e.getDamager() instanceof Player)) {
                    Player player = (Player) e.getDamager();
                    if (EnumAction.ARMORSTAND_BREAK.isEnabled()) {
                        logAction(player, entity, entity.getLocation(), EnumAction.ARMORSTAND_BREAK);
                    }
                }
            }
        }
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
        obj.add("block", JsonUtil.jsonify(block.getState()));
        obj.add("oldBlock", JsonUtil.jsonify(event.getBlock().getState()));

        logAction(EnumDefaultPlayer.BLOCK, entity, block.getLocation(), EnumAction.ENTITY_FORMED, obj);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onXPPickup(PlayerExpChangeEvent event) {
        if (!EnumAction.XP_PICKUP.isEnabled()) {
            return;
        }
        Player player = event.getPlayer();
        int amount = event.getAmount();
        JsonObject data = new JsonObject();
        data.addProperty("amount", amount);
        logAction(player, null, player.getLocation(), EnumAction.XP_PICKUP, data);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onVehiclePlace(VehicleCreateEvent event) {
        if (!EnumAction.VEHICLE_PLACE.isEnabled()) {
            return;
        }
        Vehicle vehicle = event.getVehicle();
        Player player = null;
        for (Entity entity : vehicle.getNearbyEntities(10, 10, 10)) {
            if ((entity instanceof Player)) {
                player = (Player) entity;
                break;
            }
        }
        if (player != null) {
            logAction(player, vehicle, vehicle.getLocation(), EnumAction.VEHICLE_PLACE);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBreak(VehicleDestroyEvent event) {
        if (!EnumAction.VEHICLE_BREAK.isEnabled()) {
            return;
        }
        Entity attacker = event.getAttacker();
        if (attacker != null && (attacker instanceof Player)) {
            Player player = (Player) attacker;
            logAction(player, event.getVehicle(), event.getVehicle().getLocation(), EnumAction.VEHICLE_BREAK);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSpawnEgg(CreatureSpawnEvent event) {
        Entity entity = event.getEntity();
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER_EGG && EnumAction.SPAWNEGG_USE.isEnabled()) {
            Player player = null;
            for (Entity e : entity.getNearbyEntities(20, 20, 20)) {
                if ((e instanceof Player)) {
                    player = (Player) e;
                    break;
                }
            }
            if (player != null) {
                JsonObject obj = new JsonObject();
                obj.addProperty("type", entity.getType().name());
                logAction(player, entity, entity.getLocation(), EnumAction.SPAWNEGG_USE, obj);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onArmorEdit(PlayerArmorStandManipulateEvent event) {
        if (!EnumAction.ARMORSTAND_EDIT.isEnabled())
            return;
        ArmorStand armorStand = event.getRightClicked();
        JsonObject obj = new JsonObject();
        obj.add("entity", JsonUtil.jsonify(event.getRightClicked()));
        obj.addProperty("slot", event.getSlot().name());
        obj.addProperty("helm", armorStand.getHelmet() != null ? ItemUtil.itemToJSON(armorStand.getHelmet()) : null);
        obj.addProperty("chest", armorStand.getChestplate() != null ? ItemUtil.itemToJSON(armorStand.getChestplate()) : null);
        obj.addProperty("legs", armorStand.getLeggings() != null ? ItemUtil.itemToJSON(armorStand.getLeggings()) : null);
        obj.addProperty("boots", armorStand.getBoots() != null ? ItemUtil.itemToJSON(armorStand.getBoots()) : null);
        obj.addProperty("hand", armorStand.getItemInHand() != null ? ItemUtil.itemToJSON(armorStand.getItemInHand()) : null);

        logAction(event.getPlayer(), armorStand, armorStand.getLocation(), EnumAction.ARMORSTAND_EDIT, obj);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTNT(EntitySpawnEvent event) {
        Entity entity = event.getEntity();
        if ((entity instanceof TNTPrimed) && EnumAction.TNT_PRIME.isEnabled()) {
            Player player = null;
            for (Entity e : entity.getNearbyEntities(10, 10, 10)) {
                if ((e instanceof Player)) {
                    player = (Player) e;
                    break;
                }
            }
            if (player != null) {
                logAction(player, entity, entity.getLocation(), EnumAction.TNT_PRIME);
            }
        } else if ((entity instanceof ArmorStand) && EnumAction.ARMORSTAND_CREATE.isEnabled()) {
            Player player = null;
            for (Entity e : entity.getNearbyEntities(10, 10, 10)) {
                if ((e instanceof Player)) {
                    player = (Player) e;
                    break;
                }
            }
            if (player != null) {
                logAction(player, entity, entity.getLocation(), EnumAction.ARMORSTAND_CREATE);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onExit(VehicleEnterEvent event) {
        if (!EnumAction.VEHICLE_ENTER.isEnabled()) {
            return;
        }
        Entity le = event.getEntered();
        Entity vehicle = le.getVehicle();
        if ((le instanceof Player)) {
            logAction((Player) le, vehicle, vehicle.getLocation(), EnumAction.VEHICLE_ENTER);
        }

    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onExit(VehicleExitEvent event) {
        if (!EnumAction.VEHICLE_ENTER.isEnabled()) {
            return;
        }
        LivingEntity le = event.getExited();
        Entity vehicle = le.getVehicle();
        if ((le instanceof Player)) {
            logAction((Player) le, vehicle, vehicle.getLocation(), EnumAction.VEHICLE_EXIT);
        }

    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTarget(EntityTargetLivingEntityEvent event){
        if (!EnumAction.ENTITY_FOLLOW.isEnabled()){
            return;
        }
        Entity entity = event.getEntity();
        if (!(entity instanceof LivingEntity))
            return;
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
                logBlockAction(actor, block.getState(), EnumAction.BLOCK_EXPLODE);
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
