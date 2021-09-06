package mod.pianomanu.blockcarpentry.util;

import mod.pianomanu.blockcarpentry.tileentity.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.GrassBlock;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Util class for certain frame block things like light level and textures
 *
 * @author PianoManu
 * @version 1.11 08/20/21
 */
public class BlockAppearanceHelper {

    public static final int NO_OVERLAY_ID = 0;
    public static final int GRASS_OVERLAY_ID = 1;
    public static final int GRASS_LARGE_OVERLAY_ID = 2;
    public static final int SNOW_LARGE_OVERLAY_ID = 3;
    public static final int SNOW_OVERLAY_ID = 4;
    public static final int VINE_OVERLAY_ID = 5;
    public static final int STONE_BRICK_OVERLAY_ID = 6;
    public static final int BRICK_OVERLAY_ID = 7;
    public static final int SANDSTONE_OVERLAY_ID = 8;
    public static final int BOUNDARY_OVERLAY_ID = 9;
    public static final int CHISELED_STONE_OVERLAY_ID = 10;
    public static final int CRIMSON_OVERLAY_ID = 11;
    public static final int WARPED_OVERLAY_ID = 12;

    public static final OverlaySet NO_OVERLAY = new OverlaySet(NO_OVERLAY_ID);
    public static final OverlaySet GRASS_OVERLAY = new OverlaySet(GRASS_OVERLAY_ID, GRASS_LARGE_OVERLAY_ID);
    public static final OverlaySet SNOW_OVERLAY = new OverlaySet(SNOW_OVERLAY_ID, SNOW_LARGE_OVERLAY_ID);
    public static final OverlaySet VINE_OVERLAY = new OverlaySet(VINE_OVERLAY_ID);
    public static final OverlaySet GUNPOWDER_OVERLAY = new OverlaySet(STONE_BRICK_OVERLAY_ID, BRICK_OVERLAY_ID, SANDSTONE_OVERLAY_ID, BOUNDARY_OVERLAY_ID, CHISELED_STONE_OVERLAY_ID);
    public static final OverlaySet CRIMSON_OVERLAY = new OverlaySet(CRIMSON_OVERLAY_ID);
    public static final OverlaySet WARPED_OVERLAY = new OverlaySet(WARPED_OVERLAY_ID);

    public static void setGlassColor(World world, BlockPos pos, PlayerEntity player, Hand hand) {
        if (BlockSavingHelper.isDyeItem(player.getHeldItem(hand).getItem())) {
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof ISupportsColor) {
                ISupportsColor fte = (ISupportsColor) tileEntity;
                DyeColor itemCol = DyeColor.getColor(player.getHeldItem(hand));
                if(itemCol != null) {
                    fte.setColor(itemCol);
                }
            }
        }
    }

    //TODO: come back and repair this
    /*
    public static void setWoolColor(World world, BlockPos pos, PlayerEntity player, Hand hand) {
        if (BlockSavingHelper.isDyeItem(player.getHeldItem(hand).getItem())) {
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof BedFrameTile) {
                BedFrameTile fte = (BedFrameTile) tileEntity;
                if (world.getBlockState(pos).get(BedFrameBlock.PART) == BedPart.FOOT) {
                    fte.setBlanketColor(dyeItemToInt(player.getHeldItem(hand).getItem()));
                }
                if (world.getBlockState(pos).get(BedFrameBlock.PART) == BedPart.HEAD) {
                    fte.setPillowColor(dyeItemToInt(player.getHeldItem(hand).getItem()));
                }
                //player.sendStatusMessage(new TranslationTextComponent("Glass Color: " + glassColorToString(fte.getGlassColor()-1)), true);
            }
        }
    }*/

    // preserved to keep a copy of the original slab behaviour somewhere
    /*
    @Deprecated
    public static void setOverlay(World world, BlockPos pos, PlayerEntity player, ItemStack itemStack) {
        if (itemStack.getItem().equals(Items.GRASS)) {
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof FrameBlockTile_OLD) {
                FrameBlockTile_OLD fte = (FrameBlockTile_OLD) tileEntity;
                if (fte.getOverlay() == 1) {
                    fte.setOverlay(2);
                    player.sendStatusMessage(new TranslationTextComponent("message.blockcarpentry.grass_overlay_large"), true);
                } else {
                    fte.setOverlay(1);
                    player.sendStatusMessage(new TranslationTextComponent("message.blockcarpentry.grass_overlay"), true);
                }
            }
            if (tileEntity instanceof TwoBlocksFrameBlockTile) {
                TwoBlocksFrameBlockTile fte = (TwoBlocksFrameBlockTile) tileEntity;
                BlockState state = world.getBlockState(pos);
                if (!state.get(SixWaySlabFrameBlock.DOUBLE_SLAB)) {
                    if (fte.getOverlay_1() == 1) {
                        fte.setOverlay_1(2);
                        player.sendStatusMessage(new TranslationTextComponent("message.blockcarpentry.grass_overlay_large"), true);
                    } else {
                        fte.setOverlay_1(1);
                        player.sendStatusMessage(new TranslationTextComponent("message.blockcarpentry.grass_overlay"), true);
                    }
                } else {
                    if (fte.getOverlay_2() == 1) {
                        fte.setOverlay_2(2);
                        player.sendStatusMessage(new TranslationTextComponent("message.blockcarpentry.grass_overlay_large"), true);
                    } else {
                        fte.setOverlay_2(1);
                        player.sendStatusMessage(new TranslationTextComponent("message.blockcarpentry.grass_overlay"), true);
                    }
                }
            }
        }
        if (itemStack.getItem().equals(Items.SNOWBALL)) {
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof FrameBlockTile_OLD) {
                FrameBlockTile_OLD fte = (FrameBlockTile_OLD) tileEntity;
                if (fte.getOverlay() == 3) {
                    fte.setOverlay(4);
                    player.sendStatusMessage(new TranslationTextComponent("message.blockcarpentry.snow_overlay_small"), true);
                } else {
                    fte.setOverlay(3);
                    player.sendStatusMessage(new TranslationTextComponent("message.blockcarpentry.snow_overlay"), true);
                }
            }
            if (tileEntity instanceof TwoBlocksFrameBlockTile) {
                BlockState state = world.getBlockState(pos);
                if (!state.get(SixWaySlabFrameBlock.DOUBLE_SLAB)) {
                    TwoBlocksFrameBlockTile fte = (TwoBlocksFrameBlockTile) tileEntity;
                    if (fte.getOverlay_1() == 3) {
                        fte.setOverlay_1(4);
                        player.sendStatusMessage(new TranslationTextComponent("message.blockcarpentry.snow_overlay_small"), true);
                    } else {
                        fte.setOverlay_1(3);
                        player.sendStatusMessage(new TranslationTextComponent("message.blockcarpentry.snow_overlay"), true);
                    }
                } else {
                    TwoBlocksFrameBlockTile fte = (TwoBlocksFrameBlockTile) tileEntity;
                    if (fte.getOverlay_2() == 3) {
                        fte.setOverlay_2(4);
                        player.sendStatusMessage(new TranslationTextComponent("message.blockcarpentry.snow_overlay_small"), true);
                    } else {
                        fte.setOverlay_2(3);
                        player.sendStatusMessage(new TranslationTextComponent("message.blockcarpentry.snow_overlay"), true);
                    }
                }
            }
        }
        if (itemStack.getItem().equals(Items.VINE)) {
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof FrameBlockTile_OLD) {
                FrameBlockTile_OLD fte = (FrameBlockTile_OLD) tileEntity;
                fte.setOverlay(5);
                player.sendStatusMessage(new TranslationTextComponent("message.blockcarpentry.vine_overlay"), true);
            }
            if (tileEntity instanceof TwoBlocksFrameBlockTile) {
                BlockState state = world.getBlockState(pos);
                if (!state.get(SixWaySlabFrameBlock.DOUBLE_SLAB)) {
                    TwoBlocksFrameBlockTile fte = (TwoBlocksFrameBlockTile) tileEntity;
                    fte.setOverlay_1(5);
                    player.sendStatusMessage(new TranslationTextComponent("message.blockcarpentry.vine_overlay"), true);
                } else {
                    TwoBlocksFrameBlockTile fte = (TwoBlocksFrameBlockTile) tileEntity;
                    fte.setOverlay_2(5);
                    player.sendStatusMessage(new TranslationTextComponent("message.blockcarpentry.vine_overlay"), true);
                }
            }
        }
        if (itemStack.getItem().equals(Items.GUNPOWDER)) {
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof FrameBlockTile_OLD) {
                FrameBlockTile_OLD fte = (FrameBlockTile_OLD) tileEntity;
                if (fte.getOverlay() > 5 && fte.getOverlay() < 10) {
                    fte.setOverlay(fte.getOverlay() + 1);
                } else fte.setOverlay(6);
                player.sendStatusMessage(new TranslationTextComponent("message.blockcarpentry.special_overlay", (fte.getOverlay() - 5)), true);
            }
            if (tileEntity instanceof TwoBlocksFrameBlockTile) {
                BlockState state = world.getBlockState(pos);
                if (!state.get(SixWaySlabFrameBlock.DOUBLE_SLAB)) {
                    TwoBlocksFrameBlockTile fte = (TwoBlocksFrameBlockTile) tileEntity;
                    if (fte.getOverlay_1() > 5 && fte.getOverlay_1() < 10) {
                        fte.setOverlay_1(fte.getOverlay_1() + 1);
                    } else fte.setOverlay_1(6);
                    player.sendStatusMessage(new TranslationTextComponent("message.blockcarpentry.special_overlay", (fte.getOverlay_1() - 5)), true);
                } else {
                    TwoBlocksFrameBlockTile fte = (TwoBlocksFrameBlockTile) tileEntity;
                    if (fte.getOverlay_2() > 5 && fte.getOverlay_2() < 10) {
                        fte.setOverlay_2(fte.getOverlay_2() + 1);
                    } else fte.setOverlay_2(6);
                    player.sendStatusMessage(new TranslationTextComponent("message.blockcarpentry.special_overlay", (fte.getOverlay_2() - 5)), true);
                }
            }
        }
        if (itemStack.getItem().equals(Items.CRIMSON_ROOTS)) {
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof FrameBlockTile_OLD) {
                FrameBlockTile_OLD fte = (FrameBlockTile_OLD) tileEntity;
                fte.setOverlay(11);
                player.sendStatusMessage(new TranslationTextComponent("message.blockcarpentry.crimson_overlay"), true);
            }
            if (tileEntity instanceof TwoBlocksFrameBlockTile) {
                BlockState state = world.getBlockState(pos);
                if (!state.get(SixWaySlabFrameBlock.DOUBLE_SLAB)) {
                    TwoBlocksFrameBlockTile fte = (TwoBlocksFrameBlockTile) tileEntity;
                    fte.setOverlay_1(11);
                    player.sendStatusMessage(new TranslationTextComponent("message.blockcarpentry.crimson_overlay"), true);
                } else {
                    TwoBlocksFrameBlockTile fte = (TwoBlocksFrameBlockTile) tileEntity;
                    fte.setOverlay_2(11);
                    player.sendStatusMessage(new TranslationTextComponent("message.blockcarpentry.crimson_overlay"), true);
                }
            }
        }
        if (itemStack.getItem().equals(Items.WARPED_ROOTS)) {
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof FrameBlockTile_OLD) {
                FrameBlockTile_OLD fte = (FrameBlockTile_OLD) tileEntity;
                fte.setOverlay(12);
                player.sendStatusMessage(new TranslationTextComponent("message.blockcarpentry.warped_overlay"), true);
            }
            if (tileEntity instanceof TwoBlocksFrameBlockTile) {
                BlockState state = world.getBlockState(pos);
                if (!state.get(SixWaySlabFrameBlock.DOUBLE_SLAB)) {
                    TwoBlocksFrameBlockTile fte = (TwoBlocksFrameBlockTile) tileEntity;
                    fte.setOverlay_1(12);
                    player.sendStatusMessage(new TranslationTextComponent("message.blockcarpentry.warped_overlay"), true);
                } else {
                    TwoBlocksFrameBlockTile fte = (TwoBlocksFrameBlockTile) tileEntity;
                    fte.setOverlay_2(12);
                    player.sendStatusMessage(new TranslationTextComponent("message.blockcarpentry.warped_overlay"), true);
                }
            }
        }
    }
    */
    public static int setTintIndex(BlockState state) {
        Block b = state.getBlock();
        if (b instanceof GrassBlock || b instanceof LeavesBlock) {
            return 1;
        }
        return -1;
    }
}

/**
 * An immutable collection of overlay IDs, used to allow cycling between overlays with shared logic;
 */
class OverlaySet {
    private ArrayList<Integer> set;

    public OverlaySet(Integer... overlays) {
        set = new ArrayList<Integer>(Arrays.asList(overlays));
    }

    public int first() {
        return set.get(0);
    }

    /**
     * Finds the next overlay in this set.
     * Loops back around to the start if next is called on the last overlay
     * Will return the first overlay if current doesn't exist within the set
     * @param current
     * @return
     */
    public int next(Integer current) {
        int index = set.indexOf(current);
        if(index == -1 || index+1 >= set.size()) {
            return set.get(0);
        }
        return set.get(index+1);
    }

    public int find(Integer id) {
        return set.indexOf(id);
    }
}
