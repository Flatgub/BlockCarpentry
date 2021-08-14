package mod.pianomanu.blockcarpentry.util;

import mod.pianomanu.blockcarpentry.block.BedFrameBlock;
import mod.pianomanu.blockcarpentry.block.SixWaySlabFrameBlock;
import mod.pianomanu.blockcarpentry.setup.Registration;
import mod.pianomanu.blockcarpentry.tileentity.BedFrameTile;
import mod.pianomanu.blockcarpentry.tileentity.ChestFrameBlockEntity;
import mod.pianomanu.blockcarpentry.tileentity.FrameBlockTile;
import mod.pianomanu.blockcarpentry.tileentity.TwoBlocksFrameBlockTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.GrassBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.state.properties.BedPart;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.Tags;

import java.util.ArrayList;
import java.util.List;

import static mod.pianomanu.blockcarpentry.util.BCBlockStateProperties.CONTAINS_BLOCK;
import static mod.pianomanu.blockcarpentry.util.BCBlockStateProperties.LIGHT_LEVEL;

/**
 * Util class for certain frame block things like light level and textures
 *
 * @author PianoManu
 * @version 1.10 06/05/21
 */
public class BlockAppearanceHelper {
    public static int setLightLevel(ItemStack item, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand) {
        if (item.getItem() == Items.GLOWSTONE_DUST && state.get(LIGHT_LEVEL) < 13) {
            int count = player.getHeldItem(hand).getCount();
            world.setBlockState(pos, state.with(LIGHT_LEVEL, state.getBlock().getLightValue(state, world, pos) + 3));
            player.getHeldItem(hand).setCount(count - 1);
            player.sendStatusMessage(new TranslationTextComponent("message.blockcarpentry.light_level", (state.get(LIGHT_LEVEL) + 3)), true);
        }
        if ((item.getItem() == Items.COAL || item.getItem() == Items.CHARCOAL) && state.get(LIGHT_LEVEL) < 15) {
            int count = player.getHeldItem(hand).getCount();
            world.setBlockState(pos, state.with(LIGHT_LEVEL, state.getBlock().getLightValue(state, world, pos) + 1));
            player.getHeldItem(hand).setCount(count - 1);
            player.sendStatusMessage(new TranslationTextComponent("message.blockcarpentry.light_level", (state.get(LIGHT_LEVEL) + 1)), true);
        }
        if (item.getItem() == Items.GLOWSTONE_DUST && state.get(LIGHT_LEVEL) >= 13) {
            player.sendStatusMessage(new TranslationTextComponent("message.blockcarpentry.light_level", state.get(LIGHT_LEVEL)), true);
        }
        if ((item.getItem() == Items.COAL || item.getItem() == Items.CHARCOAL) && state.get(LIGHT_LEVEL) == 15) {
            player.sendStatusMessage(new TranslationTextComponent("message.blockcarpentry.light_level", state.get(LIGHT_LEVEL)), true);
        }
        return state.get(LIGHT_LEVEL);
    }

    public static void setTexture(ItemStack item, BlockState state, World world, PlayerEntity player, BlockPos pos) {
        if (item.getItem() == Registration.TEXTURE_WRENCH.get() && !player.isSneaking() && state.get(CONTAINS_BLOCK) && mod.pianomanu.blockcarpentry.util.Tags.isFrameBlock(state.getBlock())) {
            BlockEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof FrameBlockTile) {
                FrameBlockTile fte = (FrameBlockTile) tileEntity;
                if (fte.getTexture() < 5) { //six sides possible
                    fte.setTexture(fte.getTexture() + 1);
                } else {
                    fte.setTexture(0);
                }
                player.sendStatusMessage(new TranslationTextComponent("message.blockcarpentry.texture", fte.getTexture()), true);
            }
            if (tileEntity instanceof BedFrameTile) {
                BedFrameTile fte = (BedFrameTile) tileEntity;
                if (fte.getTexture() < 5) { //six sides possible
                    fte.setTexture(fte.getTexture() + 1);
                } else {
                    fte.setTexture(0);
                }
                player.sendStatusMessage(new TranslationTextComponent("message.blockcarpentry.texture", fte.getTexture()), true);
            }
            if (tileEntity instanceof ChestFrameBlockEntity) {
                ChestFrameBlockEntity fte = (ChestFrameBlockEntity) tileEntity;
                if (fte.getTexture() < 5) { //six sides possible
                    fte.setTexture(fte.getTexture() + 1);
                } else {
                    fte.setTexture(0);
                }
                player.sendStatusMessage(new TranslationTextComponent("message.blockcarpentry.texture", fte.getTexture()), true);
            }
            if (tileEntity instanceof TwoBlocksFrameBlockTile) {
                TwoBlocksFrameBlockTile fte = (TwoBlocksFrameBlockTile) tileEntity;
                if (!state.get(SixWaySlabFrameBlock.DOUBLE_SLAB)) {
                    if (fte.getTexture_1() < 5) {
                        fte.setTexture_1(fte.getTexture_1() + 1);
                    } else {
                        fte.setTexture_1(0);
                    }
                    player.sendStatusMessage(new TranslationTextComponent("message.blockcarpentry.texture", fte.getTexture_1()), true);
                } else {
                    if (fte.getTexture_2() < 5) {
                        fte.setTexture_2(fte.getTexture_2() + 1);
                    } else {
                        fte.setTexture_2(0);
                    }
                    player.sendStatusMessage(new TranslationTextComponent("message.blockcarpentry.texture", fte.getTexture_2()), true);
                }
            }
        }
    }

    public static void setDesign(World world, BlockPos pos, PlayerEntity player, ItemStack item) {
        if (item.getItem() == Registration.CHISEL.get() && !player.isSneaking()) {
            BlockEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof FrameBlockTile) {
                FrameBlockTile fte = (FrameBlockTile) tileEntity;
                if (fte.getDesign() < fte.maxDesigns) {
                    fte.setDesign(fte.getDesign() + 1);
                } else {
                    fte.setDesign(0);
                }
                player.sendStatusMessage(new TranslationTextComponent("message.blockcarpentry.design", fte.getDesign()), true);
            }
            if (tileEntity instanceof BedFrameTile) {
                BedFrameTile fte = (BedFrameTile) tileEntity;
                if (fte.getDesign() < fte.maxDesigns) {
                    fte.setDesign(fte.getDesign() + 1);
                } else {
                    fte.setDesign(0);
                }
                player.sendStatusMessage(new TranslationTextComponent("message.blockcarpentry.design", fte.getDesign()), true);
            }
            if (tileEntity instanceof ChestFrameBlockEntity) {
                ChestFrameBlockEntity fte = (ChestFrameBlockEntity) tileEntity;
                if (fte.getDesign() < fte.maxDesigns) {
                    fte.setDesign(fte.getDesign() + 1);
                } else {
                    fte.setDesign(0);
                }
                player.sendStatusMessage(new TranslationTextComponent("message.blockcarpentry.design", fte.getDesign()), true);
            }
            if (tileEntity instanceof TwoBlocksFrameBlockTile) {
                TwoBlocksFrameBlockTile fte = (TwoBlocksFrameBlockTile) tileEntity;
                BlockState state = world.getBlockState(pos);
                if (!state.get(SixWaySlabFrameBlock.DOUBLE_SLAB)) {
                    if (fte.getDesign_1() < fte.maxDesigns) {
                        fte.setDesign_1(fte.getDesign_1() + 1);
                    } else {
                        fte.setDesign_1(0);
                    }
                    player.sendStatusMessage(new TranslationTextComponent("message.blockcarpentry.design", fte.getDesign_1()), true);
                } else {
                    if (fte.getDesign_2() < fte.maxDesigns) {
                        fte.setDesign_2(fte.getDesign_2() + 1);
                    } else {
                        fte.setDesign_2(0);
                    }
                    player.sendStatusMessage(new TranslationTextComponent("message.blockcarpentry.design", fte.getDesign_2()), true);
                }
            }
        }
    }

    public static void setDesignTexture(World world, BlockPos pos, PlayerEntity player, ItemStack item) {
        if (item.getItem() == Registration.PAINTBRUSH.get() && !player.isSneaking()) {
            BlockEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof FrameBlockTile) {
                FrameBlockTile fte = (FrameBlockTile) tileEntity;
                if (fte.getDesignTexture() < fte.maxDesignTextures) {
                    fte.setDesignTexture(fte.getDesignTexture() + 1);
                } else {
                    fte.setDesignTexture(0);
                }
                //player.sendMessage(new TranslationTextComponent("message.frame.design_texture"));
                player.sendStatusMessage(new TranslationTextComponent("message.blockcarpentry.design_texture", fte.getDesignTexture()), true);
            }
            if (tileEntity instanceof BedFrameTile) {
                BedFrameTile fte = (BedFrameTile) tileEntity;
                if (fte.getDesignTexture() < 7) {
                    fte.setDesignTexture(fte.getDesignTexture() + 1);
                } else {
                    fte.setDesignTexture(0);
                }
                //player.sendMessage(new TranslationTextComponent("message.frame.design_texture"));
                player.sendStatusMessage(new TranslationTextComponent("message.blockcarpentry.design_texture", fte.getDesignTexture()), true);
            }
            if (tileEntity instanceof ChestFrameBlockEntity) {
                ChestFrameBlockEntity fte = (ChestFrameBlockEntity) tileEntity;
                if (fte.getDesignTexture() < fte.maxDesignTextures) {
                    fte.setDesignTexture(fte.getDesignTexture() + 1);
                } else {
                    fte.setDesignTexture(0);
                }
                //player.sendMessage(new TranslationTextComponent("message.frame.design_texture"));
                player.sendStatusMessage(new TranslationTextComponent("message.blockcarpentry.design_texture", fte.getDesignTexture()), true);
            }
        }
    }

    public static void setGlassColor(World world, BlockPos pos, PlayerEntity player, Hand hand) {
        if (BlockSavingHelper.isDyeItem(player.getHeldItem(hand).getItem())) {
            BlockEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof FrameBlockTile) {
                FrameBlockTile fte = (FrameBlockTile) tileEntity;
                fte.setGlassColor(dyeItemToInt(player.getHeldItem(hand).getItem()) + 1); //plus 1, because 0 is undyed glass
                //player.sendStatusMessage(new TranslationTextComponent("Glass Color: " + glassColorToString(fte.getGlassColor()-1)), true);
            }
        }
    }

    public static void setWoolColor(World world, BlockPos pos, PlayerEntity player, Hand hand) {
        if (BlockSavingHelper.isDyeItem(player.getHeldItem(hand).getItem())) {
            BlockEntity tileEntity = world.getBlockEntity(pos);
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
    }

    //reminder to myself: DO NOT USE, CAUSES SERVER CRASHES, fix or remove
    private static String glassColorToString(int glassColor) {
        List<String> colors = new ArrayList<>();
        for (Item item : Tags.Items.DYES.getAllElements()) {
            colors.add(item.getName().getString());
        }
        return colors.get(glassColor);
    }

    public static Integer dyeItemToInt(Item item) {
        List<Item> colors = new ArrayList<>(BlockSavingHelper.getDyeItems());
        if (colors.contains(item)) {
            return colors.indexOf(item);
        }
        return 0;
    }

    public static void setOverlay(World world, BlockPos pos, PlayerEntity player, ItemStack itemStack) {
        if (itemStack.getItem().equals(Items.GRASS)) {
            BlockEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof FrameBlockTile) {
                FrameBlockTile fte = (FrameBlockTile) tileEntity;
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
            BlockEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof FrameBlockTile) {
                FrameBlockTile fte = (FrameBlockTile) tileEntity;
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
            BlockEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof FrameBlockTile) {
                FrameBlockTile fte = (FrameBlockTile) tileEntity;
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
            BlockEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof FrameBlockTile) {
                FrameBlockTile fte = (FrameBlockTile) tileEntity;
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
    }

    public static int setTintIndex(BlockState state) {
        Block b = state.getBlock();
        if (b instanceof GrassBlock || b instanceof LeavesBlock) {
            return 1;
        }
        return -1;
    }

    public static void setRotation(World world, BlockPos pos, PlayerEntity player, ItemStack itemStack) {
        if (itemStack.getItem() == Registration.TEXTURE_WRENCH.get() && !player.isSneaking() && mod.pianomanu.blockcarpentry.util.Tags.isIllusionBlock(world.getBlockState(pos).getBlock())) {
            BlockEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof FrameBlockTile) {
                FrameBlockTile fte = (FrameBlockTile) tileEntity;
                if (fte.getRotation() < 7) {
                    fte.setRotation(fte.getRotation() + 1);
                } else {
                    fte.setRotation(0);
                }
                //player.sendMessage(new TranslationTextComponent("message.frame.design_texture"));
                player.sendStatusMessage(new TranslationTextComponent("message.blockcarpentry.rotation", fte.getRotation()), true);
            }
            if (tileEntity instanceof BedFrameTile) {
                BedFrameTile fte = (BedFrameTile) tileEntity;
                if (fte.getRotation() < 7) {
                    fte.setRotation(fte.getRotation() + 1);
                } else {
                    fte.setRotation(0);
                }
                //player.sendMessage(new TranslationTextComponent("message.frame.design_texture"));
                player.sendStatusMessage(new TranslationTextComponent("message.blockcarpentry.rotation", fte.getRotation()), true);
            }
            if (tileEntity instanceof ChestFrameBlockEntity) {
                ChestFrameBlockEntity fte = (ChestFrameBlockEntity) tileEntity;
                if (fte.getRotation() < 7) {
                    fte.setRotation(fte.getRotation() + 1);
                } else {
                    fte.setRotation(0);
                }
                //player.sendMessage(new TranslationTextComponent("message.frame.design_texture"));
                player.sendStatusMessage(new TranslationTextComponent("message.blockcarpentry.rotation", fte.getRotation()), true);
            }
            if (tileEntity instanceof TwoBlocksFrameBlockTile) {
                TwoBlocksFrameBlockTile fte = (TwoBlocksFrameBlockTile) tileEntity;
                BlockState state = world.getBlockState(pos);
                if (!state.get(SixWaySlabFrameBlock.DOUBLE_SLAB)) {
                    if (fte.getRotation_1() < 7) {
                        fte.setRotation_1(fte.getRotation_1() + 1);
                    } else {
                        fte.setRotation_1(0);
                    }
                    player.sendStatusMessage(new TranslationTextComponent("message.blockcarpentry.rotation", fte.getRotation_1()), true);
                } else {
                    if (fte.getRotation_2() < 7) {
                        fte.setRotation_2(fte.getRotation_2() + 1);
                    } else {
                        fte.setRotation_2(0);
                    }
                    player.sendStatusMessage(new TranslationTextComponent("message.blockcarpentry.rotation", fte.getRotation_2()), true);
                }
            }
        }
    }
}
//========SOLI DEO GLORIA========//