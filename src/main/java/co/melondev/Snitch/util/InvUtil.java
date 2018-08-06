package co.melondev.Snitch.util;

import co.melondev.Snitch.enums.EnumAction;
import co.melondev.Snitch.listeners.InventoryListener;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.server.v1_12_R1.MojangsonParseException;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Devon on 7/16/18.
 */
public class InvUtil {

    /**
     * Logs all items within a container as a removal
     *
     * @param player    the player who "removed" them
     * @param inventory the inventory to loop through
     * @param location  the location of this inventory
     */
    public static void logContentsAsRemoval(Player player, Inventory inventory, Location location) {
        if (!EnumAction.ITEM_TAKE.isEnabled()) return;
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack itemStack = inventory.getItem(i);
            if (itemStack != null) {
                InventoryListener.logAction(player, location, itemStack.clone(), EnumAction.ITEM_TAKE, i, null);
            }
        }
    }

    /**
     * Syncs an inventory to match the betadata
     * @param inventory     the inventory to sync
     * @param invData       the metadata of the inventory
     */
    public static void syncInventory(Inventory inventory, JsonObject invData) {
        for (int i = 0; i < inventory.getSize(); i++) {
            if (invData.has("slot" + i)) {
                JsonElement element = invData.get("slot" + i);
                if (element.isJsonNull()) {
                    inventory.setItem(i, null);
                } else {
                    try {
                        ItemStack itemStack = ItemUtil.JSONtoItemStack(element.getAsString());
                        inventory.setItem(i, itemStack);
                    } catch (MojangsonParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
