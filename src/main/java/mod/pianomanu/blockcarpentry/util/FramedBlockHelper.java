package mod.pianomanu.blockcarpentry.util;

import mod.pianomanu.blockcarpentry.BlockCarpentryMain;
import mod.pianomanu.blockcarpentry.block.SixWaySlabFrameBlock;
import mod.pianomanu.blockcarpentry.setup.Registration;
import mod.pianomanu.blockcarpentry.setup.config.BCModConfig;
import mod.pianomanu.blockcarpentry.tileentity.*;
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
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.Objects;

import static mod.pianomanu.blockcarpentry.util.BCBlockStateProperties.CONTAINS_BLOCK;
import static mod.pianomanu.blockcarpentry.util.BCBlockStateProperties.LIGHT_LEVEL;

/**
 * Collection of static shared code for framed blocks that doesn't belong in {@link IFrameableBlock}
 */
public class FramedBlockHelper {
    public static final BooleanProperty CONTAINS_BLOCK = BCBlockStateProperties.CONTAINS_BLOCK;
    public static final IntegerProperty LIGHT_LEVEL = BCBlockStateProperties.LIGHT_LEVEL;
    public static final ITag.INamedTag<Item> OVERLAY_TAG =  ItemTags.makeWrapperTag("blockcarpentry:applies_overlay");

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
            TileEntity tileEntity = world.getTileEntity(pos);

            if(state.get(CONTAINS_BLOCK)) {
                // Attempt hammer interaction, either via hammer or by shift rightclick if disabled in the config.
                // removes the current mimic texture
                if (itemType == Registration.HAMMER.get() || (!BCModConfig.HAMMER_NEEDED.get() && player.isSneaking())) {
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

                // Attempt texture wrench interaction
                // on frame blocks "swaps" which texture is showing on all sides
                // on illusion blocks "rotates" the block to change which face each texture appears on
                if (itemType == Registration.TEXTURE_WRENCH.get() && !player.isSneaking() ) {

                    // SWAP TEXTURE for framed blocks
                    if (mod.pianomanu.blockcarpentry.util.Tags.isFrameBlock(state.getBlock())) {
                        if(tileEntity instanceof IFrameEntity) {
                            IFrameEntity fte = (IFrameEntity) tileEntity;
                            if (fte.getTexture() < 5) { //six sides possible
                                fte.setTexture(fte.getTexture() + 1);
                            } else {
                                fte.setTexture(0);
                            }
                            //TODO: add a sound here
                            player.sendStatusMessage(new TranslationTextComponent("message.blockcarpentry.texture", fte.getTexture()), true);
                            return ActionResultType.SUCCESS;
                        }

                        //TODO: come back to this when slabs are redone
                        if (tileEntity instanceof TwoBlocksFrameBlockTile) {
                            TwoBlocksFrameBlockTile fte = (TwoBlocksFrameBlockTile) tileEntity;
                            if (!state.get(SixWaySlabFrameBlock.DOUBLE_SLAB)) {
                                if (fte.getTexture_1() < 5) {
                                    fte.setTexture_1(fte.getTexture_1() + 1);
                                } else {
                                    fte.setTexture_1(0);
                                }
                                player.sendStatusMessage(new TranslationTextComponent("message.blockcarpentry.texture", fte.getTexture_1()), true);
                                return ActionResultType.SUCCESS;
                            } else {
                                if (fte.getTexture_2() < 5) {
                                    fte.setTexture_2(fte.getTexture_2() + 1);
                                } else {
                                    fte.setTexture_2(0);
                                }
                                player.sendStatusMessage(new TranslationTextComponent("message.blockcarpentry.texture", fte.getTexture_2()), true);
                                return ActionResultType.SUCCESS;
                            }
                        }
                    }

                    // ROTATE SIDES for illusion blocks
                    if (mod.pianomanu.blockcarpentry.util.Tags.isIllusionBlock(state.getBlock())) {
                        if (tileEntity instanceof IFrameEntity) {
                            IFrameEntity fte = (IFrameEntity) tileEntity;
                            if (fte.getRotation() < 7) {
                                fte.setRotation(fte.getRotation() + 1);
                            } else {
                                fte.setRotation(0);
                            }
                            //player.sendMessage(new TranslationTextComponent("message.frame.design_texture"));
                            player.sendStatusMessage(new TranslationTextComponent("message.blockcarpentry.rotation", fte.getRotation()), true);
                        }

                        //TODO: come back to this when slabs are redone
                        if (tileEntity instanceof TwoBlocksFrameBlockTile) {
                            TwoBlocksFrameBlockTile fte = (TwoBlocksFrameBlockTile) tileEntity;
                            if (!state.get(SixWaySlabFrameBlock.DOUBLE_SLAB)) {
                                if (fte.getRotation_1() < 7) {
                                    fte.setRotation_1(fte.getRotation_1() + 1);
                                } else {
                                    fte.setRotation_1(0);
                                }
                                player.sendStatusMessage(new TranslationTextComponent("message.blockcarpentry.rotation", fte.getRotation_1()), true);
                                return ActionResultType.SUCCESS;
                            } else {
                                if (fte.getRotation_2() < 7) {
                                    fte.setRotation_2(fte.getRotation_2() + 1);
                                } else {
                                    fte.setRotation_2(0);
                                }
                                player.sendStatusMessage(new TranslationTextComponent("message.blockcarpentry.rotation", fte.getRotation_2()), true);
                                return ActionResultType.SUCCESS;
                            }
                        }
                    }
                }

                // Attempt chisel interaction
                // changes the model design on certain kinds of block
                if (itemType == Registration.CHISEL.get() && !player.isSneaking()) {
                    if (tileEntity instanceof IFrameEntity) {
                        IFrameEntity fte = (IFrameEntity) tileEntity;
                        if (fte.getDesign() < fte.getMaxDesigns()) {
                            fte.setDesign(fte.getDesign() + 1);
                        } else {
                            fte.setDesign(0);
                        }
                        player.sendStatusMessage(new TranslationTextComponent("message.blockcarpentry.design", fte.getDesign()), true);
                        return ActionResultType.SUCCESS;
                    }

                    //TODO: come back to this when slabs are redone
                    if (tileEntity instanceof TwoBlocksFrameBlockTile) {
                        TwoBlocksFrameBlockTile fte = (TwoBlocksFrameBlockTile) tileEntity;
                        if (!state.get(SixWaySlabFrameBlock.DOUBLE_SLAB)) {
                            if (fte.getDesign_1() < fte.maxDesigns) {
                                fte.setDesign_1(fte.getDesign_1() + 1);
                            } else {
                                fte.setDesign_1(0);
                            }
                            player.sendStatusMessage(new TranslationTextComponent("message.blockcarpentry.design", fte.getDesign_1()), true);
                            return ActionResultType.SUCCESS;
                        } else {
                            if (fte.getDesign_2() < fte.maxDesigns) {
                                fte.setDesign_2(fte.getDesign_2() + 1);
                            } else {
                                fte.setDesign_2(0);
                            }
                            player.sendStatusMessage(new TranslationTextComponent("message.blockcarpentry.design", fte.getDesign_2()), true);
                            return ActionResultType.SUCCESS;
                        }
                    }

                }

                // Attempt paintbrush interaction
                // changes the colour of details on certain kinds of block design
                if (itemType == Registration.PAINTBRUSH.get() && !player.isSneaking()) {
                    if (tileEntity instanceof IFrameEntity) {
                        IFrameEntity fte = (IFrameEntity) tileEntity;
                        if (fte.getDesignTexture() < fte.getMaxDesignTextures()) {
                            fte.setDesignTexture(fte.getDesignTexture() + 1);
                        } else {
                            fte.setDesignTexture(0);
                        }
                        //player.sendMessage(new TranslationTextComponent("message.frame.design_texture"));
                        player.sendStatusMessage(new TranslationTextComponent("message.blockcarpentry.design_texture", fte.getDesignTexture()), true);
                        return ActionResultType.SUCCESS;
                    }
                }

                // Attempt overlay interaction
                // applies an overlay decal depending on the item used
                // TODO: none of these have slab support, because it was too much code to duplicate
                //  - come back once slabs are redone and add support here
                if (itemType.isIn(OVERLAY_TAG)) {
                    int newOverlay = -1;
                    TranslationTextComponent message = null;

                    if (tileEntity instanceof IFrameEntity) {
                        IFrameEntity fte = (IFrameEntity) tileEntity;
                        int curOverlay = fte.getOverlay();
                        if(itemType.equals(Items.GRASS)) {
                            newOverlay = BlockAppearanceHelper.GRASS_OVERLAY.next(curOverlay);
                            message = new TranslationTextComponent("message.blockcarpentry.grass_overlay", BlockAppearanceHelper.GRASS_OVERLAY.find(newOverlay)+1);
                        }
                        if(itemType.equals(Items.SNOWBALL)) {
                            newOverlay = BlockAppearanceHelper.SNOW_OVERLAY.next(curOverlay);
                            message = new TranslationTextComponent("message.blockcarpentry.snow_overlay", BlockAppearanceHelper.SNOW_OVERLAY.find(newOverlay)+1);
                        }
                        if(itemType.equals(Items.VINE)) {
                            newOverlay = BlockAppearanceHelper.VINE_OVERLAY.first();
                            message = new TranslationTextComponent("message.blockcarpentry.vine_overlay");
                        }
                        if(itemType.equals(Items.GUNPOWDER)) {
                            newOverlay = BlockAppearanceHelper.GUNPOWDER_OVERLAY.next(curOverlay);
                            message = new TranslationTextComponent("message.blockcarpentry.special_overlay", BlockAppearanceHelper.GUNPOWDER_OVERLAY.find(newOverlay)+1);
                        }
                        if(itemType.equals(Items.CRIMSON_ROOTS)) {
                            newOverlay = BlockAppearanceHelper.CRIMSON_OVERLAY.first();
                            message = new TranslationTextComponent("message.blockcarpentry.crimson_overlay");
                        }
                        if(itemType.equals(Items.WARPED_ROOTS)) {
                            newOverlay = BlockAppearanceHelper.WARPED_OVERLAY.first();
                            message = new TranslationTextComponent("message.blockcarpentry.warped_overlay");
                        }

                        //if an item was matched
                        if(newOverlay != -1) {
                            fte.setOverlay(newOverlay);
                            player.sendStatusMessage(message, true);
                            return ActionResultType.SUCCESS;
                        }
                    }
                }
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
