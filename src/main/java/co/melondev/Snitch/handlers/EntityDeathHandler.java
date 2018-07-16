package co.melondev.Snitch.handlers;

import co.melondev.Snitch.entities.SnitchEntry;
import co.melondev.Snitch.entities.SnitchProcessHandler;
import co.melondev.Snitch.entities.SnitchSession;
import co.melondev.Snitch.enums.EnumSnitchActivity;
import co.melondev.Snitch.util.EntityUtil;
import com.google.gson.JsonObject;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

/**
 * Created by Devon on 7/16/18.
 */
public class EntityDeathHandler implements SnitchProcessHandler {
    @Override
    public boolean handleRollback(SnitchSession session, SnitchEntry entry) {
        JsonObject entityData = entry.getData().get("entity").getAsJsonObject();

        EntityType type = EntityType.valueOf(entityData.get("entityType").getAsString());
        Entity entity = entry.getSnitchWorld().getBukkitWorld().spawnEntity(entry.getSnitchPosition().toLocation(entry.getSnitchWorld()), type);
        EntityUtil.rebuildEntity(entity, entityData);

        return true;
    }

    @Override
    public boolean handlePreview(SnitchSession session, SnitchEntry entry) {
        return false;
    }

    @Override
    public boolean handleRestore(SnitchSession session, SnitchEntry entry) {
        return false;
    }

    @Override
    public boolean can(EnumSnitchActivity activity) {
        return activity == EnumSnitchActivity.ROLLBACK;
    }
}
