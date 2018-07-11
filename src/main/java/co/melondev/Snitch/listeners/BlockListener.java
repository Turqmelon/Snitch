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
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.SheepDyeWoolEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class BlockListener implements Listener {

    private SnitchPlugin i;
    private final List<Material> interactables = Arrays.asList(
            Material.ACACIA_DOOR,
            Material.ACACIA_FENCE_GATE,
            Material.ANVIL,
            Material.BEACON,
            Material.BED,
            Material.BIRCH_DOOR,
            Material.BIRCH_FENCE_GATE,
            Material.BOAT,
            Material.BOAT_ACACIA,
            Material.BOAT_BIRCH,
            Material.BOAT_DARK_OAK,
            Material.BOAT_JUNGLE,
            Material.BOAT_SPRUCE,
            Material.BREWING_STAND,
            Material.COMMAND,
            Material.CHEST,
            Material.DARK_OAK_DOOR,
            Material.DARK_OAK_FENCE_GATE,
            Material.DAYLIGHT_DETECTOR,
            Material.DAYLIGHT_DETECTOR_INVERTED,
            Material.DISPENSER,
            Material.DROPPER,
            Material.ENCHANTMENT_TABLE,
            Material.ENDER_CHEST,
            Material.FENCE_GATE,
            Material.FURNACE,
            Material.HOPPER,
            Material.HOPPER_MINECART,
            Material.ITEM_FRAME,
            Material.JUNGLE_DOOR,
            Material.JUNGLE_FENCE_GATE,
            Material.LEVER,
            Material.MINECART,
            Material.NOTE_BLOCK,
            Material.POWERED_MINECART,
            Material.REDSTONE_COMPARATOR,
            Material.REDSTONE_COMPARATOR_OFF,
            Material.REDSTONE_COMPARATOR_ON,
            Material.SIGN,
            Material.SIGN_POST,
            Material.STORAGE_MINECART,
            Material.TRAP_DOOR,
            Material.TRAPPED_CHEST,
            Material.WALL_SIGN,
            Material.WOOD_BUTTON,
            Material.WOOD_DOOR);

    public BlockListener(SnitchPlugin i) {
        this.i = i;
    }

    private void logBlockAction(Player player, Block block, EnumAction action){
        logBlockAction(player, block, action, null);
    }

    private void logBlockAction(Player player, Block block, EnumAction action, JsonObject data){
        i.async(()->{
            SnitchPlayer snitchPlayer = i.getStorage().getPlayer(player.getUniqueId());
            SnitchWorld world = i.getStorage().register(block.getWorld());
            SnitchPosition position = new SnitchPosition(block);
            JsonObject d = data;
            if (d == null){
                d = new JsonObject();
                d.add("block", JsonUtil.jsonify(block));
            }
            i.getStorage().record(action, snitchPlayer, world, position, d, System.currentTimeMillis());
        });
    }

    private void logBlockAction(EnumDefaultPlayer defaultPlayer, Block block, EnumAction action, JsonObject data){
        i.async(()->{
            SnitchWorld world = i.getStorage().register(block.getWorld());
            SnitchPosition position = new SnitchPosition(block);
            JsonObject d = data;
            if (d == null){
                d = new JsonObject();
                d.add("block", JsonUtil.jsonify(block));
            }
            i.getStorage().record(action, defaultPlayer.getSnitchPlayer(), world, position, d, System.currentTimeMillis());
        });
    }

    private void logBlockAction(EnumDefaultPlayer defaultPlayer, Block block, EnumAction action){
        this.logBlockAction(defaultPlayer, block, action, null);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEmpty(PlayerBucketEmptyEvent event){
        if (!EnumAction.BUCKET_EMPTY.isEnabled()){
            return;
        }

        final Player player = event.getPlayer();
        final Block block = event.getBlockClicked();
        final Material material = event.getBucket();
        JsonObject obj = new JsonObject();
        obj.add("block", JsonUtil.jsonify(block));
        obj.add("bucket", JsonUtil.jsonify(new ItemStack(material)));
        logBlockAction(player, block, EnumAction.BUCKET_EMPTY, obj);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFill(PlayerBucketFillEvent event){
        if (!EnumAction.BUCKET_FILL.isEnabled()){
            return;
        }
        final Player player = event.getPlayer();
        final Block block = event.getBlockClicked();
        final Material material = event.getBucket();
        JsonObject obj = new JsonObject();
        obj.add("block", JsonUtil.jsonify(block));
        obj.add("bucket", JsonUtil.jsonify(new ItemStack(material)));
        logBlockAction(player, block, EnumAction.BUCKET_FILL, obj);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event){
        Player player = event.getPlayer();
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK){
            Block block = event.getClickedBlock();
            if (interactables.contains(block.getType()) && EnumAction.BLOCK_USE.isEnabled()){
                logBlockAction(player, block, EnumAction.BLOCK_USE);
            }
        }
        else if (event.getAction() == Action.PHYSICAL){
            Block block = event.getClickedBlock();
            if (block.getType() == Material.SOIL){
                if (EnumAction.CROP_TRAMPLE.isEnabled()){
                    logBlockAction(player, block, EnumAction.CROP_TRAMPLE);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSpread(BlockSpreadEvent event){
        if (!EnumAction.BLOCK_SPREAD.isEnabled()){
            return;
        }
        final Block block = event.getBlock();
        final Block source = event.getSource();
        JsonObject obj = new JsonObject();
        obj.add("block", JsonUtil.jsonify(block));
        obj.add("source", JsonUtil.jsonify(source));
        logBlockAction(EnumDefaultPlayer.BLOCK, block, EnumAction.BLOCK_SPREAD, obj);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event){
        if (!EnumAction.BLOCK_PLACE.isEnabled())
            return;
        final Player player = event.getPlayer();
        final Block block = event.getBlock();
        logBlockAction(player, block, EnumAction.BLOCK_PLACE);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onForm(BlockFormEvent event){
        if (!EnumAction.BLOCK_FORM.isEnabled())
            return;
        final Block block = event.getNewState().getBlock();
        logBlockAction(EnumDefaultPlayer.BLOCK, block, EnumAction.BLOCK_FORM);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityChangeBlock(EntityChangeBlockEvent event){
        Entity entity = event.getEntity();
        if ((entity instanceof FallingBlock)){
            Block block = event.getBlock();
            if (EnumAction.BLOCK_FALL.isEnabled()){
                logBlockAction(EnumDefaultPlayer.BLOCK, block, EnumAction.BLOCK_FALL);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFade(BlockFadeEvent event){
        if (!EnumAction.BLOCK_FADE.isEnabled())
            return;
        final Block block = event.getNewState().getBlock();
        logBlockAction(EnumDefaultPlayer.BLOCK, block, EnumAction.BLOCK_FADE);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDispense(BlockDispenseEvent event){
        if (!EnumAction.BLOCK_DISPENSE.isEnabled())
            return;
        Block block = event.getBlock();
        ItemStack itemStack = event.getItem();
        JsonObject obj = new JsonObject();
        obj.add("block", JsonUtil.jsonify(block));
        obj.add("item", JsonUtil.jsonify(itemStack));
        logBlockAction(EnumDefaultPlayer.BLOCK, block, EnumAction.BLOCK_DISPENSE, obj);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event){
        if (!EnumAction.BLOCK_BREAK.isEnabled())
            return;
        final Player player = event.getPlayer();
        final Block block = event.getBlock();
        logBlockAction(player, block, EnumAction.BLOCK_BREAK);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBurn(BlockBurnEvent event){
        if (!EnumAction.BLOCK_BURN.isEnabled())
            return;
        final Block block = event.getBlock();
        logBlockAction(EnumDefaultPlayer.FIRE, block, EnumAction.BLOCK_BURN);
    }
}
