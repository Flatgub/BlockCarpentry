package mod.pianomanu.blockcarpentry.block;

import mod.pianomanu.blockcarpentry.BlockCarpentryMain;
import mod.pianomanu.blockcarpentry.setup.Registration;
import mod.pianomanu.blockcarpentry.setup.config.BCModConfig;
import mod.pianomanu.blockcarpentry.tileentity.ChestFrameBlockEntity;
import mod.pianomanu.blockcarpentry.util.BlockAppearanceHelper;
import mod.pianomanu.blockcarpentry.util.BlockSavingHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.entity.player.ServerPlayer;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.IWaterLoggable;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.Objects;

import static net.minecraft.state.properties.BlockStateProperties.HORIZONTAL_FACING;

/**
 * Main class for frame chests - all important block info can be found here
 * Visit {@link FrameBlock} for a better documentation
 *
 * @author PianoManu
 * @version 1.6 05/27/21
 */
public class ChestFrameBlock extends FrameBlock implements IWaterLoggable {
    private static final VoxelShape INNER_CUBE = Block.box(2.0, 2.0, 2.0, 14.0, 14.0, 14.0);
    private static final VoxelShape BOTTOM_NORTH = Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 2.0);
    private static final VoxelShape BOTTOM_EAST = Block.box(14.0, 0.0, 2.0, 16.0, 2.0, 14.0);
    private static final VoxelShape BOTTOM_SOUTH = Block.box(0.0, 0.0, 14.0, 16.0, 2.0, 16.0);
    private static final VoxelShape BOTTOM_WEST = Block.box(0.0, 0.0, 2.0, 2.0, 2.0, 14.0);
    private static final VoxelShape TOP_NORTH = Block.box(0.0, 14.0, 0.0, 16.0, 16.0, 2.0);
    private static final VoxelShape TOP_EAST = Block.box(14.0, 14.0, 2.0, 16.0, 16.0, 14.0);
    private static final VoxelShape TOP_SOUTH = Block.box(0.0, 14.0, 14.0, 16.0, 16.0, 16.0);
    private static final VoxelShape TOP_WEST = Block.box(0.0, 14.0, 2.0, 2.0, 16.0, 14.0);
    private static final VoxelShape NW_PILLAR = Block.box(0.0, 2.0, 0.0, 2.0, 14.0, 2.0);
    private static final VoxelShape SW_PILLAR = Block.box(0.0, 2.0, 14.0, 2.0, 14.0, 16.0);
    private static final VoxelShape NE_PILLAR = Block.box(14.0, 2.0, 0.0, 16.0, 14.0, 2.0);
    private static final VoxelShape SE_PILLAR = Block.box(14.0, 2.0, 14.0, 16.0, 14.0, 16.0);
    private static final VoxelShape CHEST = Shapes.or(INNER_CUBE, BOTTOM_EAST, BOTTOM_SOUTH, BOTTOM_WEST, BOTTOM_NORTH, TOP_EAST, TOP_SOUTH, TOP_WEST, TOP_NORTH, NW_PILLAR, SW_PILLAR, NE_PILLAR, SE_PILLAR);

    public ChestFrameBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(CONTAINS_BLOCK, false).setValue(LIGHT_LEVEL, 0).setValue(HORIZONTAL_FACING, Direction.NORTH).setValue(WATERLOGGED, false));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED, HORIZONTAL_FACING, CONTAINS_BLOCK, LIGHT_LEVEL);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos blockpos = context.getPos();
        FluidState fluidstate = context.getWorld().getFluidState(blockpos);
        if (fluidstate.getFluid() == Fluids.WATER) {
            return this.defaultBlockState().setValue(HORIZONTAL_FACING, context.getPlacementHorizontalFacing().getOpposite()).setValue(WATERLOGGED, fluidstate.isSource());
        } else {
            return this.defaultBlockState().setValue(HORIZONTAL_FACING, context.getPlacementHorizontalFacing().getOpposite());
        }
    }

    @Override
    public boolean hasBlockEntity(BlockState state) {
        return true;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        //return Registration.CHEST_FRAME_TILE.get().create();
        return new ChestFrameBlockEntity(Registration.CHEST_FRAME_TILE.get(), pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, World world, BlockPos pos, Player player,
                                 Hand hand, BlockRayTraceResult result) {
        ItemStack item = player.getItemInHand(hand);
        if (!world.isClientSide) {
            BlockEntity tileEntity = world.getBlockEntity(pos);
            if (item.getItem() instanceof BlockItem) {
                if (Objects.requireNonNull(item.getItem().getRegistryName()).getNamespace().equals(BlockCarpentryMain.MOD_ID)) {
                    return InteractionResult.PASS;
                }
                int count = player.getItemInHand(hand).getCount();
                Block heldBlock = ((BlockItem) item.getItem()).getBlock();
                if (tileEntity instanceof ChestFrameBlockEntity && !item.isEmpty() && BlockSavingHelper.isValidBlock(heldBlock) && !state.getValue(CONTAINS_BLOCK)) {
                    BlockState handBlockState = ((BlockItem) item.getItem()).getBlock().defaultBlockState();
                    insertBlock(world, pos, state, handBlockState);
                    if (!player.isCreative())
                        player.getItemInHand(hand).setCount(count - 1);
                }
            }
            //hammer is needed to remove the block from the frame - you can change it in the config
            if (player.getItemInHand(hand).getItem() == Registration.HAMMER.get() || (!BCModConfig.HAMMER_NEEDED.get() && player.isCrouching())) {
                if (!player.isCreative())
                    this.dropContainedBlock(world, pos);
                state = state.setValue(CONTAINS_BLOCK, Boolean.FALSE);
                world.setBlock(pos, state, 2);
            }
            BlockAppearanceHelper.setLightLevel(item, state, world, pos, player, hand);
            BlockAppearanceHelper.setTexture(item, state, world, player, pos);
            BlockAppearanceHelper.setDesign(world, pos, player, item);
            BlockAppearanceHelper.setDesignTexture(world, pos, player, item);
            BlockAppearanceHelper.setRotation(world, pos, player, item);
            if (tileEntity instanceof ChestFrameBlockEntity && state.getValue(CONTAINS_BLOCK)) {
                if (!(Objects.requireNonNull(item.getItem().getRegistryName()).getNamespace().equals(BlockCarpentryMain.MOD_ID))) {
                    NetworkHooks.openGui((ServerPlayer) player, (ChestFrameBlockEntity) tileEntity, pos);
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void dropContainedBlock(World worldIn, BlockPos pos) {
        if (!worldIn.isClientSide) {
            BlockEntity tileentity = worldIn.getBlockEntity(pos);
            if (tileentity instanceof ChestFrameBlockEntity) {
                ChestFrameBlockEntity frameBlockEntity = (ChestFrameBlockEntity) tileentity;
                BlockState blockState = frameBlockEntity.getMimic();
                if (!(blockState == null)) {
                    worldIn.levelEvent(1010, pos, 0);
                    frameBlockEntity.clear();
                    float f = 0.7F;
                    double d0 = (double) (worldIn.rand.nextFloat() * 0.7F) + (double) 0.15F;
                    double d1 = (worldIn.rand.nextFloat() * 0.7F) + (double) 0.060000002F + 0.6D;
                    double d2 = (double) (worldIn.rand.nextFloat() * 0.7F) + (double) 0.15F;
                    ItemStack itemstack1 = new ItemStack(blockState.getBlock());
                    ItemEntity itementity = new ItemEntity(worldIn, (double) pos.getX() + d0, (double) pos.getY() + d1, (double) pos.getZ() + d2, itemstack1);
                    itementity.setDefaultPickUpDelay();
                    worldIn.addFreshEntity(itementity);
                    frameBlockEntity.clear();
                }
            }
        }
    }

    @Override
    public void insertBlock(IWorld worldIn, BlockPos pos, BlockState state, BlockState handBlock) {
        BlockEntity tileentity = worldIn.getBlockEntity(pos);
        if (tileentity instanceof ChestFrameBlockEntity) {
            ChestFrameBlockEntity frameBlockEntity = (ChestFrameBlockEntity) tileentity;
            frameBlockEntity.clear();
            frameBlockEntity.setMimic(handBlock);
            worldIn.setBlock(pos, state.setValue(CONTAINS_BLOCK, Boolean.TRUE), 2);
        }
    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity te = worldIn.getBlockEntity(pos);
            if (te instanceof ChestFrameBlockEntity) {
                dropContainedBlock(worldIn, pos);
                InventoryHelper.dropItems(worldIn, pos, ((ChestFrameBlockEntity) te).getItems());
            }
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        if (stateIn.get(WATERLOGGED)) {
            worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
        }

        return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return CHEST;
    }
}
//========SOLI DEO GLORIA========//