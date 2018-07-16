package co.melondev.Snitch.handlers;

import co.melondev.Snitch.entities.SnitchEntry;
import co.melondev.Snitch.entities.SnitchProcessHandler;
import co.melondev.Snitch.entities.SnitchSession;
import co.melondev.Snitch.enums.EnumSnitchActivity;

/**
 * Created by Devon on 7/16/18.
 */
public class ArmorStandEditHandler implements SnitchProcessHandler {
    @Override
    public boolean handleRollback(SnitchSession session, SnitchEntry entry) {

        return false;
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
        return false;
    }
}
