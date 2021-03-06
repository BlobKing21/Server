package org.cloudburstmc.server.event.entity;

import org.cloudburstmc.server.block.Block;
import org.cloudburstmc.server.block.BlockState;
import org.cloudburstmc.server.entity.Entity;
import org.cloudburstmc.server.event.Cancellable;

/**
 * Created on 15-10-26.
 */
public class EntityBlockChangeEvent extends EntityEvent implements Cancellable {

    private final Block block;
    private final BlockState to;

    public EntityBlockChangeEvent(Entity entity, Block block, BlockState to) {
        this.entity = entity;
        this.block = block;
        this.to = to;
    }

    public Block getBlock() {
        return block;
    }

    public BlockState getTo() {
        return to;
    }

}
