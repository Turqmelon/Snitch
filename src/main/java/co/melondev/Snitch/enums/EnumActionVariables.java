package co.melondev.Snitch.enums;

import co.melondev.Snitch.util.ItemUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.server.v1_12_R1.MojangsonParseException;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public enum EnumActionVariables {


    BLOCK("block"){
        @Override
        public String getReplacement(JsonObject obj) {
            String type = obj.get("type").getAsString();
            byte data = obj.get("data").getAsByte();

            try {
                Material m = Material.valueOf(type.toUpperCase());
                return ItemUtil.getItemName(new ItemStack(m, 1, data));
            }catch (Exception ex){
                return type;
            }
        }
    },
    DYE("dye") {
        @Override
        public String getReplacement(JsonObject obj) {
            return obj.get("type").getAsString();
        }
    },
    ENCHANTS("enchants") {
        @Override
        public String getReplacement(JsonObject obj) {
            List<String> names = new ArrayList<>();
            for(Map.Entry<String, JsonElement> entry : obj.entrySet()){
                String enchantmentName = entry.getKey();
                int level = entry.getValue().getAsInt();
                Enchantment enchantment = Enchantment.getByName(enchantmentName);
                names.add(ItemUtil.getEnchantmentName(enchantment) + " " + level);
            }
            return names.isEmpty() ? "nothing" : String.join(", ", names);
        }
    },

    SOURCE_BLOCK("source"){
        @Override
        public String getReplacement(JsonObject obj) {
            return BLOCK.getReplacement(obj);
        }
    },
    BUCKET("bucket"){
        @Override
        public String getReplacement(JsonObject obj) {
            return obj.get("type").getAsString();
        }
    },
    ITEM("item") {
        @Override
        public String getReplacement(JsonObject obj) {
            try {
                ItemStack itemStack = ItemUtil.JSONtoItemStack(obj.get("raw").getAsString());
                return itemStack.getAmount() + "x " + ItemUtil.getItemName(itemStack);
            } catch (MojangsonParseException e) {
                e.printStackTrace();
                return "???";
            }
        }
    },
    ENTITY("entity") {
        @Override
        public String getReplacement(JsonObject obj) {
            return obj.get("entityType").getAsString();
        }
    },
    MESSAGE("message") {
        @Override
        public String getReplacement(JsonObject obj) {
            return obj.get("message").getAsString();
        }
    },
    IP("ip"){
        @Override
        public String getReplacement(JsonObject obj) {
            return obj.get("ip").getAsString();
        }
    },
    LOCATION("location") {
        @Override
        public String getReplacement(JsonObject obj) {
            String world = obj.get("world").getAsString();
            double x = obj.get("x").getAsDouble();
            double y = obj.get("y").getAsDouble();
            double z = obj.get("z").getAsDouble();
            DecimalFormat df = new DecimalFormat("#");
            return df.format(x) + "x " + df.format(y) + "y " + df.format(z) + "z in " + world;
        }
    },
    SPAWNEGG("spawnegg"){
        @Override
        public String getReplacement(JsonObject obj) {
            return obj.get("type").getAsString();
        }
    },
    VEHICLE("vehicle"){
        @Override
        public String getReplacement(JsonObject obj) {
            return obj.get("type").getAsString();
        }
    },
    XP("xp"){
        @Override
        public String getReplacement(JsonObject obj) {
            return obj.get("amount").getAsInt() + "";
        }
    };

    private String key;

    EnumActionVariables(String key) {
        this.key = key;
    }

    public abstract String getReplacement(JsonObject obj);

    public String getKey() {
        return key;
    }
}
