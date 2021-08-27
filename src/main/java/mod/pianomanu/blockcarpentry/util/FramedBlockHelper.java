package mod.pianomanu.blockcarpentry.util;

import mod.pianomanu.blockcarpentry.BlockCarpentryMain;
import mod.pianomanu.blockcarpentry.setup.Registration;
import mod.pianomanu.blockcarpentry.setup.config.BCModConfig;
import mod.pianomanu.blockcarpentry.tileentity.FrameBlockTile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Collection of static shared code for framed blocks that doesn't belong in {@link IFrameableBlock}
 */
public class FramedBlockHelper {
    public static final BooleanProperty CONTAINS_BLOCK = BCBlockStateProperties.CONTAINS_BLOCK;
    public static final IntegerProperty LIGHT_LEVEL = BCBlockStateProperties.LIGHT_LEVEL;

    /**
     * Used to check whether an ItemStack contains a valid block to be used as a framed block material
     * @param stack the ItemStack to validate
     * @return whether the ItemStack can be used on a framed block
     */
    public static boolean isItemStackValidInsertCandidate(ItemStack stack) {
        if(stack.isEmpty() || !(stack.getItem() instanceof BlockItem)) {
            return false;
        }

        BlockItem block = (BlockItem)stack.getItem();
        //ignore other blockcarpentry blocks
        if(Objects.requireNonNull(block.getRegistryName()).getNamespace().equals(BlockCarpentryMain.MOD_ID)) {
            return false;
        }

        //TODO: investigate checking whether a block's voxelshape is opaque as part of this process
        //other blocks may override isOpaque to return false even on blocks that are a perfect cube, so it's
        //probably better to check the OPAQUE_CACHE ourselves
        return BlockSavingHelper.isValidBlock(block.getBlock());
    }

    /**
     * Generic basic right click behaviour for all framed blocks
     * Attempts to use the player's item to increase light level, swap texture, change appearance, etc,
     * then attempts to apply the item in the player's hand to the block to update the mimic
     * finally attempts to use the player's item as a hammer to remove the current mimic
     *
     * @param block  the frameable block type in question
     * @param state  the blockstate of the specific block
     * @param world  the world the block is in
     * @param pos    the position of the block
     * @param player the player interacting with the block
     * @param hand   the hand the player is using
     * @param trace  the raycast from the player's interaction
     * @return the resulting {@link ActionResultType}
     */
    public static ActionResultType doGenericRightClick(IFrameableBlock block, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult trace) {
        ItemStack item = player.getHeldItem(hand);
        if (!world.isRemote) {
            //this can have a bunch of side effects like consuming the itemstack because its a collection of tool use and modifier functions
            //TODO: adapt these to be more obvious parts of the interaction process
            BlockAppearanceHelper.setAppearanceDetails(world, item, state, pos, player, hand);

            //attempt to insert the ItemStack
            if(FramedBlockHelper.isItemStackValidInsertCandidate(item)) {
                if(state.get(CONTAINS_BLOCK)) { return ActionResultType.PASS; }

                TileEntity tileEntity = world.getTileEntity(pos);
                Block heldBlock = ((BlockItem) item.getItem()).getBlock();
                if (tileEntity instanceof FrameBlockTile) {
                    return block.attemptInsertBlock(world, item, state, pos,player, hand);
                }
            }

            //hammer is needed to remove the block from the frame - you can change it in the config
            if (item.getItem() == Registration.HAMMER.get() || (!BCModConfig.HAMMER_NEEDED.get() && player.isSneaking())) {
                if (!player.isCreative())
                    block.dropContainedBlock(world, pos);
                state = state.with(CONTAINS_BLOCK, Boolean.FALSE);
                world.setBlockState(pos, state, 2);
            }
        }
        return ActionResultType.SUCCESS;
    }

    /**
     * Auto-place behaviour for framed blocks, to allow the offhand block to be immediately applied to any newly placed
     * framed blocks in a single motion.
     *
     * @param block  the frameable block type in question
     * @param world  the world the block is in
     * @param pos    the position of the block
     * @param state  the blockstate of the specific block
     * @param placer the {@link LivingEntity} which placed the block
     * @param stack  the ItemStack this block was placed from.
     */
    public static void onPlace(IFrameableBlock block, World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if(!world.isRemote) {
            //ensure we were placed by a player
            if(placer != null & placer instanceof ServerPlayerEntity) {
                ServerPlayerEntity player = (ServerPlayerEntity) placer;
                ItemStack offhandStack = player.getHeldItemOffhand();
                if(FramedBlockHelper.isItemStackValidInsertCandidate(offhandStack)) {
                    //TODO: this will always subtract 1 and it shouldn't
                    //either that or we dont do this behaviour on doors.
                    block.attemptInsertBlock(world, offhandStack, state, pos, player, Hand.OFF_HAND);
                }
            }
        }
    }
}
