package co.melondev.Snitch.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.server.v1_12_R1.MojangsonParseException;
import org.bukkit.Art;
import org.bukkit.DyeColor;
import org.bukkit.Rotation;
import org.bukkit.TreeSpecies;
import org.bukkit.entity.*;
import org.bukkit.entity.minecart.CommandMinecart;
import org.bukkit.material.Colorable;
import org.bukkit.material.MaterialData;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.UUID;

/**
 * Created by Devon on 7/16/18.
 */
public class EntityUtil {

    public static void rebuildEntity(Entity entity, JsonObject entityData) {
        entity.setFireTicks(entityData.get("fire").getAsInt());
        entity.setCustomNameVisible(entityData.get("nameVisible").getAsBoolean());
        if (!entityData.get("customName").isJsonNull()) {
            entity.setCustomName(entityData.get("customName").getAsString());
        }
        entity.setFallDistance(entityData.get("fallDistance").getAsFloat());
        entity.setGlowing(entityData.get("glowing").getAsBoolean());
        entity.setGravity(entityData.get("gravity").getAsBoolean());
        entity.setInvulnerable(entityData.get("invulnerable").getAsBoolean());
        entity.setPortalCooldown(entityData.get("portalCooldown").getAsInt());
        entity.setSilent(entityData.get("silent").getAsBoolean());
        entity.setTicksLived(entityData.get("ticksLived").getAsInt());

        String[] velocityRaw = entityData.get("velocity").getAsString().split(",");
        entity.setVelocity(new Vector(Double.parseDouble(velocityRaw[0]), Double.parseDouble(velocityRaw[1]), Double.parseDouble(velocityRaw[2])));

        if ((entity instanceof LivingEntity)) {
            LivingEntity le = (LivingEntity) entity;
            le.setAI(entityData.get("ai").getAsBoolean());
            le.setCanPickupItems(entityData.get("pickupItems").getAsBoolean());
            le.setCollidable(entityData.get("collidable").getAsBoolean());
            le.setGliding(entityData.get("gliding").getAsBoolean());
            le.setLastDamage(entityData.get("lastDamage").getAsDouble());
            le.setMaximumAir(entityData.get("maxAir").getAsInt());
            le.setMaximumNoDamageTicks(entityData.get("maxNoDamageTicks").getAsInt());
            le.setRemainingAir(entityData.get("air").getAsInt());
            le.setRemoveWhenFarAway(entityData.get("removeWhenFar").getAsBoolean());
            if ((le instanceof Bat)) {
                ((Bat) le).setAwake(entityData.get("awake").getAsBoolean());
            }
            if ((le instanceof Enderman)) {
                JsonElement carrying = entityData.get("carrying");
                if (!carrying.isJsonNull()) {
                    try {
                        org.bukkit.inventory.ItemStack itemStack = ItemUtil.JSONtoItemStack(carrying.getAsString());
                        ((Enderman) le).setCarriedMaterial(new MaterialData(itemStack.getType(), (byte) itemStack.getDurability()));
                    } catch (MojangsonParseException e) {
                        e.printStackTrace();
                    }
                }
            }
            if ((le instanceof AbstractHorse)) {
                ((AbstractHorse) le).setDomestication(entityData.get("domestication").getAsInt());
                ((AbstractHorse) le).setJumpStrength(entityData.get("jumpStrength").getAsDouble());
                ((AbstractHorse) le).setMaxDomestication(entityData.get("maxDomestication").getAsInt());
            }
            if ((le instanceof ChestedHorse)) {
                ((ChestedHorse) le).setCarryingChest(entityData.get("carryingChest").getAsBoolean());
            }
            if ((le instanceof Creeper)) {
                ((Creeper) le).setExplosionRadius(entityData.get("explosionRadius").getAsInt());
                ((Creeper) le).setMaxFuseTicks(entityData.get("fuseTicks").getAsInt());
                ((Creeper) le).setPowered(entityData.get("powered").getAsBoolean());
            }
            if ((le instanceof Horse)) {
                ((Horse) le).setColor(Horse.Color.valueOf(entityData.get("color").getAsString()));
                ((Horse) le).setStyle(Horse.Style.valueOf(entityData.get("style").getAsString()));
            }
            if ((le instanceof IronGolem)) {
                ((IronGolem) le).setPlayerCreated(entityData.get("playerCreated").getAsBoolean());
            }
            if ((le instanceof Llama)) {
                ((Llama) le).setColor(Llama.Color.valueOf(entityData.get("color").getAsString()));
            }
            if ((le instanceof Ocelot)) {
                ((Ocelot) le).setCatType(Ocelot.Type.valueOf(entityData.get("type").getAsString()));
            }
            if ((le instanceof Parrot)) {
                ((Parrot) le).setVariant(Parrot.Variant.valueOf(entityData.get("variant").getAsString()));
            }
            if ((le instanceof Pig)) {
                ((Pig) le).setSaddle(entityData.get("saddle").getAsBoolean());
            }
            if ((le instanceof PigZombie)) {
                ((PigZombie) le).setAnger(entityData.get("anger").getAsInt());
                ((PigZombie) le).setAngry(entityData.get("angry").getAsBoolean());
            }
            if ((le instanceof Rabbit)) {
                ((Rabbit) le).setRabbitType(Rabbit.Type.valueOf(entityData.get("type").getAsString()));
            }
            if ((le instanceof Colorable)) {
                ((Colorable) le).setColor(DyeColor.valueOf(entityData.get("dyeColor").getAsString()));
            }
            if ((le instanceof Slime)) {
                ((Slime) le).setSize(entityData.get("size").getAsInt());
            }
            if ((le instanceof Snowman)) {
                ((Snowman) le).setDerp(entityData.get("derp").getAsBoolean());
            }
            if ((le instanceof Spellcaster)) {
                ((Spellcaster) le).setSpell(Spellcaster.Spell.valueOf(entityData.get("spell").getAsString()));
            }
            if ((le instanceof Wolf)) {
                ((Wolf) le).setCollarColor(DyeColor.valueOf(entityData.get("collar").getAsString()));
                ((Wolf) le).setAngry(entityData.get("angry").getAsBoolean());
            }
            if ((le instanceof Villager)) {
                ((Villager) le).setCareer(Villager.Career.valueOf(entityData.get("career").getAsString()));
                ((Villager) le).setProfession(Villager.Profession.valueOf(entityData.get("profession").getAsString()));
                ((Villager) le).setRiches(entityData.get("riches").getAsInt());
            }
            if ((le instanceof Ageable)) {
                Ageable a = (Ageable) le;
                a.setBreed(entityData.get("canBreed").getAsBoolean());
                a.setAge(entityData.get("age").getAsInt());
                a.setAgeLock(entityData.get("ageLock").getAsBoolean());
                if (entityData.get("adult").getAsBoolean()) {
                    a.setAdult();
                } else {
                    a.setBaby();
                }
            }
            if ((le instanceof Sittable)) {
                ((Sittable) le).setSitting(entityData.get("sitting").getAsBoolean());
            }
            if ((le instanceof Tameable)) {
                Tameable t = (Tameable) le;
                t.setTamed(entityData.get("tamed").getAsBoolean());
                JsonElement owner = entityData.get("owner");
                if (!owner.isJsonNull()) {
                    t.setOwner(new AnimalTamer() {
                        @Override
                        public String getName() {
                            return owner.getAsJsonObject().get("name").getAsString();
                        }

                        @Override
                        public UUID getUniqueId() {
                            return UUID.fromString(owner.getAsJsonObject().get("uuid").getAsString());
                        }
                    });
                }
            }
            if ((le instanceof Zombie)) {
                Zombie z = (Zombie) le;
                z.setBaby(entityData.get("baby").getAsBoolean());
            }
            if ((le instanceof ZombieVillager)) {
                ZombieVillager zv = (ZombieVillager) le;
                zv.setVillagerProfession(Villager.Profession.valueOf(entityData.get("zombieProfession").getAsString()));
            }
        }

        if ((entity instanceof Painting)) {
            ((Painting) entity).setArt(Art.valueOf(entityData.get("art").getAsString()));
        }
        if ((entity instanceof CommandMinecart)) {
            ((CommandMinecart) entity).setName(entityData.get("name").getAsString());
            ((CommandMinecart) entity).setCommand(entityData.get("command").getAsString());
        }
        if ((entity instanceof Item)) {
            try {
                ((Item) entity).setItemStack(ItemUtil.JSONtoItemStack(entityData.get("item").getAsString()));
            } catch (MojangsonParseException e) {
                e.printStackTrace();
            }
            ((Item) entity).setPickupDelay(entityData.get("pickupDelay").getAsInt());
        }
        if ((entity instanceof ItemFrame)) {
            JsonElement item = entityData.get("item");
            if (!item.isJsonNull()) {
                try {
                    ((ItemFrame) entity).setItem(ItemUtil.JSONtoItemStack(item.getAsString()));
                } catch (MojangsonParseException e) {
                    e.printStackTrace();
                }
            }
            ((ItemFrame) entity).setRotation(Rotation.valueOf(entityData.get("rotation").getAsString()));
        }
        if ((entity instanceof Boat)) {
            ((Boat) entity).setWoodType(TreeSpecies.valueOf(entityData.get("treeSpecies").getAsString()));
        }
        if ((entity instanceof ArmorStand)) {
            ArmorStand as = (ArmorStand) entity;
            as.setBodyPose(rebuildAngle(entityData.getAsJsonObject("bodyPose")));
            as.setHeadPose(rebuildAngle(entityData.getAsJsonObject("headPose")));
            as.setLeftArmPose(rebuildAngle(entityData.getAsJsonObject("leftArmPose")));
            as.setLeftLegPose(rebuildAngle(entityData.getAsJsonObject("leftLegPose")));
            as.setRightArmPose(rebuildAngle(entityData.getAsJsonObject("rightArmPose")));
            as.setRightLegPose(rebuildAngle(entityData.getAsJsonObject("rightLegPose")));
            as.setArms(entityData.get("arms").getAsBoolean());
            as.setBasePlate(entityData.get("basePlate").getAsBoolean());
            as.setMarker(entityData.get("marker").getAsBoolean());
            as.setSmall(entityData.get("small").getAsBoolean());
            as.setVisible(entityData.get("visible").getAsBoolean());
            JsonElement boots = entityData.get("boots");
            JsonElement legs = entityData.get("leggings");
            JsonElement chest = entityData.get("chestplate");
            JsonElement helm = entityData.get("helmet");
            JsonElement hand = entityData.get("hand");
            if (!boots.isJsonNull()) {
                try {
                    as.setBoots(ItemUtil.JSONtoItemStack(boots.getAsString()));
                } catch (MojangsonParseException e) {
                    e.printStackTrace();
                }
            }
            if (!legs.isJsonNull()) {
                try {
                    as.setLeggings(ItemUtil.JSONtoItemStack(legs.getAsString()));
                } catch (MojangsonParseException e) {
                    e.printStackTrace();
                }
            }
            if (!chest.isJsonNull()) {
                try {
                    as.setChestplate(ItemUtil.JSONtoItemStack(chest.getAsString()));
                } catch (MojangsonParseException e) {
                    e.printStackTrace();
                }
            }
            if (!helm.isJsonNull()) {
                try {
                    as.setHelmet(ItemUtil.JSONtoItemStack(helm.getAsString()));
                } catch (MojangsonParseException e) {
                    e.printStackTrace();
                }
            }
            if (!hand.isJsonNull()) {
                try {
                    as.setItemInHand(ItemUtil.JSONtoItemStack(hand.getAsString()));
                } catch (MojangsonParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static EulerAngle rebuildAngle(JsonObject obj) {
        double x = obj.get("x").getAsDouble();
        double y = obj.get("y").getAsDouble();
        double z = obj.get("z").getAsDouble();
        return new EulerAngle(x, y, z);
    }

}
