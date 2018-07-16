package co.melondev.Snitch.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.block.banner.Pattern;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.entity.minecart.CommandMinecart;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Colorable;
import org.bukkit.material.MaterialData;
import org.bukkit.util.EulerAngle;

import java.util.Map;

public class JsonUtil {

    public static JsonObject jsonify(DyeColor dye){
        JsonObject obj = new JsonObject();
        obj.addProperty("type", dye.name());
        return obj;
    }

    public static JsonObject jsonify(Entity entity){

        JsonObject obj = new JsonObject();
        obj.addProperty("entityType", entity.getType().name());
        obj.addProperty("fire", entity.getFireTicks());
        obj.addProperty("nameVisible", entity.isCustomNameVisible());
        obj.addProperty("customName", entity.getCustomName());
        obj.addProperty("fallDistance", entity.getFallDistance());
        obj.addProperty("glowing", entity.isGlowing());
        obj.addProperty("gravity", entity.hasGravity());
        obj.addProperty("invulnerable", entity.isInvulnerable());
        obj.addProperty("portalCooldown", entity.getPortalCooldown());
        obj.addProperty("silent", entity.isSilent());
        obj.addProperty("ticksLived", entity.getTicksLived());
        obj.addProperty("velocity", entity.getVelocity().toString());

        if ((entity instanceof LivingEntity)){
            obj.addProperty("living", true);
            LivingEntity le = (LivingEntity) entity;
            obj.addProperty("ai", le.hasAI());
            obj.addProperty("pickupItems", le.getCanPickupItems());
            obj.addProperty("collidable", le.isCollidable());
            obj.addProperty("gliding", le.isGliding());
            obj.addProperty("lastDamage", le.getLastDamage());
            obj.addProperty("maxAir", le.getMaximumAir());
            obj.addProperty("maxNoDamageTicks", le.getMaximumNoDamageTicks());
            obj.addProperty("noDamageTicks", le.getNoDamageTicks());
            obj.addProperty("air", le.getRemainingAir());
            obj.addProperty("removeWhenFar", le.getRemoveWhenFarAway());
            if ((le instanceof Bat)){
                obj.addProperty("awake", ((Bat) le).isAwake());
            }
            if ((le instanceof Enderman)){
                Enderman e = (Enderman) le;
                MaterialData carrying = e.getCarriedMaterial();
                obj.add("carrying", carrying!=null?JsonUtil.jsonify(new ItemStack(carrying.getItemType(), 1, carrying.getData())):null);
            }
            if ((le instanceof AbstractHorse)){
                AbstractHorse horse = (AbstractHorse) le;
                obj.addProperty("domestication", horse.getDomestication());
                obj.addProperty("jumpStrength", horse.getJumpStrength());
                obj.addProperty("maxDomestication", horse.getMaxDomestication());
            }
            if ((le instanceof ChestedHorse)){
                obj.addProperty("carryingChest", ((ChestedHorse) le).isCarryingChest());
            }
            if ((le instanceof Creeper)){
                Creeper c = (Creeper) le;
                obj.addProperty("explosionRadius", c.getExplosionRadius());
                obj.addProperty("fuseTicks", c.getMaxFuseTicks());
                obj.addProperty("powered", c.isPowered());
            }

            if ((le instanceof Horse)){
                obj.addProperty("color", ((Horse) le).getColor().name());
                obj.addProperty("style", ((Horse) le).getStyle().name());
            }

            if ((le instanceof IronGolem)){
                obj.addProperty("playerCreated", ((IronGolem) le).isPlayerCreated());
            }

            if ((le instanceof Llama)){
                obj.addProperty("color", ((Llama) le).getColor().name());
            }

            if ((le instanceof Ocelot)){
                obj.addProperty("type", ((Ocelot)le).getCatType().name());
            }

            if ((le instanceof Parrot)){
                obj.addProperty("variant", ((Parrot)le).getVariant().name());
            }

            if ((le instanceof Pig)){
                obj.addProperty("saddle", ((Pig)le).hasSaddle());
            }

            if ((le instanceof PigZombie)){
                PigZombie pz = (PigZombie) le;
                obj.addProperty("anger", pz.getAnger());
                obj.addProperty("angry", pz.isAngry());
            }

            if ((le instanceof Rabbit)){
                obj.addProperty("type", ((Rabbit)le).getRabbitType().name());
            }

            if ((le instanceof Sheep)){
                obj.addProperty("sheared", ((Sheep)le).isSheared());
            }

            if ((le instanceof Colorable)){
                obj.addProperty("dyeColor", ((Colorable)le).getColor().name());
            }

            if ((le instanceof Slime)){
                obj.addProperty("size", ((Slime)le).getSize());
            }

            if ((le instanceof Snowman)){
                obj.addProperty("derp", ((Snowman)le).isDerp());
            }

            if ((le instanceof Spellcaster)){
                Evoker e = (Evoker) le;
                obj.addProperty("spell", e.getSpell().name());
            }

            if ((le instanceof Wolf)){
                Wolf w = (Wolf) le;
                obj.addProperty("collar", w.getCollarColor().name());
                obj.addProperty("angry", w.isAngry());
            }

            if ((le instanceof Villager)){
                Villager v = (Villager) le;
                obj.addProperty("career", v.getCareer().name());
                obj.addProperty("profession", v.getProfession().name());
                obj.addProperty("riches", v.getRiches());
            }

            if ((le instanceof Ageable)){
                Ageable a = (Ageable) le;
                obj.addProperty("canBreed", a.canBreed());
                obj.addProperty("age", a.getAge());
                obj.addProperty("ageLock", a.getAgeLock());
                obj.addProperty("adult", a.isAdult());
            }

            if ((le instanceof Sittable)){
                obj.addProperty("sitting", ((Sittable)le).isSitting());
            }

            if ((le instanceof Tameable)){
                obj.addProperty("tamed", ((Tameable)le).isTamed());
                Tameable t= (Tameable) le;
                if (t.getOwner() != null){
                    obj.add("owner", jsonify(t.getOwner()));
                }
                else{
                    obj.add("owner", null);
                }

            }

            if ((le instanceof Zombie)){
                Zombie z = (Zombie) le;
                obj.addProperty("baby", z.isBaby());
            }

            if ((le instanceof ZombieVillager)){
                obj.addProperty("zombieProfession", ((ZombieVillager)le).getVillagerProfession().name());
            }

        }
        else{
            obj.addProperty("living", false);
        }

        if ((entity instanceof Painting)){
            Painting painting = (Painting) entity;
            obj.addProperty("art", painting.getArt().name());
        }

        if ((entity instanceof CommandMinecart)){
            CommandMinecart cmd = (CommandMinecart) entity;
            obj.addProperty("name", cmd.getCustomName());
            obj.addProperty("command", cmd.getCommand());
        }

        if ((entity instanceof Item)){
            Item item = (Item) entity;
            obj.add("item", jsonify(item.getItemStack()));
            obj.addProperty("pickupDelay", item.getPickupDelay());
        }

        if ((entity instanceof ItemFrame)){
            ItemFrame itemFrame = (ItemFrame) entity;
            obj.add("item", itemFrame.getItem() != null ? jsonify(itemFrame.getItem()) : null);
            obj.addProperty("rotation", itemFrame.getRotation().name());
        }

        if ((entity instanceof Boat)){
            obj.addProperty("treeSpecies", ((Boat)entity).getWoodType().name());
        }

        if ((entity instanceof ArmorStand)){
            ArmorStand armorStand = (ArmorStand) entity;
            obj.add("bodyPose", jsonify(armorStand.getBodyPose()));
            obj.add("headPose", jsonify(armorStand.getHeadPose()));
            obj.add("leftArmPose", jsonify(armorStand.getLeftArmPose()));
            obj.add("leftLegPose", jsonify(armorStand.getLeftLegPose()));
            obj.add("rightArmPose", jsonify(armorStand.getRightArmPose()));
            obj.add("rightLegPose", jsonify(armorStand.getRightLegPose()));
            obj.addProperty("arms", armorStand.hasArms());
            obj.addProperty("basePlate", armorStand.hasBasePlate());
            obj.addProperty("marker", armorStand.isMarker());
            obj.addProperty("small", armorStand.isSmall());
            obj.addProperty("visible", armorStand.isVisible());
            obj.add("boots", armorStand.getBoots() != null ? jsonify(armorStand.getBoots()) : null);
            obj.add("leggings", armorStand.getLeggings() != null ? jsonify(armorStand.getLeggings()) : null);
            obj.add("chestplate", armorStand.getChestplate() != null ? jsonify(armorStand.getChestplate()) : null);
            obj.add("helmet", armorStand.getHelmet() != null ? jsonify(armorStand.getHelmet()) : null);
            obj.add("hand", armorStand.getItemInHand() != null ? jsonify(armorStand.getItemInHand()) : null);
        }

        return obj;

    }

    public static JsonObject jsonify(EulerAngle angle){
        JsonObject obj = new JsonObject();
        obj.addProperty("x", angle.getX());
        obj.addProperty("y", angle.getY());
        obj.addProperty("z", angle.getZ());
        return obj;
    }

    public static JsonObject jsonify(AnimalTamer tamer){
        JsonObject obj = new JsonObject();
        obj.addProperty("name", tamer.getName());
        obj.addProperty("uuid", tamer.getUniqueId().toString());
        return obj;
    }

    public static JsonObject jsonify(Map<Enchantment, Integer> enchants){

        JsonObject obj = new JsonObject();
        for(Map.Entry<Enchantment, Integer> entry : enchants.entrySet()){
            Enchantment ench = entry.getKey();
            int level = entry.getValue();
            obj.addProperty(ench.getName(), level);
        }

        return obj;
    }

    public static JsonObject jsonify(ItemStack itemStack){
        JsonObject obj = new JsonObject();
        obj.addProperty("type", itemStack.getType().name());
        obj.addProperty("amount", itemStack.getAmount());
        obj.addProperty("data", itemStack.getDurability());
        obj.addProperty("raw", ItemUtil.itemToJSON(itemStack));
        return obj;

    }

    public static JsonObject jsonify(Pattern pattern) {
        JsonObject obj = new JsonObject();
        obj.addProperty("color", pattern.getColor().name());
        obj.addProperty("type", pattern.getPattern().name());
        return obj;
    }

    public static JsonObject jsonify(Inventory inventory) {
        JsonObject obj = new JsonObject();
        for (int i = 0; i < inventory.getSize(); i++) {
            obj.addProperty("slot" + i, ItemUtil.itemToJSON(inventory.getItem(i)));
        }
        return obj;
    }

    public static JsonObject jsonify(BlockState block) {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", block.getType().name());
        obj.addProperty("data", block.getRawData());
        if ((block instanceof Banner)) {
            obj.addProperty("baseColor", ((Banner) block).getBaseColor().name());
            JsonArray patterns = new JsonArray();
            for (Pattern pattern : ((Banner) block).getPatterns()) {
                patterns.add(jsonify(pattern));
            }
            obj.add("patterns", patterns);
        }
        if ((block instanceof Beacon)) {
            obj.addProperty("primaryEffect", ((Beacon) block).getPrimaryEffect().getType().getName());
            obj.addProperty("secondaryEffect", ((Beacon) block).getSecondaryEffect().getType().getName());
        }
        if ((block instanceof Colorable)) {
            obj.addProperty("color", ((Colorable) block).getColor().name());
        }
        if ((block instanceof BrewingStand)) {
            obj.addProperty("brewingTime", ((BrewingStand) block).getBrewingTime());
            obj.addProperty("fuelLevel", ((BrewingStand) block).getFuelLevel());
            obj.add("inventory", jsonify(((BrewingStand) block).getSnapshotInventory()));
        }
        if ((block instanceof Container)) {
            Container container = (Container) block;
            obj.add("inventory", jsonify(container.getSnapshotInventory()));
        }
        if ((block instanceof Lockable)) {
            Lockable lock = (Lockable) block;
            obj.addProperty("locked", lock.getLock());
        }
        if ((block instanceof CommandBlock)) {
            obj.addProperty("name", ((CommandBlock) block).getName());
            obj.addProperty("command", ((CommandBlock) block).getCommand());
        }
        if ((block instanceof Chest)) {
            obj.add("inventory", jsonify(((Chest) block).getBlockInventory()));
        }
        if ((block instanceof CreatureSpawner)) {
            CreatureSpawner spawner = (CreatureSpawner) block;
            obj.addProperty("spawnRange", spawner.getSpawnRange());
            obj.addProperty("spawnType", spawner.getSpawnedType().name());
            obj.addProperty("spawnCount", spawner.getSpawnCount());
            obj.addProperty("requiredPlayers", spawner.getRequiredPlayerRange());
            obj.addProperty("minDelay", spawner.getMinSpawnDelay());
            obj.addProperty("maxDelay", spawner.getMaxSpawnDelay());
            obj.addProperty("maxNearby", spawner.getMaxNearbyEntities());
            obj.addProperty("delay", spawner.getDelay());
        }
        if ((block instanceof Nameable)) {
            obj.addProperty("customName", ((Nameable) block).getCustomName());
        }
        if ((block instanceof EndGateway)) {
            obj.addProperty("exactTeleport", ((EndGateway) block).isExactTeleport());
            obj.add("exitLocation", jsonify(((EndGateway) block).getExitLocation()));
        }
        if ((block instanceof FlowerPot)) {
            obj.addProperty("contents", ItemUtil.itemToJSON(new ItemStack(((FlowerPot) block).getContents().getItemType(), 1, ((FlowerPot) block).getContents().getData())));
        }
        if ((block instanceof Furnace)) {
            Furnace furnace = (Furnace) block;
            obj.addProperty("cookTime", furnace.getCookTime());
            obj.addProperty("burnTime", furnace.getBurnTime());
            obj.add("inventory", jsonify(furnace.getSnapshotInventory()));
        }
        if ((block instanceof NoteBlock)) {
            NoteBlock note = (NoteBlock) block;
            obj.addProperty("note", note.getRawNote());
        }
        if ((block instanceof Sign)) {
            JsonArray signText = new JsonArray();
            for (int i = 0; i < 4; i++) {
                signText.add(((Sign) block).getLine(i));
            }
            obj.add("text", signText);
        }
        if ((block instanceof Skull)) {
            obj.addProperty("skullType", ((Skull) block).getSkullType().name());
            obj.addProperty("rotation", ((Skull) block).getRotation().name());
            obj.addProperty("owningPlayer", ((Skull) block).hasOwner() ? ((Skull) block).getOwningPlayer().getUniqueId().toString() : null);
        }
        return obj;

    }

    public static Location fromJson(JsonObject obj) {
        World world = Bukkit.getWorld(obj.get("world").getAsString());
        double x = obj.get("x").getAsDouble();
        double y = obj.get("y").getAsDouble();
        double z = obj.get("z").getAsDouble();
        return new Location(world, x, y, z);
    }

    public static JsonObject jsonify(Location exitLocation) {
        JsonObject obj = new JsonObject();
        obj.addProperty("world", exitLocation.getWorld().getName());
        obj.addProperty("x", exitLocation.getX());
        obj.addProperty("y", exitLocation.getY());
        obj.addProperty("z", exitLocation.getZ());
        return obj;
    }

}
