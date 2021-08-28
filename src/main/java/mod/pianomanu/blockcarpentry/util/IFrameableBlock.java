package mod.pianomanu.blockcarpentry.util;

import mod.pianomanu.blockcarpentry.tileentity.FrameBlockTile;
import mod.pianomanu.blockcarpentry.tileentity.IFrameEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

/**
 * The interface for interacting with Frameable blocks.
 * Because most kinds of frameable block have to extend their vanilla counterpart, shared code exists either here
 * or in {@link FramedBlockHelper}. These methods can be overridden if necessary to achieve block specific behaviours
 * but in most cases the default implementations should be sufficient or at least extendable while still calling the super.
 */
public interface IFrameableBlock {
    /**
     * The method to call when attempting to insert a block into a frame after all other checks have been performed.
     * By this point things like isItemStackValidInsertCandidate have already run and so this method is intended for
     * subclass specific behaviours where additional checks or success behaviours are necessary.
     * 
     * @param world  the world the block is in
     * @param item   the ItemStack to insert
     * @param state  the state of the block
     * @param pos    the position of the block
     * @param player the player inserting the block
     * @param hand   the hand the item is in
     * @return ActionResultType.SUCCESS if the insert succeeded.
     */
    default ActionResultType attemptInsertBlock(World world, ItemStack item, BlockState state, BlockPos pos, PlayerEntity player, Hand hand ) {
        if(!(world.getTileEntity(pos) instanceof IFrameEntity)) {
            return ActionResultType.FAIL;
        }

        Block heldBlock = ((BlockItem) item.getItem()).getBlock();
        BlockState handBlockState = heldBlock.getDefaultState();
        insertBlock(world, pos, state, handBlockState);
        if (!player.isCreative()) {
            item.setCount(item.getCount() - 1);
        }
        return ActionResultType.SUCCESS;
    }

    /**
     * Used to apply a block to a frame. Sets the mimic and does a block update.
     *
     * @param worldIn   the world where we drop the block
     * @param pos       the block position where we drop the block
     * @param state     the old block state
     * @param handBlock the block state of the held block - the block we want to insert into the frame
     */
    default void insertBlock (IWorld worldIn, BlockPos pos, BlockState state, BlockState handBlock) {
        TileEntity tileentity = worldIn.getTileEntity(pos);
        if (tileentity instanceof IFrameEntity) {
            IFrameEntity frameTileEntity = (IFrameEntity) tileentity;
            frameTileEntity.clear();
            frameTileEntity.setMimic(handBlock);
            worldIn.setBlockState(pos, state.with(BCBlockStateProperties.CONTAINS_BLOCK, Boolean.TRUE), Constants.BlockFlags.BLOCK_UPDATE);
        }
    }

    /**
     * Used to drop the contained block (the currently applied facade, if it exists)
     *
     * @param worldIn the world where we drop the block
     * @param pos     the block position where we drop the block
     */
    default void dropContainedBlock(World worldIn, BlockPos pos) {
        if (!worldIn.isRemote) {
            TileEntity tileentity = worldIn.getTileEntity(pos);
            if (tileentity instanceof FrameBlockTile) {
                FrameBlockTile frameTileEntity = (FrameBlockTile) tileentity;
                BlockState blockState = frameTileEntity.getMimic();
                if (!(blockState == null)) {
                    worldIn.playEvent(1010, pos, 0); // ???
                    frameTileEntity.clear();
                    float offset = 0.7F;
                    double offx = (double) (worldIn.rand.nextFloat() * offset) + (double) 0.15F;
                    double offy = (double) (worldIn.rand.nextFloat() * offset) + (double) 0.060000002F + 0.6D;
                    double offz = (double) (worldIn.rand.nextFloat() * offset) + (double) 0.15F;
                    ItemStack itemstack = new ItemStack(blockState.getBlock());
                    ItemEntity itementity = new ItemEntity(worldIn, (double) pos.getX() + offx, (double) pos.getY() + offy, (double) pos.getZ() + offz, itemstack);
                    itementity.setDefaultPickupDelay();
                    worldIn.addEntity(itementity);
                    frameTileEntity.clear();
                }
            }
        }
    }
}
