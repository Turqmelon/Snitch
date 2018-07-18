package co.melondev.Snitch.listeners;

import co.melondev.Snitch.SnitchPlugin;
import co.melondev.Snitch.entities.*;
import co.melondev.Snitch.enums.EnumAction;
import co.melondev.Snitch.enums.EnumDefaultPlayer;
import co.melondev.Snitch.util.InvUtil;
import co.melondev.Snitch.util.JsonUtil;
import co.melondev.Snitch.util.MsgUtil;
import com.google.gson.JsonObject;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class BlockListener implements Listener {

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
    private SnitchPlugin i;

    public BlockListener(SnitchPlugin i) {
        this.i = i;
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
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    private void logBlockAction(EnumDefaultPlayer defaultPlayer, BlockState block, EnumAction action, JsonObject data) {
        i.async(()->{
            try {
                SnitchWorld world = i.getStorage().register(block.getWorld());
                SnitchPosition position = new SnitchPosition(block.getLocation());
                JsonObject d = data;
                if (d == null) {
                    d = new JsonObject();
                    d.add("block", JsonUtil.jsonify(block));
                }
                i.getStorage().record(action, defaultPlayer.getSnitchPlayer(), world, position, d, System.currentTimeMillis());
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    private void logBlockAction(EnumDefaultPlayer defaultPlayer, BlockState block, EnumAction action) {
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
        obj.add("block", JsonUtil.jsonify(block.getState()));
        obj.add("bucket", JsonUtil.jsonify(new ItemStack(material)));
        logBlockAction(player, block.getState(), EnumAction.BUCKET_EMPTY, obj);
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
        obj.add("block", JsonUtil.jsonify(block.getState()));
        obj.add("bucket", JsonUtil.jsonify(new ItemStack(material)));
        logBlockAction(player, block.getState(), EnumAction.BUCKET_FILL, obj);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event){
        Player player = event.getPlayer();
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();
            if (player.hasMetadata("snitch-inspector")) {
                event.setCancelled(true);
                i.async(() -> {
                    try {
                        SnitchQuery query = new SnitchQuery().relativeTo(new SnitchPosition(block.getLocation()))
                                .inWorld(block.getWorld()).exactPosition();
                        List<SnitchEntry> entries = i.getStorage().performLookup(query);
                        MsgUtil.sendRecords(player, query, entries, 1, 7);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
            }
        } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();

            if (block.getType() == Material.CAKE_BLOCK && EnumAction.CAKE_EAT.isEnabled()) {
                logBlockAction(player, block.getState(), EnumAction.CAKE_EAT);
            }

            if (player.hasMetadata("snitch-inspector")) {
                event.setCancelled(true);
                Block target = block.getRelative(event.getBlockFace());
                i.async(() -> {
                    try {
                        SnitchQuery query = new SnitchQuery().relativeTo(new SnitchPosition(target.getLocation()))
                                .inWorld(target.getWorld()).exactPosition();
                        List<SnitchEntry> entries = i.getStorage().performLookup(query);
                        MsgUtil.sendRecords(player, query, entries, 1, 7);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
            }

            if ((block.getState() instanceof InventoryHolder) && EnumAction.CONTAINER_ACCESS.isEnabled()) {
                logBlockAction(player, block.getState(), EnumAction.CONTAINER_ACCESS);
            } else if (interactables.contains(block.getType()) && EnumAction.BLOCK_USE.isEnabled()) {
                logBlockAction(player, block.getState(), EnumAction.BLOCK_USE);
            }
        } else if (event.getAction() == Action.PHYSICAL){
            Block block = event.getClickedBlock();
            if (block.getType() == Material.SOIL){
                if (EnumAction.CROP_TRAMPLE.isEnabled()){
                    logBlockAction(player, block.getState(), EnumAction.CROP_TRAMPLE);
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
        obj.add("block", JsonUtil.jsonify(block.getState()));
        obj.add("source", JsonUtil.jsonify(source.getState()));
        logBlockAction(EnumDefaultPlayer.BLOCK, block.getState(), EnumAction.BLOCK_SPREAD, obj);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event){
        if (!EnumAction.BLOCK_PLACE.isEnabled())
            return;
        final Player player = event.getPlayer();
        final Block block = event.getBlock();
        if (block.getType() == Material.FIRE) {
            logBlockAction(player, block.getState(), EnumAction.BLOCK_IGNITE);
        } else {
            logBlockAction(player, block.getState(), EnumAction.BLOCK_PLACE);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTreeGrow(StructureGrowEvent event) {
        Player player = event.getPlayer();
        if (player == null) {
            return;
        }
        if (event.getSpecies() != TreeType.BROWN_MUSHROOM && event.getSpecies() != TreeType.RED_MUSHROOM) {
            if (EnumAction.TREE_GROW.isEnabled()) {
                for (BlockState block : event.getBlocks()) {
                    logBlockAction(player, block, EnumAction.TREE_GROW);
                }
            }
        } else if (EnumAction.MUSHROOM_GROW.isEnabled()) {
            for (BlockState block : event.getBlocks()) {
                logBlockAction(player, block, EnumAction.MUSHROOM_GROW);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSignChange(SignChangeEvent event) {
        if (!EnumAction.SIGN_CHANGE.isEnabled())
            return;
        Player player = event.getPlayer();
        Block block = event.getBlock();
        String[] currentLines = ((Sign) block.getState()).getLines();
        String[] changedLines = event.getLines();
        JsonObject obj = new JsonObject();
        obj.add("block", JsonUtil.jsonify(block.getState()));
        JsonObject oldLines = new JsonObject();
        JsonObject newLines = new JsonObject();
        for (int i = 0; i < 4; i++) {
            oldLines.addProperty("line" + i, currentLines[i]);
            newLines.addProperty("line" + i, changedLines[i]);
        }
        obj.add("old", oldLines);
        obj.add("new", newLines);
        logBlockAction(player, block.getState(), EnumAction.SIGN_CHANGE, obj);
    }


    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFromTo(BlockFromToEvent event) {
        BlockState from = event.getBlock().getState();
        BlockState to = event.getToBlock().getState();
        if (from.getType() == Material.STATIONARY_WATER || from.getType() == Material.WATER) {
            if (EnumAction.WATER_FLOW.isEnabled()) {
                logBlockAction(EnumDefaultPlayer.WATER, to, EnumAction.WATER_FLOW);
            }
        } else if (from.getType() == Material.STATIONARY_LAVA || from.getType() == Material.LAVA) {
            if (EnumAction.LAVA_FLOW.isEnabled()) {
                logBlockAction(EnumDefaultPlayer.LAVA, to, EnumAction.LAVA_FLOW);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onForm(BlockFormEvent event){
        if (!EnumAction.BLOCK_FORM.isEnabled())
            return;
        final Block block = event.getNewState().getBlock();
        logBlockAction(EnumDefaultPlayer.BLOCK, block.getState(), EnumAction.BLOCK_FORM);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityChangeBlock(EntityChangeBlockEvent event){
        Entity entity = event.getEntity();
        if ((entity instanceof FallingBlock)){
            Block block = event.getBlock();
            if (EnumAction.BLOCK_FALL.isEnabled()){
                logBlockAction(EnumDefaultPlayer.BLOCK, block.getState(), EnumAction.BLOCK_FALL);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFade(BlockFadeEvent event){
        if (!EnumAction.BLOCK_FADE.isEnabled() && !EnumAction.LEAF_DECAY.isEnabled())
            return;
        final Block block = event.getNewState().getBlock();
        if (block.getType() == Material.LEAVES || block.getType() == Material.LEAVES_2) {
            logBlockAction(EnumDefaultPlayer.BLOCK, block.getState(), EnumAction.LEAF_DECAY);
        } else {
            logBlockAction(EnumDefaultPlayer.BLOCK, block.getState(), EnumAction.BLOCK_FADE);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDispense(BlockDispenseEvent event){
        if (!EnumAction.BLOCK_DISPENSE.isEnabled())
            return;
        Block block = event.getBlock();
        ItemStack itemStack = event.getItem();
        JsonObject obj = new JsonObject();
        obj.add("block", JsonUtil.jsonify(block.getState()));
        obj.add("item", JsonUtil.jsonify(itemStack));
        logBlockAction(EnumDefaultPlayer.BLOCK, block.getState(), EnumAction.BLOCK_DISPENSE, obj);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event){
        if (!EnumAction.BLOCK_BREAK.isEnabled())
            return;
        final Player player = event.getPlayer();
        final Block block = event.getBlock();
        if ((block.getState() instanceof InventoryHolder)) {
            InvUtil.logContentsAsRemoval(player, ((InventoryHolder) block.getState()).getInventory(), block.getLocation());
        }
        logBlockAction(player, block.getState(), EnumAction.BLOCK_BREAK);
        BlockFace[] faces = {BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST, BlockFace.UP};
        for (BlockFace face : faces) {
            Block relative = block.getRelative(face);
            if (relative.getType() == Material.TORCH || relative.getType() == Material.REDSTONE_TORCH_OFF ||
                    relative.getType() == Material.REDSTONE_TORCH_ON ||
                    (relative.getType() == Material.REDSTONE && face == BlockFace.UP) ||
                    (relative.getType() == Material.REDSTONE_COMPARATOR_ON && face == BlockFace.UP) ||
                    (relative.getType() == Material.REDSTONE_COMPARATOR_OFF && face == BlockFace.UP) ||
                    relative.getType() == Material.WALL_SIGN ||
                    (relative.getType() == Material.SIGN_POST && face == BlockFace.UP)) {
                if ((relative.getState() instanceof InventoryHolder)) {
                    InvUtil.logContentsAsRemoval(player, ((InventoryHolder) relative.getState()).getInventory(), relative.getLocation());
                }
                logBlockAction(player, relative.getState(), EnumAction.BLOCK_BREAK);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBurn(BlockBurnEvent event){
        if (!EnumAction.BLOCK_BURN.isEnabled())
            return;
        final Block block = event.getBlock();
        logBlockAction(EnumDefaultPlayer.FIRE, block.getState(), EnumAction.BLOCK_BURN);
    }
}
