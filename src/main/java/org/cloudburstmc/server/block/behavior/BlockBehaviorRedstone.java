package org.cloudburstmc.server.block.behavior;

import org.cloudburstmc.server.block.Block;
import org.cloudburstmc.server.item.ItemStack;
import org.cloudburstmc.server.item.ToolType;
import org.cloudburstmc.server.item.behavior.ItemToolBehavior;
import org.cloudburstmc.server.math.Direction;
import org.cloudburstmc.server.utils.BlockColor;

public class BlockBehaviorRedstone extends BlockBehaviorSolid {

    @Override
    public float getResistance() {
        return 10;
    }

    @Override
    public float getHardness() {
        return 5;
    }

    @Override
    public ToolType getToolType() {
        return ItemToolBehavior.TYPE_PICKAXE;
    }

    @Override
    public ItemStack[] getDrops(Block block, ItemStack hand) {
        if (hand.isPickaxe() && hand.getTier() >= ItemToolBehavior.TIER_WOODEN) {
            return new ItemStack[]{
                    toItem(block)
            };
        } else {
            return new ItemStack[0];
        }
    }

    @Override
    public BlockColor getColor(Block block) {
        return BlockColor.REDSTONE_BLOCK_COLOR;
    }

    @Override
    public boolean isPowerSource(Block block) {
        return true;
    }

    @Override
    public int getWeakPower(Block block, Direction face) {
        return 15;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}