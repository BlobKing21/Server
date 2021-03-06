package org.cloudburstmc.server.block.behavior;

import com.nukkitx.math.vector.Vector3f;
import org.cloudburstmc.server.block.Block;
import org.cloudburstmc.server.block.BlockState;
import org.cloudburstmc.server.entity.Entity;
import org.cloudburstmc.server.item.behavior.Item;
import org.cloudburstmc.server.math.AxisAlignedBB;
import org.cloudburstmc.server.math.Direction;
import org.cloudburstmc.server.player.Player;
import org.cloudburstmc.server.utils.BlockColor;

import javax.annotation.Nonnull;
import java.util.Objects;

public class BlockBehaviorDelegate extends BlockBehavior {

    private final BlockBehavior parent;

    public BlockBehaviorDelegate(@Nonnull BlockBehavior parent) {
        Objects.requireNonNull(parent);
        this.parent = parent;
    }

    public BlockBehavior getParent() {
        return parent;
    }

    @Override
    public boolean canHarvestWithHand() {
        return parent.canHarvestWithHand();
    }

    @Override
    public boolean isBreakable(Item item) {
        return parent.isBreakable(item);
    }

    @Override
    public int tickRate() {
        return parent.tickRate();
    }

    @Override
    public int onUpdate(Block block, int type) {
        return parent.onUpdate(block, type);
    }

    @Override
    public boolean onActivate(Block block, Item item) {
        return parent.onActivate(block, item);
    }

    @Override
    public boolean onActivate(Block block, Item item, Player player) {
        return parent.onActivate(block, item, player);
    }

    @Override
    public int getBurnChance() {
        return parent.getBurnChance();
    }

    @Override
    public int getBurnAbility() {
        return parent.getBurnAbility();
    }

    @Override
    public int getToolType() {
        return parent.getToolType();
    }

    @Override
    public int getLightLevel(Block block) {
        return parent.getLightLevel(block);
    }

    @Override
    public boolean canBePlaced() {
        return parent.canBePlaced();
    }

    @Override
    public boolean canBeReplaced(Block block) {
        return parent.canBeReplaced(block);
    }

    @Override
    public boolean isTransparent() {
        return parent.isTransparent();
    }

    @Override
    public boolean isSolid() {
        return parent.isSolid();
    }

    @Override
    public boolean isLiquid() {
        return parent.isLiquid();
    }

    @Override
    public int getFilterLevel() {
        return parent.getFilterLevel();
    }

    @Override
    public boolean canBeActivated(Block block) {
        return parent.canBeActivated(block);
    }

    @Override
    public boolean hasEntityCollision() {
        return parent.hasEntityCollision();
    }

    @Override
    public boolean canPassThrough() {
        return parent.canPassThrough();
    }

    @Override
    public boolean canBePushed() {
        return parent.canBePushed();
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return parent.hasComparatorInputOverride();
    }

    @Override
    public int getComparatorInputOverride(Block block) {
        return parent.getComparatorInputOverride(block);
    }

    @Override
    public boolean canBeClimbed() {
        return parent.canBeClimbed();
    }

    @Override
    public BlockColor getColor(Block block) {
        return parent.getColor(block);
    }

    @Override
    public boolean canBeFlooded() {
        return parent.canBeFlooded();
    }

    @Override
    public boolean place(Item item, Block block, Block target, Direction face, Vector3f clickPos, Player player) {
        return parent.place(item, block, target, face, clickPos, player);
    }

    @Override
    public boolean placeBlock(Block block, Item item) {
        return parent.placeBlock(block, item);
    }

    @Override
    public boolean placeBlock(Block block, Item item, boolean update) {
        return parent.placeBlock(block, item, update);
    }

    @Override
    public boolean placeBlock(Block block, BlockState newState) {
        return parent.placeBlock(block, newState);
    }

    @Override
    public boolean placeBlock(Block block, BlockState newState, boolean update) {
        return parent.placeBlock(block, newState, update);
    }

    @Override
    public boolean onBreak(Block block, Item item) {
        return parent.onBreak(block, item);
    }

    @Override
    public boolean onBreak(Block block, Item item, Player player) {
        return parent.onBreak(block, item, player);
    }

    @Override
    public float getHardness() {
        return parent.getHardness();
    }

    @Override
    public String getDescriptionId(BlockState state) {
        return parent.getDescriptionId(state);
    }

    @Override
    public float getResistance() {
        return parent.getResistance();
    }

    @Override
    public float getFrictionFactor() {
        return parent.getFrictionFactor();
    }

    @Override
    public Vector3f addVelocityToEntity(Block block, Vector3f vector, Entity entity) {
        return parent.addVelocityToEntity(block, vector, entity);
    }

    @Override
    public Item[] getDrops(Block block, Item hand) {
        return parent.getDrops(block, hand);
    }

    @Override
    public float getBreakTime(BlockState state, Item item, Player player) {
        return parent.getBreakTime(state, item, player);
    }

    @Override
    public boolean canBeBrokenWith(Item item) {
        return parent.canBeBrokenWith(item);
    }

    @Override
    @Deprecated
    public float getBreakTime(Item item) {
        return parent.getBreakTime(item);
    }

    @Override
    public boolean collidesWithBB(Block block, AxisAlignedBB bb) {
        return parent.collidesWithBB(block, bb);
    }

    @Override
    public boolean collidesWithBB(Block block, AxisAlignedBB bb, boolean collisionBB) {
        return parent.collidesWithBB(block, bb, collisionBB);
    }

    @Override
    public void onEntityCollide(Block block, Entity entity) {
        parent.onEntityCollide(block, entity);
    }

    @Override
    public AxisAlignedBB getBoundingBox() {
        return parent.getBoundingBox();
    }

    @Override
    public AxisAlignedBB getCollisionBoxes(Block block) {
        return parent.getCollisionBoxes(block);
    }

    @Override
    public String getSaveId() {
        return parent.getSaveId();
    }

    @Override
    public int getWeakPower(Block block, Direction face) {
        return parent.getWeakPower(block, face);
    }

    @Override
    public int getStrongPower(Block block, Direction side) {
        return parent.getStrongPower(block, side);
    }

    @Override
    public boolean isPowerSource(Block block) {
        return parent.isPowerSource(block);
    }

    @Override
    public int getDropExp() {
        return parent.getDropExp();
    }

    @Override
    public boolean isNormalBlock(Block block) {
        return parent.isNormalBlock(block);
    }

    @Override
    public BlockBehavior clone() {
        return parent.clone();
    }

    @Override
    public Item toItem(Block block) {
        return parent.toItem(block);
    }

    @Override
    public boolean canSilkTouch() {
        return parent.canSilkTouch();
    }

    @Override
    public boolean canWaterlogSource() {
        return parent.canWaterlogSource();
    }

    @Override
    public boolean canWaterlogFlowing() {
        return parent.canWaterlogFlowing();
    }
}
