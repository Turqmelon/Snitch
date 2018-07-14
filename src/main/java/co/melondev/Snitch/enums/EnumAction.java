package co.melondev.Snitch.enums;

import co.melondev.Snitch.entities.SnitchProcessHandler;
import co.melondev.Snitch.handlers.BlockCreationHandler;
import co.melondev.Snitch.handlers.BlockDestructionHandler;
import co.melondev.Snitch.handlers.BlockSpreadHandler;
import co.melondev.Snitch.handlers.NoCapabilityHandler;
import org.apache.commons.lang.WordUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum EnumAction {

    BLOCK_BURN(0, "burn", "%actor burned %block", new BlockDestructionHandler()),
    BLOCK_BREAK(1, "break", "%actor broke %block", new BlockDestructionHandler()),
    BLOCK_DISPENSE(2, "dispense", "%block dispensed %item", new NoCapabilityHandler()),
    BLOCK_FADE(3, "fade", "%block faded", new BlockDestructionHandler()),
    BLOCK_FALL(4, "fall", "%block fell", new BlockDestructionHandler()),
    BLOCK_FORM(5, "form", "%block formed", new BlockCreationHandler()),
    BLOCK_PLACE(6, "place", "%actor placed %block", new BlockCreationHandler()),
    BLOCK_SHIFT(7, "shift", "%block shifted", new NoCapabilityHandler()), // TODO
    BLOCK_SPREAD(8, "spread", "%source spread to %block", new BlockSpreadHandler()),
    BLOCK_USE(9, "use", "%actor used %block", new NoCapabilityHandler()),
    BLOCK_EXPLODE(16, "explode", "%actor blew up $block", new BlockDestructionHandler()),
    BONEMEAL_USE(10, "bonemeal", "%actor used bonemeal on %block", new NoCapabilityHandler()), // TODO
    BUCKET_FILL(11, "fill", "%actor filled a %bucket bucket", new BlockDestructionHandler()),
    BUCKET_EMPTY(12, "empty", "%actor emptied a %bucket bucket", new BlockCreationHandler()),
    CAKE_EAT(13, "eat", "%actor ate cake", new NoCapabilityHandler()), // TODO
    CONTAINER_ACCESS(14, "access", "%actor accessed %block", new NoCapabilityHandler()), // TODO
    CRAFT_ITEM(15, "craft", "%actor crafted %item", new NoCapabilityHandler()),
    CROP_TRAMPLE(17, "trample", "%actor trampled %block", new BlockDestructionHandler()),
    ENCHANT_ITEM(18, "enchant", "%actor enchanted %item with %enchants", new NoCapabilityHandler()),
    ENTITY_BREAK(19, "break", "%actor broke %block", processHandler),
    ENTITY_DYE(20, "dye", "%actor dyed %entity %dye", processHandler),
    ENTITY_EXPLODE(21, "explode", "%actor exploded", processHandler),
    ENTITY_FOLLOW(22, "lure", "%actor lured %entity", processHandler),
    ENTITY_FORMED(23, "form", "%actor formed", processHandler),
    ENTITY_KILL(24, "kill", "%actor killed %entity", processHandler),
    ENTITY_LEASH(25, "leash", "%actor leashed %entity", processHandler),
    ENTITY_SHEAR(26, "shear", "%actor sheared %entity", processHandler),
    ENTITY_SPAWN(27, "spawn", "%entity spawned from %cause", processHandler),
    ENTITY_UNLEASH(28, "unleash", "%actor unleashed %entity", processHandler),
    FIRE_SPREAD(30, "fire", "fire spread to %block", processHandler),
    FIREWORK_LAUNCH(31, "firework", "%actor launched firework", processHandler),
    HANGING_PLACE(32, "hang", "%actor hung art", processHandler),
    HANGING_BREAK(33, "unhang", "%actor knocked down art", processHandler),
    ITEM_DROP(34, "drop", "%actor dropped %item", processHandler),
    ITEM_INSERT(35, "insert", "%actor inserted %item", processHandler),
    ITEM_PICKUP(36, "pickup", "%actor picked up %item", processHandler),
    ITEM_TAKE(37, "take", "%actor took %item", processHandler),
    ITEM_ROTATE(38, "rotate", "%actor rotated %item", processHandler),
    LAVA_FLOW(39, "flow", "lava flowed", processHandler),
    BLOCK_IGNITE(40, "ignite", "%actor ignited %block", processHandler),
    LEAF_DECAY(41, "decay", "leaf decayed", processHandler),
    LIGHTNING(42, "lightning", "lightning struck", processHandler),
    MUSHROOM_GROW(43, "grow", "%actor grew large mushroom", processHandler),
    PLAYER_CHAT(44, "chat", "%actor said: %message", processHandler),
    PLAYER_COMMAND(45, "command", "%actor executed: %message", processHandler),
    PLAYER_DEATH(46, "death", "%actor died", processHandler),
    PLAYER_JOIN(47, "join", "%actor joined from %ip", processHandler),
    PLAYER_QUIT(48, "quit", "%actor left", processHandler),
    PLAYER_TELEPORT(49, "teleport", "%actor teleported to %location", processHandler),
    POTION_SPLASH(50, "splash", "%potion splashed", processHandler),
    SHEEP_EAT(51, "sheep", "sheep ate %block", processHandler),
    SIGN_CHANGE(52, "sign", "%actor changed sign: %message", processHandler),
    SPAWNEGG_USE(53, "spawnegg", "%actor used %spawnegg egg", processHandler),
    TNT_PRIME(54, "tnt", "%actor primed TNT", processHandler),
    TREE_GROW(55, "grow", "%actor grew tree", processHandler),
    VEHICLE_BREAK(56, "break", "%actor broke a %vehicle", processHandler),
    VEHICLE_ENTER(57, "enter", "%actor entered a %vehicle", processHandler),
    VEHICLE_EXIT(58, "exit", "%actor left a %vehicle", processHandler),
    VEHICLE_PLACE(59, "place", "%actor placed a %vehicle", processHandler),
    WATER_FLOW(60, "flow", "water flowed", processHandler),
    WORLD_EDIT(61, "we", "%actor used worldedit", processHandler),
    XP_PICKUP(62, "xp", "%actor picked up %xp XP", processHandler);

    private static Map<Integer, EnumAction> actionMap = new HashMap<>();
    private int id;
    private String name;
    private String explained;
    private SnitchProcessHandler processHandler;

    EnumAction(int id, String name, String explained, SnitchProcessHandler processHandler) {
        this.id = id;
        this.name = name;
        this.explained = explained;
        this.processHandler = processHandler;
    }

    public static List<EnumAction> getByName(String name) {
        List<EnumAction> results = new ArrayList<>();
        for (EnumAction action : EnumAction.values()) {
            if (action.name().equalsIgnoreCase(name) || action.getName().equalsIgnoreCase(name)) {
                results.add(action);
            }
        }
        return results;
    }

    public static EnumAction getById(int action_id) {
        if (actionMap.containsKey(action_id)) {
            return actionMap.get(action_id);
        }
        for (EnumAction action : EnumAction.values()) {
            if (action.getId() == action_id) {
                actionMap.put(action.getId(), action);
                return action;
            }
        }
        return null;
    }

    public SnitchProcessHandler getProcessHandler() {
        return processHandler;
    }

    public String getFriendlyFullName() {
        return WordUtils.capitalizeFully(name().replace("_", " "));
    }

    public String getNode(){
        return "snitch.action." + name().toLowerCase().replace("_", "");
    }

    public boolean isEnabled(){
        // TODO config check
        return true;
    }

    public String getExplained() {
        return explained;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
