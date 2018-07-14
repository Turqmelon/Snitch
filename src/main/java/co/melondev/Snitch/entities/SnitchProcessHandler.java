package co.melondev.Snitch.entities;

import co.melondev.Snitch.enums.EnumSnitchActivity;

/**
 * Created by Devon on 7/14/18.
 */
public interface SnitchProcessHandler {

    boolean handleRollback(SnitchSession session, SnitchEntry entry);

    boolean handlePreview(SnitchSession session, SnitchEntry entry);

    boolean handleRestore(SnitchSession session, SnitchEntry entry);

    boolean can(EnumSnitchActivity activity);

}
