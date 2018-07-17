package co.melondev.Snitch.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.server.v1_12_R1.MojangsonParseException;
import org.apache.commons.lang.Validate;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.EntityType;
import org.bukkit.material.Colorable;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Devon on 7/16/18.
 */
public class BlockUtil {

    public static void rebuildBlock(Block block, JsonObject blockData) throws MojangsonParseException {
        block.setTypeIdAndData(Material.valueOf(blockData.get("type").getAsString()).getId(),
                blockData.get("data").getAsByte(), false);
        BlockState bs = block.getState();
        bs.update();
        bs = block.getState();

        if ((bs instanceof Banner)) {
            ((Banner) bs).setBaseColor(DyeColor.valueOf(blockData.get("baseColor").getAsString()));
            JsonArray patterns = blockData.getAsJsonArray("patterns");
            for (JsonElement element : patterns) {
                JsonObject patternData = element.getAsJsonObject();
                DyeColor color = DyeColor.valueOf(patternData.get("color").getAsString());
                PatternType type = PatternType.valueOf(patternData.get("type").getAsString());
                ((Banner) bs).addPattern(new Pattern(color, type));
            }
        }
        if ((bs instanceof Beacon)) {
            JsonElement primary = blockData.get("primaryEffect");
            JsonElement secondary = blockData.get("secondaryEffect");
            if (!primary.isJsonNull()) {
                ((Beacon) bs).setPrimaryEffect(PotionEffectType.getByName(primary.getAsString()));
            }
            if (!secondary.isJsonNull()) {
                ((Beacon) bs).setSecondaryEffect(PotionEffectType.getByName(secondary.getAsString()));
            }
        }
        if ((bs instanceof Colorable)) {
            ((Colorable) bs).setColor(DyeColor.valueOf(blockData.get("color").getAsString()));
        }
        if ((bs instanceof BrewingStand)) {
            ((BrewingStand) bs).setBrewingTime(blockData.get("brewingTime").getAsInt());
            ((BrewingStand) bs).setFuelLevel(blockData.get("fuelLevel").getAsInt());
            InvUtil.syncInventory(((BrewingStand) bs).getInventory(), blockData.getAsJsonObject("inventory"));
        }
        if ((bs instanceof Container)) {
            InvUtil.syncInventory(((Container) bs).getInventory(), blockData.getAsJsonObject("inventory"));
        }
        if ((bs instanceof Lockable)) {
            Lockable l = (Lockable) bs;
            JsonElement lock = blockData.get("locked");
            if (!lock.isJsonNull()) {
                l.setLock(lock.getAsString());
            }
        }
        if ((bs instanceof CommandBlock)) {
            CommandBlock cb = (CommandBlock) bs;
            cb.setName(blockData.get("name").getAsString());
            cb.setCommand(blockData.get("command").getAsString());
        }
        if ((bs instanceof Chest)) {
            InvUtil.syncInventory(((Chest) bs).getBlockInventory(), blockData.getAsJsonObject("inventory"));
        }
        if ((bs instanceof CreatureSpawner)) {
            CreatureSpawner cs = (CreatureSpawner) bs;
            cs.setSpawnRange(blockData.get("spawnRange").getAsInt());
            cs.setSpawnedType(EntityType.valueOf(blockData.get("spawnType").getAsString()));
            cs.setSpawnCount(blockData.get("spawnCount").getAsInt());
            cs.setRequiredPlayerRange(blockData.get("requiredPlayers").getAsInt());
            cs.setMinSpawnDelay(blockData.get("minDelay").getAsInt());
            cs.setMaxSpawnDelay(blockData.get("maxDelay").getAsInt());
            cs.setMaxNearbyEntities(blockData.get("maxNearby").getAsInt());
            cs.setDelay(blockData.get("delay").getAsInt());
        }
        if ((bs instanceof Nameable)) {
            Nameable n = (Nameable) bs;
            JsonElement name = blockData.get("customName");
            if (!name.isJsonNull()) {
                n.setCustomName(name.getAsString());
            }
        }
        if ((bs instanceof EndGateway)) {
            ((EndGateway) bs).setExactTeleport(blockData.get("exactTeleport").getAsBoolean());
            ((EndGateway) bs).setExitLocation(JsonUtil.fromJson(blockData.getAsJsonObject("exitLocation")));
        }
        if ((bs instanceof FlowerPot)) {
            JsonElement content = blockData.get("contents");
            if (!content.isJsonNull()) {
                org.bukkit.inventory.ItemStack itemStack = ItemUtil.JSONtoItemStack(content.getAsString());
                ((FlowerPot) bs).setContents(itemStack.getData());
            }
        }
        if ((bs instanceof Furnace)) {
            ((Furnace) bs).setCookTime(blockData.get("cookTime").getAsShort());
            ((Furnace) bs).setBurnTime(blockData.get("burnTime").getAsShort());
            InvUtil.syncInventory(((Furnace) bs).getInventory(), blockData.getAsJsonObject("inventory"));
        }
        if ((bs instanceof NoteBlock)) {
            ((NoteBlock) bs).setRawNote(blockData.get("note").getAsByte());
        }
        if ((bs instanceof Sign)) {
            JsonArray signText = blockData.getAsJsonArray("text");
            int line = 0;
            for (JsonElement element : signText) {
                ((Sign) bs).setLine(line++, element.getAsString());
            }
        }
        if ((bs instanceof Skull)) {
            ((Skull) bs).setSkullType(SkullType.valueOf(blockData.get("skullType").getAsString()));
            ((Skull) bs).setRotation(BlockFace.valueOf(blockData.get("rotation").getAsString()));
            JsonElement owner = blockData.get("owningPlayer");
            if (!owner.isJsonNull()) {
                UUID uuid = UUID.fromString(owner.getAsString());
                ((Skull) bs).setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
            }
        }

        bs.update();
    }

    public static List<AdjustedBlock> removeNear(List<Material> materialList, Location location, int range) {
        List<AdjustedBlock> changes = new ArrayList<>();
        Validate.notNull(location, "Location can't be null.");
        Validate.isTrue(range > 0, "Range must be bigger than 0.");
        Validate.notEmpty(materialList, "Material list can't be empty.");

        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        World world = location.getWorld();

        for (int xx = x - range; xx <= x + range; xx++) {
            for (int yy = y - range; yy <= y + range; yy++) {
                for (int zz = z - range; zz <= z + range; zz++) {
                    Location l = new Location(world, xx, yy, zz);
                    if (l.getBlock().getType() == Material.AIR)
                        continue;
                    if (materialList.contains(l.getBlock().getType())) {
                        final BlockState old = location.getBlock().getState();
                        l.getBlock().setType(Material.AIR);
                        final BlockState newState = location.getBlock().getState();
                        changes.add(new AdjustedBlock(old, newState));
                    }
                }
            }
        }
        return changes;
    }

}
