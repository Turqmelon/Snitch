package co.melondev.Snitch.enums;

public enum EnumAction {

    BLOCK_BURN(0, "burn", "%actor burned %block"),
    BLOCK_BREAK(1, "break", "%actor broke %block"),
    BLOCK_DISPENSE(2, "dispense", "%block dispensed %item"),
    BLOCK_FADE(3, "fade", "%block faded"),
    BLOCK_FALL(4, "fall", "%block fell"),
    BLOCK_FORM(5, "form", "%block formed"),
    BLOCK_PLACE(6, "place", "%actor placed %block"),
    BLOCK_SHIFT(7, "shift", "%block shifted"),
    BLOCK_SPREAD(8, "spread", "%source spread to %block"),
    BLOCK_USE(9, "use", "%actor used %block"),
    BLOCK_EXPLODE(16, "explode", "%actor blew up $block"),
    BONEMEAL_USE(10, "bonemeal", "%actor used bonemeal on %block"),
    BUCKET_FILL(11, "fill", "%actor filled a %bucket bucket"),
    BUCKET_EMPTY(12, "empty", "%actor emptied a %bucket bucket"),
    CAKE_EAT(13, "eat", "%actor ate cake"),
    CONTAINER_ACCESS(14, "access", "%actor accessed %block"),
    CRAFT_ITEM(15, "craft", "%actor crafted %item"),
    CROP_TRAMPLE(17, "trample", "%actor trampled %block"),
    ENCHANT_ITEM(18, "enchant", "%actor enchanted %item with %enchants"),
    ENTITY_BREAK(19, "break", "%actor broke %block"),
    ENTITY_DYE(20, "dye", "%actor dyed %entity %dye"),
    ENTITY_EXPLODE(21, "explode", "%actor exploded"),
    ENTITY_FOLLOW(22, "lure", "%actor lured %entity"),
    ENTITY_FORMED(23, "form", "%actor formed"),
    ENTITY_KILL(24, "kill", "%actor killed %entity"),
    ENTITY_LEASH(25, "leash", "%actor leashed %entity"),
    ENTITY_SHEAR(26, "shear", "%actor sheared %entity"),
    ENTITY_SPAWN(27, "spawn", "%entity spawned from %cause"),
    ENTITY_UNLEASH(28, "unleash", "%actor unleashed %entity"),
    FIRE_SPREAD(30, "fire", "fire spread to %block"),
    FIREWORK_LAUNCH(31, "firework", "%actor launched firework"),
    HANGING_PLACE(32, "hang", "%actor hung art"),
    HANGING_BREAK(33, "unhang", "%actor knocked down art"),
    ITEM_DROP(34, "drop", "%actor dropped %item"),
    ITEM_INSERT(35, "insert", "%actor inserted %item"),
    ITEM_PICKUP(36, "pickup", "%actor picked up %item"),
    ITEM_TAKE(37, "take", "%actor took %item"),
    ITEM_ROTATE(38, "rotate", "%actor rotated %item"),
    LAVA_FLOW(39, "flow", "lava flowed"),
    BLOCK_IGNITE(40, "ignite", "%actor ignited %block"),
    LEAF_DECAY(41, "decay", "leaf decayed"),
    LIGHTNING(42, "lightning", "lightning struck"),
    MUSHROOM_GROW(43, "grow", "%actor grew large mushroom"),
    PLAYER_CHAT(44, "chat", "%actor said: %message"),
    PLAYER_COMMAND(45, "command", "%actor executed: %message"),
    PLAYER_DEATH(46, "death", "%actor died"),
    PLAYER_JOIN(47, "join", "%actor joined from %ip"),
    PLAYER_QUIT(48, "quit", "%actor left"),
    PLAYER_TELEPORT(49, "teleport", "%actor teleported to %location"),
    POTION_SPLASH(50, "splash", "%potion splashed"),
    SHEEP_EAT(51, "sheep", "sheep ate %block"),
    SIGN_CHANGE(52, "sign", "%actor changed sign: %message"),
    SPAWNEGG_USE(53, "spawnegg", "%actor used %spawnegg egg"),
    TNT_PRIME(54, "tnt", "%actor primed TNT"),
    TREE_GROW(55, "grow", "%actor grew tree"),
    VEHICLE_BREAK(56, "break", "%actor broke a %vehicle"),
    VEHICLE_ENTER(57, "enter", "%actor entered a %vehicle"),
    VEHICLE_EXIT(58, "exit", "%actor left a %vehicle"),
    VEHICLE_PLACE(59, "place", "%actor placed a %vehicle"),
    WATER_FLOW(60, "flow", "water flowed"),
    WORLD_EDIT(61, "we", "%actor used worldedit"),
    XP_PICKUP(62, "xp", "%actor picked up %xp XP");

    private int id;
    private String name;
    private String explained;

    EnumAction(int id, String name, String explained) {
        this.id = id;
        this.name = name;
        this.explained = explained;
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
