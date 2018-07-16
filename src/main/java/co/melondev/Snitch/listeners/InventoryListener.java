package co.melondev.Snitch.listeners;

import co.melondev.Snitch.SnitchPlugin;
import co.melondev.Snitch.entities.SnitchPlayer;
import co.melondev.Snitch.entities.SnitchPosition;
import co.melondev.Snitch.entities.SnitchWorld;
import co.melondev.Snitch.enums.EnumAction;
import co.melondev.Snitch.util.JsonUtil;
import com.google.gson.JsonObject;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.Set;

public class InventoryListener implements Listener {

    private SnitchPlugin i;

    public InventoryListener(SnitchPlugin i) {
        this.i = i;
    }

    private void logAction(Player player, ItemStack itemStack, Location location, EnumAction action){
        logAction(player, itemStack, location, action, null);
    }

    private void logAction(Player player, ItemStack itemStack, Location location, EnumAction action, JsonObject data){
        i.async(()->{
            try {
                SnitchPlayer snitchPlayer = i.getStorage().getPlayer(player.getUniqueId());
                SnitchWorld world = i.getStorage().register(location.getWorld());
                SnitchPosition position = new SnitchPosition(location);
                JsonObject d = data;
                if (d == null) {
                    d = new JsonObject();
                    d.add("item", JsonUtil.jsonify(itemStack));
                }
                i.getStorage().record(action, snitchPlayer, world, position, d, System.currentTimeMillis());
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEnchant(EnchantItemEvent event){

        if (!EnumAction.ENCHANT_ITEM.isEnabled()){
            return;
        }

        Player player = event.getEnchanter();
        ItemStack itemStack = event.getItem();
        Block block = event.getEnchantBlock();

        JsonObject obj = new JsonObject();
        obj.add("item", JsonUtil.jsonify(itemStack));
        obj.add("block", JsonUtil.jsonify(block.getState()));
        obj.add("enchants", JsonUtil.jsonify(event.getEnchantsToAdd()));

        logAction(player, itemStack, block.getLocation(), EnumAction.ENCHANT_ITEM, obj);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCraft(CraftItemEvent event){
        if (!EnumAction.CRAFT_ITEM.isEnabled()){
            return;
        }
        HumanEntity e = event.getWhoClicked();
        if ((e instanceof Player)){
            Player p = (Player) e;
            ItemStack itemStack = event.getRecipe().getResult();
            JsonObject data = new JsonObject();
            data.add("item", JsonUtil.jsonify(itemStack));

            Location l;
            Block target = p.getTargetBlock((Set<Material>)null, 10);
            if (target != null && target.getType() == Material.WORKBENCH){
                l = target.getLocation();
            }
            else{
                l = p.getLocation();
            }

            logAction(p, itemStack, l, EnumAction.CRAFT_ITEM, data);
        }
    }
}
