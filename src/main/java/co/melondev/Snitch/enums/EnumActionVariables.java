package co.melondev.Snitch.enums;

import co.melondev.Snitch.util.ItemUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.server.v1_12_R1.MojangsonParseException;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Specifies how to internet variables within {@link EnumAction#getExplained()}
 */
public enum EnumActionVariables {


    BLOCK("block", false) {
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
    DYE("dye", false) {
        @Override
        public String getReplacement(JsonObject obj) {
            DyeColor color = DyeColor.valueOf(obj.get("type").getAsString());
            ChatColor chatColor = ChatColor.WHITE;

            switch (color) {
                case RED:
                    chatColor = ChatColor.RED;
                    break;
                case BLUE:
                    chatColor = ChatColor.BLUE;
                    break;
                case CYAN:
                    chatColor = ChatColor.DARK_AQUA;
                    break;
                case GRAY:
                    chatColor = ChatColor.DARK_GRAY;
                    break;
                case LIME:
                    chatColor = ChatColor.GREEN;
                    break;
                case PINK:
                    chatColor = ChatColor.LIGHT_PURPLE;
                    break;
                case BLACK:
                    chatColor = ChatColor.BLACK;
                    break;
                case BROWN:
                    chatColor = ChatColor.DARK_RED;
                    break;
                case GREEN:
                    chatColor = ChatColor.DARK_GREEN;
                    break;
                case WHITE:
                    chatColor = ChatColor.WHITE;
                    break;
                case ORANGE:
                    chatColor = ChatColor.GOLD;
                    break;
                case PURPLE:
                    chatColor = ChatColor.DARK_PURPLE;
                    break;
                case SILVER:
                    chatColor = ChatColor.GRAY;
                    break;
                case YELLOW:
                    chatColor = ChatColor.YELLOW;
                    break;
                case MAGENTA:
                    chatColor = ChatColor.LIGHT_PURPLE;
                    break;
                case LIGHT_BLUE:
                    chatColor = ChatColor.AQUA;
                    break;
            }

            return chatColor + WordUtils.capitalizeFully(color.name().replace("_", " ")) + "§7";
        }
    },
    ENCHANTS("enchants", false) {
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

    SOURCE_BLOCK("source", false) {
        @Override
        public String getReplacement(JsonObject obj) {
            return BLOCK.getReplacement(obj);
        }
    },
    BUCKET("bucket", false) {
        @Override
        public String getReplacement(JsonObject obj) {
            return obj.get("type").getAsString().toLowerCase().replace("_", " ");
        }
    },
    ITEM("item", false) {
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
    ENTITY("entity", false) {
        @Override
        public String getReplacement(JsonObject obj) {
            return obj.get("entityType").getAsString().replace("_", " ").toLowerCase();
        }
    },
    MESSAGE("message", false) {
        @Override
        public String getReplacement(JsonObject obj) {
            return obj.get("message").getAsString();
        }
    },
    CAUSE("cause", false) {
        @Override
        public String getReplacement(JsonObject obj) {
            return obj.get("cause").getAsString().replace("_", " ").toLowerCase();
        }
    },
    LOCATION("location", false) {
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
    IP("ip", true) {
        @Override
        public String getReplacement(JsonObject obj) {
            return obj.get("ip").getAsString();
        }
    },
    SPAWNEGG("spawnegg", false) {
        @Override
        public String getReplacement(JsonObject obj) {
            return obj.get("type").getAsString();
        }
    },
    VEHICLE("vehicle", false) {
        @Override
        public String getReplacement(JsonObject obj) {
            return obj.get("type").getAsString();
        }
    },
    XP("xp", false) {
        @Override
        public String getReplacement(JsonObject obj) {
            return obj.get("amount").getAsInt() + "";
        }
    },
    SLOT("slot", false) {
        @Override
        public String getReplacement(JsonObject obj) {
            return obj.get("slot").getAsString().toLowerCase();
        }
    },
    OLDSIGNTEXT("old", false) {
        @Override
        public String getReplacement(JsonObject obj) {
            List<String> lines = new ArrayList<>();
            JsonObject old = obj.getAsJsonObject("old");
            for (int i = 0; i < 4; i++) {
                lines.add(old.get("line" + i).getAsString());
            }
            return String.join(", ", lines);
        }
    },
    NEWSIGNTEXT("new", false) {
        @Override
        public String getReplacement(JsonObject obj) {
            List<String> lines = new ArrayList<>();
            JsonObject old = obj.getAsJsonObject("new");
            for (int i = 0; i < 4; i++) {
                lines.add(old.get("line" + i).getAsString());
            }
            return String.join(", ", lines);
        }
    };

    /**
     * The key to replace in {@link EnumAction#getExplained()}
     */
    private String key;
    private boolean requirePermission;

    EnumActionVariables(String key, boolean requirePermission) {
        this.key = key;
        this.requirePermission = requirePermission;
    }

    public boolean shouldRedactDetailsFor(CommandSender sender) {
        return isRequirePermission() && !sender.hasPermission("snitch.viewdata." + getKey().toLowerCase());
    }

    public boolean isRequirePermission() {
        return requirePermission;
    }

    /**
     * Returns the proper replacement for a variable
     *
     * @param obj the metadata from an event
     * @return the replacement
     */
    public abstract String getReplacement(JsonObject obj);

    public String getKey() {
        return key;
    }
}
