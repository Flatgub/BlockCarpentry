package mod.pianomanu.blockcarpentry.util;

import mod.pianomanu.blockcarpentry.BlockCarpentryMain;
import mod.pianomanu.blockcarpentry.setup.Registration;
import mod.pianomanu.blockcarpentry.setup.config.BCModConfig;
import mod.pianomanu.blockcarpentry.tileentity.FrameBlockTile;
import mod.pianomanu.blockcarpentry.tileentity.IFrameEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.Objects;

import static mod.pianomanu.blockcarpentry.util.BCBlockStateProperties.LIGHT_LEVEL;

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
     * Generic basic right click behaviour for all framed blocks.
     * Attempts to use apply any tools to the frame and if no tool interactions are performed then
     * it attempts to apply the in hand block as a mimic.
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

            //attempt hammer/wrench/etc interaction
            if(attemptToolUse(block, state, world, pos, player, hand, trace).isSuccess()) {
                return ActionResultType.SUCCESS;
            }

            //in the absence fo a tool, attempt apply interaction
            return attemptApplyMimic(block, state, world, pos, player, hand, trace);
        }
        return ActionResultType.SUCCESS;
    }


    /**
     * Shared behaviour for attempting to apply the in hand block as a mimic texture to a frame
     *
     * @param block  the frameable block type in question
     * @param state  the blockstate of the specific block
     * @param world  the world the block is in
     * @param pos    the position of the block
     * @param player the player interacting with the block
     * @param hand   the hand the player is using
     * @param trace  the raycast from the player's interaction
     * @return PASS by default or SUCCESS if an item was applied as a mimic
     */
    public static ActionResultType attemptApplyMimic(IFrameableBlock block, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult trace) {
        ItemStack item = player.getHeldItem(hand);
        if (!world.isRemote) {
            //attempt to insert the ItemStack
            if(FramedBlockHelper.isItemStackValidInsertCandidate(item)) {
                if(state.get(CONTAINS_BLOCK)) { return ActionResultType.PASS; }

                TileEntity tileEntity = world.getTileEntity(pos);
                if (tileEntity instanceof IFrameEntity) {
                    return block.attemptInsertBlock(world, item, state, pos,player, hand);
                }
            }
        }
        return ActionResultType.PASS;
    }

    /**
     *  Shared behaviour for all tool based interactions, such as hammer, wrench, chisel or glowstone.
     *
     * @param block  the frameable block type in question
     * @param state  the blockstate of the specific block
     * @param world  the world the block is in
     * @param pos    the position of the block
     * @param player the player interacting with the block
     * @param hand   the hand the player is using
     * @param trace  the raycast from the player's interaction
     * @return PASS by default or SUCCESS if a tool interaction was performed
     */
    public static ActionResultType attemptToolUse(IFrameableBlock block, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult trace) {
        if (!world.isRemote) {
            ItemStack item = player.getHeldItem(hand);
            Item itemType = item.getItem();

            if(state.get(CONTAINS_BLOCK)) {
                // Attempt hammer interaction, either via hammer or by shift rightclick if disabled in the config.
                // removes the current mimic texture
                if (item.getItem() == Registration.HAMMER.get() || (!BCModConfig.HAMMER_NEEDED.get() && player.isSneaking())) {
                    if (!player.isCreative())
                        block.dropContainedBlock(world, pos);
                    state = state.with(CONTAINS_BLOCK, Boolean.FALSE);
                    world.setBlockState(pos, state, Constants.BlockFlags.BLOCK_UPDATE);
                    return ActionResultType.SUCCESS;
                }

                // Attempt light level interaction
                // increases the light level of the frame by consuming coal, charcoal or glowstone dust
                if(itemType == Items.GLOWSTONE_DUST || itemType == Items.COAL || itemType == Items.CHARCOAL) {
                    int lightLevel = state.get(LIGHT_LEVEL);
                    int maxPossible = (itemType == Items.GLOWSTONE_DUST) ? 13 : 15;
                    int increase = (itemType == Items.GLOWSTONE_DUST) ? 3 : 1;

                    if(lightLevel < maxPossible) {
                        lightLevel = Math.min(lightLevel + increase, maxPossible);
                        world.setBlockState(pos, state.with(LIGHT_LEVEL, lightLevel));
                        if(!player.isCreative()) {
                            item.setCount(item.getCount() - 1);
                        }
                        player.sendStatusMessage(new TranslationTextComponent("message.blockcarpentry.light_level", lightLevel), true);
                        return ActionResultType.SUCCESS;
                    }
                    else {
                        player.sendStatusMessage(new TranslationTextComponent("message.blockcarpentry.max_light_level", itemType.getName()), true);
                    }
                }

                //TODO: MISSING OTHER INTERACTIONS
                //BlockAppearanceHelper.setTexture(item, state, world, player, pos);
                //BlockAppearanceHelper.setDesign(world, pos, player, item);
                //BlockAppearanceHelper.setDesignTexture(world, pos, player, item);
                //BlockAppearanceHelper.setOverlay(world, pos, player, item);
                //BlockAppearanceHelper.setRotation(world, pos, player, item);

            }
        }
        return ActionResultType.PASS;
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
                    block.attemptInsertBlock(world, offhandStack, state, pos, player, Hand.OFF_HAND);
                }
            }
        }
    }
}
