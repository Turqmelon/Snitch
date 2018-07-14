package co.melondev.Snitch.entities;

import co.melondev.Snitch.enums.EnumSnitchActivity;

/**
 * Created by Devon on 7/14/18.
 */
public class SnitchRollback extends SnitchPreview {
    public SnitchRollback(SnitchSession session, SnitchCallback callback) {
        super(session, callback);
        activity = EnumSnitchActivity.ROLLBACK;
    }
}
