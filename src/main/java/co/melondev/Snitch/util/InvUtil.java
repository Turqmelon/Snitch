package co.melondev.Snitch.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.server.v1_12_R1.MojangsonParseException;
import org.bukkit.inventory.Inventory;

/**
 * Created by Devon on 7/16/18.
 */
public class InvUtil {

    public static void syncInventory(Inventory inventory, JsonObject invData) {
        for (int i = 0; i < inventory.getSize(); i++) {
            if (invData.has("slot" + i)) {
                JsonElement element = invData.get("slot" + i);
                if (element.isJsonNull()) {
                    inventory.setItem(i, null);
                } else {
                    try {
                        inventory.setItem(i, ItemUtil.JSONtoItemStack(element.getAsString()));
                    } catch (MojangsonParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
