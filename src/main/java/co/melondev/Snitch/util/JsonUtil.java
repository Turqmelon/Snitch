package co.melondev.Snitch.util;

import com.google.gson.JsonObject;
import org.bukkit.DyeColor;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.entity.minecart.CommandMinecart;
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

    public static JsonObject jsonify(Block block){
        JsonObject obj = new JsonObject();
        obj.addProperty("type", block.getType().name());
        obj.addProperty("data", block.getData());
        obj.addProperty("biome", block.getBiome().name());
        return obj;

    }

}
