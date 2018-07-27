package co.melondev.Snitch.entities;

import co.melondev.Snitch.enums.EnumSnitchActivity;

/**
 * Created by Devon on 7/14/18.
 *
 * Implemented by actions to determine how to handle rollback, restores, and previews - as well as what activities they support.
 */
public interface SnitchProcessHandler {

    boolean handleRollback(SnitchSession session, SnitchEntry entry);

    boolean handlePreview(SnitchSession session, SnitchEntry entry);

    boolean handleRestore(SnitchSession session, SnitchEntry entry);

    boolean can(EnumSnitchActivity activity);

}
