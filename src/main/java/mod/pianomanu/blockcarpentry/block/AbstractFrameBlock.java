package mod.pianomanu.blockcarpentry.block;

import mod.pianomanu.blockcarpentry.BlockCarpentryMain;
import mod.pianomanu.blockcarpentry.setup.Registration;
import mod.pianomanu.blockcarpentry.setup.config.BCModConfig;
import mod.pianomanu.blockcarpentry.tileentity.FrameBlockTile;
import mod.pianomanu.blockcarpentry.util.BCBlockStateProperties;
import mod.pianomanu.blockcarpentry.util.BlockAppearanceHelper;
import mod.pianomanu.blockcarpentry.util.BlockSavingHelper;
import mod.pianomanu.blockcarpentry.util.FramedBlockHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import org.apache.logging.log4j.core.jmx.Server;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;

/**
 * Basic class for frame blocks (WIP)
 * Everything here is just for test purposes and subject to change
 *
 * @author PianoManu
 * @version 1.0 06/06/21
 */
public abstract class AbstractFrameBlock extends Block {
    public static final BooleanProperty CONTAINS_BLOCK = BCBlockStateProperties.CONTAINS_BLOCK;
    public static final IntegerProperty LIGHT_LEVEL = BCBlockStateProperties.LIGHT_LEVEL;

    public AbstractFrameBlock(Properties properties) {
        super(properties);
    }

    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    public abstract TileEntity createTileEntity(BlockState state, IBlockReader world);

    @Override
    @SuppressWarnings("deprecation")
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult trace) {
        ItemStack item = player.getHeldItem(hand);
        if (!world.isRemote) {
            BlockAppearanceHelper.setAppearanceDetails(world, item, state, pos, player, hand);

            //attempt to insert the ItemStack
            if(FramedBlockHelper.isItemStackValidInsertCandidate(item)) {
                if(state.get(CONTAINS_BLOCK)) { return ActionResultType.PASS; }

                TileEntity tileEntity = world.getTileEntity(pos);
                Block heldBlock = ((BlockItem) item.getItem()).getBlock();
                if (tileEntity instanceof FrameBlockTile) {
                    BlockState handBlockState = heldBlock.getDefaultState();
                    insertBlock(world, pos, state, handBlockState);
                    if (!player.isCreative()) {
                        item.setCount(item.getCount() - 1);
                    }
                }
            }

            //hammer is needed to remove the block from the frame - you can change it in the config
            if (item.getItem() == Registration.HAMMER.get() || (!BCModConfig.HAMMER_NEEDED.get() && player.isSneaking())) {
                if (!player.isCreative())
                    this.dropContainedBlock(world, pos);
                state = state.with(CONTAINS_BLOCK, Boolean.FALSE);
                world.setBlockState(pos, state, 2);
            }
        }
        return ActionResultType.SUCCESS;
    }


    /*
     * Used on post-place to see if we should automatically set the contained block
     */
    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if(!worldIn.isRemote) {
            if(placer != null & placer instanceof ServerPlayerEntity) {
                ServerPlayerEntity player = (ServerPlayerEntity) placer;
                ItemStack offhandStack = player.getHeldItemOffhand();
                if(FramedBlockHelper.isItemStackValidInsertCandidate(offhandStack)) {
                    BlockItem offhandBlock = (BlockItem)offhandStack.getItem();
                    TileEntity tileEntity = worldIn.getTileEntity(pos);
                    if (tileEntity instanceof FrameBlockTile) {
                        BlockState handBlockState =  offhandBlock.getBlock().getDefaultState();
                        insertBlock(worldIn, pos, state, handBlockState);
                        if (!player.isCreative()) {
                            offhandStack.setCount(offhandStack.getCount() - 1); //TODO: this shouldn't always be - 1
                        }
                    }
                }
            }
        }
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
    }

    protected void dropContainedBlock(World worldIn, BlockPos pos) {
        if (!worldIn.isRemote) {
            TileEntity tileentity = worldIn.getTileEntity(pos);
            if (tileentity instanceof FrameBlockTile) {
                FrameBlockTile frameTileEntity = (FrameBlockTile) tileentity;
                BlockState blockState = frameTileEntity.getMimic();
                if (!(blockState == null)) {
                    worldIn.playEvent(1010, pos, 0);
                    frameTileEntity.clear();
                    float f = 0.7F;
                    double d0 = (double) (worldIn.rand.nextFloat() * 0.7F) + (double) 0.15F;
                    double d1 = (double) (worldIn.rand.nextFloat() * 0.7F) + (double) 0.060000002F + 0.6D;
                    double d2 = (double) (worldIn.rand.nextFloat() * 0.7F) + (double) 0.15F;
                    ItemStack itemstack1 = new ItemStack(blockState.getBlock());
                    ItemEntity itementity = new ItemEntity(worldIn, (double) pos.getX() + d0, (double) pos.getY() + d1, (double) pos.getZ() + d2, itemstack1);
                    itementity.setDefaultPickupDelay();
                    worldIn.addEntity(itementity);
                    frameTileEntity.clear();
                }
            }
        }
    }

    protected void insertBlock(IWorld worldIn, BlockPos pos, BlockState state, BlockState handBlock) {
        TileEntity tileentity = worldIn.getTileEntity(pos);
        if (tileentity instanceof FrameBlockTile) {
            FrameBlockTile frameTileEntity = (FrameBlockTile) tileentity;
            frameTileEntity.clear();
            frameTileEntity.setMimic(handBlock);
            worldIn.setBlockState(pos, state.with(CONTAINS_BLOCK, Boolean.TRUE), 2);
        }
    }


    public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) {
        if (state.get(LIGHT_LEVEL) > 15) {
            return 15;
        }
        return state.get(LIGHT_LEVEL);
    }

    public abstract BlockState getStateForPlacement(BlockItemUseContext context);

    @Override
    @SuppressWarnings("deprecation")
    public abstract VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context);

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(CONTAINS_BLOCK, LIGHT_LEVEL);
    }
}
