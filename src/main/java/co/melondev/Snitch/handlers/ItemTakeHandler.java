package co.melondev.Snitch.handlers;

import co.melondev.Snitch.entities.SnitchEntry;
import co.melondev.Snitch.entities.SnitchProcessHandler;
import co.melondev.Snitch.entities.SnitchSession;
import co.melondev.Snitch.enums.EnumSnitchActivity;
import co.melondev.Snitch.util.ItemUtil;
import com.google.gson.JsonObject;
import net.minecraft.server.v1_12_R1.MojangsonParseException;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Devon on 7/17/18.
 */
public class ItemTakeHandler implements SnitchProcessHandler {
    @Override
    public boolean handleRollback(SnitchSession session, SnitchEntry entry) {

        Location loc = entry.getSnitchPosition().toLocation(entry.getSnitchWorld());
        Block block = loc.getBlock();
        if ((block.getState() instanceof InventoryHolder)) {
            Inventory inv = ((InventoryHolder) block.getState()).getInventory();
            JsonObject obj = entry.getData();
            int slot = obj.get("slot").getAsInt();
            try {
                ItemStack itemStack = ItemUtil.JSONtoItemStack(obj.get("item").getAsJsonObject().get("raw").getAsString());
                inv.setItem(slot, itemStack);
            } catch (MojangsonParseException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    @Override
    public boolean handlePreview(SnitchSession session, SnitchEntry entry) {
        return false;
    }

    @Override
    public boolean handleRestore(SnitchSession session, SnitchEntry entry) {
        Location loc = entry.getSnitchPosition().toLocation(entry.getSnitchWorld());
        Block block = loc.getBlock();
        if ((block.getState() instanceof InventoryHolder)) {
            Inventory inv = ((InventoryHolder) block.getState()).getInventory();
            JsonObject obj = entry.getData();
            int slot = obj.get("slot").getAsInt();
            inv.setItem(slot, null);
        }

        return true;
    }

    @Override
    public boolean can(EnumSnitchActivity activity) {
        return activity == EnumSnitchActivity.ROLLBACK || activity == EnumSnitchActivity.RESTORE;
    }
}
