package co.melondev.Snitch.util;

import org.bukkit.block.BlockState;

/**
 * Created by Devon on 7/14/18.
 *
 * Stores blockstates for the old and new versions of a block
 */
public class AdjustedBlock {

    /**
     * The old block
     */
    private BlockState oldState;

    /**
     * The new block
     */
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
