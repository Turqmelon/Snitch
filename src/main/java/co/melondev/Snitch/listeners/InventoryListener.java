package co.melondev.Snitch.listeners;

import co.melondev.Snitch.SnitchPlugin;
import co.melondev.Snitch.entities.SnitchPlayer;
import co.melondev.Snitch.entities.SnitchPosition;
import co.melondev.Snitch.entities.SnitchWorld;
import co.melondev.Snitch.enums.EnumAction;
import co.melondev.Snitch.enums.EnumDefaultPlayer;
import co.melondev.Snitch.util.JsonUtil;
import com.google.gson.JsonObject;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Hopper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

public class InventoryListener implements Listener {

    private SnitchPlugin i;

    public InventoryListener(SnitchPlugin i) {
        this.i = i;
    }

    private void logAction(EnumDefaultPlayer defaultPlayer, ItemStack itemStack, Location location, EnumAction action) {
        logAction(defaultPlayer, itemStack, location, action, null);
    }

    private void logAction(Player player, ItemStack itemStack, Location location, EnumAction action){
        logAction(player, itemStack, location, action, null);
    }

    private void logAction(Player player, Location location, ItemStack itemStack, EnumAction action, int slot, InventoryClickEvent event) {
        if (action.isEnabled()) {
            int finalQty = itemStack.getAmount();
            if (event != null) {
                if (event.isRightClick()) {
                    switch (action) {
                        case ITEM_TAKE:
                            finalQty = (finalQty - (int) Math.floor(finalQty / 2));
                            break;
                        case ITEM_INSERT:
                            finalQty = 1;
                            break;
                    }
                }
            }
            JsonObject data = new JsonObject();
            data.add("item", JsonUtil.jsonify(itemStack));
            data.addProperty("changedCount", finalQty);
            data.addProperty("slot", slot);
            logAction(player, itemStack, location, action, data);
        }
    }

    private void logAction(EnumDefaultPlayer defaultPlayer, ItemStack itemStack, Location location, EnumAction action, JsonObject data) {
        i.async(() -> {
            try {
                SnitchPlayer snitchPlayer = defaultPlayer.getSnitchPlayer();
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
    public void onPickup(EntityPickupItemEvent event) {
        Entity entity = event.getEntity();
        if (!EnumAction.ITEM_DROP.isEnabled())
            return;
        if ((entity instanceof Player)) {
            Player player = (Player) entity;
            ItemStack itemStack = event.getItem().getItemStack();
            logAction(player, itemStack, player.getLocation(), EnumAction.ITEM_PICKUP);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onClick(InventoryClickEvent event) {
        if (EnumAction.ITEM_INSERT.isEnabled() || EnumAction.ITEM_TAKE.isEnabled()) {
            HumanEntity clicker = event.getWhoClicked();
            if ((clicker instanceof Player)) {
                Player player = (Player) clicker;
                ItemStack current = event.getCurrentItem();
                ItemStack cursor = event.getCursor();

                InventoryHolder holder = event.getInventory().getHolder();

                Location location = null;
                if ((holder instanceof BlockState)) {
                    location = ((BlockState) holder).getLocation();
                } else if ((holder instanceof Entity)) {
                    location = ((Entity) holder).getLocation();
                } else if ((holder instanceof DoubleChest)) {
                    location = ((DoubleChest) holder).getLocation();
                }

                int size = ((holder instanceof DoubleChest)) ? event.getView().getType().getDefaultSize() * 2 : event.getView().getType().getDefaultSize();
                int slot = event.getSlot();
                int rawSlot = event.getRawSlot();

                if (slot == rawSlot && rawSlot <= size) {
                    ItemStack added = null;
                    ItemStack taken = null;

                    if (current != null && current.getType() != Material.AIR && cursor != null && cursor.getType() != Material.AIR) {
                        if (current.isSimilar(cursor)) {
                            int count = event.isRightClick() ? 1 : current.getAmount();
                            int left = current.getMaxStackSize() - current.getAmount();
                            int inserted = count <= left ? count : left;
                            if (inserted > 0) {
                                added = cursor.clone();
                                added.setAmount(inserted);
                            }
                        } else {
                            added = cursor.clone();
                            taken = current.clone();
                        }
                    } else if (current != null && current.getType() != Material.AIR) {
                        taken = current.clone();
                    } else if (cursor != null && cursor.getType() != Material.AIR) {
                        added = cursor.clone();
                    }

                    if (added != null) {
                        logAction(player, location, added, EnumAction.ITEM_INSERT, rawSlot, event);
                    }
                    if (taken != null) {
                        logAction(player, location, taken, EnumAction.ITEM_TAKE, rawSlot, event);
                    }
                }

                if (event.isShiftClick() && cursor != null && cursor.getType() != Material.AIR) {
                    logAction(player, location, current, EnumAction.ITEM_INSERT, -1, event);
                }

            }

        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDrag(InventoryDragEvent event) {
        if (EnumAction.ITEM_TAKE.isEnabled() || EnumAction.ITEM_INSERT.isEnabled()) {
            InventoryHolder holder = event.getInventory().getHolder();
            if ((holder instanceof BlockState)) {
                Location loc = ((BlockState) holder).getLocation();
                HumanEntity clicker = event.getWhoClicked();
                if ((clicker instanceof Player)) {
                    Player player = (Player) clicker;
                    for (Map.Entry<Integer, ItemStack> newItems : event.getNewItems().entrySet()) {
                        logAction(player, loc, newItems.getValue(), EnumAction.ITEM_INSERT, newItems.getKey(), null);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryPickup(InventoryPickupItemEvent event) {
        if (!EnumAction.ITEM_PICKUP.isEnabled()) {
            return;
        }
        if ((event.getInventory() instanceof Hopper)) {
            logAction(EnumDefaultPlayer.HOPPER, event.getItem().getItemStack().clone(), event.getItem().getLocation(), EnumAction.ITEM_PICKUP);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMoveItem(InventoryMoveItemEvent event) {
        if (EnumAction.ITEM_INSERT.isEnabled() && event.getDestination() != null) {
            InventoryHolder dest = event.getSource().getHolder();
            Location location = null;
            if ((dest instanceof BlockState)) {
                location = ((BlockState) dest).getLocation();
            }
            if (location != null && (event.getSource() instanceof Hopper)) {
                logAction(EnumDefaultPlayer.HOPPER, event.getItem().clone(), location, EnumAction.ITEM_INSERT);
            }
        }
        if (EnumAction.ITEM_TAKE.isEnabled() && event.getSource() != null) {
            InventoryHolder source = event.getSource().getHolder();
            Location location = null;
            if ((source instanceof BlockState)) {
                location = ((BlockState) source).getLocation();
            }
            if (location != null) {
                if ((event.getDestination() instanceof Hopper)) {
                    logAction(EnumDefaultPlayer.HOPPER, event.getItem().clone(), location, EnumAction.ITEM_TAKE);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (!EnumAction.ITEM_DROP.isEnabled())
            return;
        ItemStack itemStack = event.getItemDrop().getItemStack();
        logAction(player, itemStack, player.getLocation(), EnumAction.ITEM_DROP);
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
