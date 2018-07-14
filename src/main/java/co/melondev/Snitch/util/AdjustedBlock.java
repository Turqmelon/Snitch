package co.melondev.Snitch.util;

import org.bukkit.block.BlockState;

/**
 * Created by Devon on 7/14/18.
 */
public class AdjustedBlock {

    private BlockState oldState;
    private BlockState newState;

    public AdjustedBlock(BlockState oldState, BlockState newState) {
        this.oldState = oldState;
        this.newState = newState;
    }

    public BlockState getOldState() {
        return oldState;
    }

    public BlockState getNewState() {
        return newState;
    }
}
