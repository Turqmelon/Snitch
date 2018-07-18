package co.melondev.Snitch.enums;

import co.melondev.Snitch.SnitchPlugin;
import co.melondev.Snitch.entities.SnitchProcessHandler;
import co.melondev.Snitch.handlers.*;
import org.apache.commons.lang.WordUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum EnumAction {

    BLOCK_BURN(0, "burn", "%actor burned %block", new BlockDestructionHandler()),
    BLOCK_BREAK(1, "break", "%actor broke %block", new BlockDestructionHandler()),
    BLOCK_DISPENSE(2, "dispense", "%block dispensed %item", new NoCapabilityHandler()),
    BLOCK_FADE(3, "fade", "%block disappeared", new BlockDestructionHandler()),
    BLOCK_FALL(4, "fall", "%block fell", new BlockDestructionHandler()),
    BLOCK_FORM(5, "form", "%block formed", new BlockCreationHandler()),
    BLOCK_PLACE(6, "place", "%actor placed %block", new BlockCreationHandler()),
    BLOCK_SHIFT(7, "shift", "%block shifted", new NoCapabilityHandler()), // TODO
    BLOCK_SPREAD(8, "spread", "%source spread to %block", new BlockSpreadHandler()),
    BLOCK_USE(9, "use", "%actor used %block", new NoCapabilityHandler()),
    BLOCK_EXPLODE(16, "explode", "%actor blew up %block", new BlockDestructionHandler()),
    BONEMEAL_USE(10, "bonemeal", "%actor used bonemeal on %block", new NoCapabilityHandler()), // TODO
    BUCKET_FILL(11, "fill", "%actor filled a %bucket bucket", new BlockDestructionHandler()),
    BUCKET_EMPTY(12, "empty", "%actor emptied a %bucket bucket", new BlockCreationHandler()),
    CAKE_EAT(13, "eat", "%actor ate cake", new NoCapabilityHandler()), // TODO
    CONTAINER_ACCESS(14, "access", "%actor accessed %block", new NoCapabilityHandler()), // TODO
    CRAFT_ITEM(15, "craft", "%actor crafted %item", new NoCapabilityHandler()),
    CROP_TRAMPLE(17, "trample", "%actor trampled %block", new BlockDestructionHandler()),
    ENCHANT_ITEM(18, "enchant", "%actor enchanted %item with %enchants", new NoCapabilityHandler()),
    ENTITY_BREAK(19, "break", "%actor broke %block", new EntityDeathHandler()),
    ENTITY_DYE(20, "dye", "%actor dyed %entity %dye", new NoCapabilityHandler()),
    ENTITY_EXPLODE(21, "explode", "%actor exploded", new NoCapabilityHandler()),
    ENTITY_FOLLOW(22, "lure", "%actor lured %entity", new NoCapabilityHandler()),
    ENTITY_FORMED(23, "form", "%actor formed", new NoCapabilityHandler()),
    ENTITY_KILL(24, "kill", "%actor killed %entity", new EntityDeathHandler()),
    ENTITY_LEASH(25, "leash", "%actor leashed %entity", new NoCapabilityHandler()),
    ENTITY_SHEAR(26, "shear", "%actor sheared %entity", new NoCapabilityHandler()),
    ENTITY_SPAWN(27, "spawn", "%entity spawned from %cause", new NoCapabilityHandler()),
    ENTITY_UNLEASH(28, "unleash", "%actor unleashed %entity", new NoCapabilityHandler()),
    ARMORSTAND_CREATE(64, "place", "%actor placed an armor stand", new NoCapabilityHandler()),
    ARMORSTAND_BREAK(63, "break", "%actor broke an armor stand", new EntityDeathHandler()),
    ARMORSTAND_EDIT(65, "edit", "%actor changed armor stand's %slot", new NoCapabilityHandler()),
    FIRE_SPREAD(30, "fire", "fire spread to %block", new BlockSpreadHandler()),
    FIREWORK_LAUNCH(31, "firework", "%actor launched firework", new NoCapabilityHandler()),
    HANGING_PLACE(32, "hang", "%actor hung art", new NoCapabilityHandler()),
    HANGING_BREAK(33, "unhang", "%actor knocked down art", new EntityDeathHandler()),
    ITEM_DROP(34, "drop", "%actor dropped %item", new NoCapabilityHandler()),
    ITEM_INSERT(35, "insert", "%actor inserted %item", new ItemInsertHandler()),
    ITEM_PICKUP(36, "pickup", "%actor picked up %item", new NoCapabilityHandler()),
    ITEM_TAKE(37, "take", "%actor took %item", new ItemTakeHandler()),
    ITEM_ROTATE(38, "rotate", "%actor rotated %item", new NoCapabilityHandler()),
    LAVA_FLOW(39, "flow", "lava flowed", new BlockCreationHandler()),
    BLOCK_IGNITE(40, "ignite", "%actor ignited %block", new BlockCreationHandler()),
    LEAF_DECAY(41, "decay", "leaf decayed", new BlockDestructionHandler()),
    LIGHTNING(42, "lightning", "lightning struck", new NoCapabilityHandler()),
    MUSHROOM_GROW(43, "grow", "%actor grew large mushroom", new NoCapabilityHandler()),
    PLAYER_CHAT(44, "chat", "%actor said: %message", new NoCapabilityHandler()),
    PLAYER_COMMAND(45, "command", "%actor executed: %message", new NoCapabilityHandler()),
    PLAYER_DEATH(46, "death", "%actor died", new NoCapabilityHandler()),
    PLAYER_JOIN(47, "join", "%actor joined from %ip", new NoCapabilityHandler()),
    PLAYER_QUIT(48, "quit", "%actor left", new NoCapabilityHandler()),
    PLAYER_TELEPORT(49, "teleport", "%actor teleported to %location via %cause", new NoCapabilityHandler()),
    POTION_SPLASH(50, "splash", "%actor threw potion", new NoCapabilityHandler()),
    SHEEP_EAT(51, "sheep", "sheep ate %block", new NoCapabilityHandler()),
    SIGN_CHANGE(52, "sign", "%actor changed sign: %old > %new", new SignChangeHandler()),
    SPAWNEGG_USE(53, "spawnegg", "%actor used %spawnegg egg", new NoCapabilityHandler()),
    TNT_PRIME(54, "tnt", "%actor primed TNT", new NoCapabilityHandler()),
    TREE_GROW(55, "grow", "%actor grew tree", new BlockCreationHandler()),
    VEHICLE_BREAK(56, "break", "%actor broke a %vehicle", new EntityDeathHandler()),
    VEHICLE_ENTER(57, "enter", "%actor entered a %vehicle", new NoCapabilityHandler()),
    VEHICLE_EXIT(58, "exit", "%actor left a %vehicle", new NoCapabilityHandler()),
    VEHICLE_PLACE(59, "place", "%actor placed a %vehicle", new NoCapabilityHandler()),
    WATER_FLOW(60, "flow", "water flowed", new BlockCreationHandler()),
    WORLD_EDIT(61, "we", "%actor used worldedit", new NoCapabilityHandler()),
    XP_PICKUP(62, "xp", "%actor picked up %xp XP", new NoCapabilityHandler());

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
        return !SnitchPlugin.getInstance().getConfiguration().getDisabledLogging().contains(this);
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
