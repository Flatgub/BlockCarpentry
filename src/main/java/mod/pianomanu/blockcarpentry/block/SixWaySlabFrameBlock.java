package mod.pianomanu.blockcarpentry.block;

import mod.pianomanu.blockcarpentry.BlockCarpentryMain;
import mod.pianomanu.blockcarpentry.setup.Registration;
import mod.pianomanu.blockcarpentry.setup.config.BCModConfig;
import mod.pianomanu.blockcarpentry.tileentity.TwoBlocksFrameBlockTile;
import mod.pianomanu.blockcarpentry.util.BCBlockStateProperties;
import mod.pianomanu.blockcarpentry.util.BlockAppearanceHelper;
import mod.pianomanu.blockcarpentry.util.BlockSavingHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.state.DirectionProperty;
import net.minecraft.util.Direction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.World;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.IWaterLoggable;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

import static net.minecraft.state.properties.BlockStateProperties.WATERLOGGED;

/**
 * Main class for frame "slabs", they can be placed in six different ways (that's the reason for this class name) - all important block info can be found here
 * Visit {@link FrameBlock} for a better documentation
 *
 * @author PianoManu
 * @version 1.9 08/13/21
 */
@SuppressWarnings("deprecation")
public class SixWaySlabFrameBlock extends AbstractSixWayFrameBlock implements IWaterLoggable {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty CONTAINS_BLOCK = BCBlockStateProperties.CONTAINS_BLOCK;
    public static final BooleanProperty CONTAINS_2ND_BLOCK = BCBlockStateProperties.CONTAINS_2ND_BLOCK;
    public static final IntegerProperty LIGHT_LEVEL = BCBlockStateProperties.LIGHT_LEVEL;
    public static final BooleanProperty DOUBLE_SLAB = BCBlockStateProperties.DOUBLE;
    //everything is inverted because when placing, we would need to take the opposite - I figured it out when I completed my work and I don't want to change everything again
    protected static final VoxelShape BOTTOM = Block.box(0.0D, 8.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape TOP = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
    protected static final VoxelShape EAST = Block.box(0.0D, 0.0D, 0.0D, 8.0D, 16.0D, 16.0D);
    protected static final VoxelShape SOUTH = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 8.0D);
    protected static final VoxelShape WEST = Block.box(8.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape NORTH = Block.box(0.0D, 0.0D, 8.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape CUBE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);

    public SixWaySlabFrameBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateContainer.getBaseState().setValue(FACING, Direction.DOWN).setValue(CONTAINS_BLOCK, Boolean.FALSE).setValue(LIGHT_LEVEL, 0).setValue(WATERLOGGED, false).setValue(DOUBLE_SLAB, false).setValue(CONTAINS_2ND_BLOCK, false));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(WATERLOGGED, DOUBLE_SLAB, CONTAINS_2ND_BLOCK);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        if (state.getValue(DOUBLE_SLAB))
            return CUBE;
        switch (state.getValue(FACING)) {
            case EAST:
                return EAST;
            case NORTH:
                return NORTH;
            case SOUTH:
                return SOUTH;
            case WEST:
                return WEST;
            case UP:
                return TOP;
            default:
                return BOTTOM;
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos blockpos = context.getPos();
        FluidState fluidstate = context.getWorld().getFluidState(blockpos);
        BlockState blockState = context.getWorld().getBlockState(blockpos);
        if (blockState.getBlock() instanceof SixWaySlabFrameBlock) {
            return blockState.setValue(DOUBLE_SLAB, true);
        }
        if (Objects.requireNonNull(context.getPlayer()).isCrouching() && BCModConfig.SNEAK_FOR_VERTICAL_SLABS.get() || !Objects.requireNonNull(context.getPlayer()).isCrouching() && !BCModConfig.SNEAK_FOR_VERTICAL_SLABS.get()) {
            if (fluidstate.getFluid() == Fluids.WATER) {
                return this.defaultBlockState().setValue(FACING, context.getFace()).setValue(WATERLOGGED, fluidstate.isSource());
            } else {
                return this.defaultBlockState().setValue(FACING, context.getFace());
            }
        } else {
            BlockState blockstate1 = this.defaultBlockState().setValue(FACING, Direction.UP).setValue(WATERLOGGED, fluidstate.getFluid() == Fluids.WATER);
            Direction direction = context.getFace();
            return direction != Direction.DOWN && (direction == Direction.UP || !(context.getHitVec().y - (double) blockpos.getY() > 0.5D)) ? blockstate1 : blockstate1.setValue(FACING, Direction.DOWN);
        }
    }

    public boolean isReplaceable(BlockState state, BlockPlaceContext useContext) {
        ItemStack itemstack = useContext.getItem();
        boolean isDouble = state.getValue(DOUBLE_SLAB);
        if (!isDouble && itemstack.getItem() == this.asItem()) {
            if (useContext.replacingClickedOnBlock()) {
                Direction direction = useContext.getFace();
                switch (state.getValue(FACING)) {
                    case EAST:
                        return direction == Direction.EAST;
                    case SOUTH:
                        return direction == Direction.SOUTH;
                    case WEST:
                        return direction == Direction.WEST;
                    case NORTH:
                        return direction == Direction.NORTH;
                    case UP:
                        return direction == Direction.UP;
                    default:
                        return direction == Direction.DOWN;
                }
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean hasBlockEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockState state, IBlockReader world) {
        return new TwoBlocksFrameBlockTile();
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitresult) {
        ItemStack item = player.getItemInHand(hand);
        if (!world.isClientSide) {
            BlockAppearanceHelper.setLightLevel(item, state, world, pos, player, hand);
            BlockAppearanceHelper.setTexture(item, state, world, player, pos);
            BlockAppearanceHelper.setDesign(world, pos, player, item);
            BlockAppearanceHelper.setDesignTexture(world, pos, player, item);
            BlockAppearanceHelper.setOverlay(world, pos, player, item);
            BlockAppearanceHelper.setRotation(world, pos, player, item);
            if (item.getItem() instanceof BlockItem) {
                if (state.getValue(BCBlockStateProperties.CONTAINS_BLOCK) && !state.getValue(DOUBLE_SLAB) || state.getValue(BCBlockStateProperties.CONTAINS_2ND_BLOCK) || Objects.requireNonNull(item.getItem().getRegistryName()).getNamespace().equals(BlockCarpentryMain.MOD_ID)) {
                    return InteractionResult.PASS;
                }
                BlockEntity tileEntity = world.getBlockEntity(pos);
                int count = player.getItemInHand(hand).getCount();
                Block heldBlock = ((BlockItem) item.getItem()).getBlock();
                if (tileEntity instanceof TwoBlocksFrameBlockTile && !item.isEmpty() && BlockSavingHelper.isValidBlock(heldBlock) && !state.getValue(CONTAINS_2ND_BLOCK)) {
                    BlockState handBlockState = ((BlockItem) item.getItem()).getBlock().defaultBlockState();
                    insertBlock(world, pos, state, handBlockState);
                    if (!player.isCreative())
                        player.getItemInHand(hand).setCount(count - 1);
                    checkForVisibility(state, world, pos, (TwoBlocksFrameBlockTile) tileEntity);
                }

            }
            if (player.getItemInHand(hand).getItem() == Registration.HAMMER.get() || (!BCModConfig.HAMMER_NEEDED.get() && player.isCrouching())) {
                if (!player.isCreative())
                    this.dropContainedBlock(world, pos);
                state = state.setValue(CONTAINS_BLOCK, Boolean.FALSE);
                state = state.setValue(CONTAINS_2ND_BLOCK, Boolean.FALSE);
                world.setBlock(pos, state, 2);
            }
        }
        return InteractionResult.SUCCESS;
    }

    protected void dropContainedBlock(World worldIn, BlockPos pos) {
        if (!worldIn.isClientSide) {
            BlockEntity tileentity = worldIn.getBlockEntity(pos);
            if (tileentity instanceof TwoBlocksFrameBlockTile) {
                TwoBlocksFrameBlockTile frameBlockEntity = (TwoBlocksFrameBlockTile) tileentity;
                BlockState blockState = frameBlockEntity.getMimic_1();
                if (!(blockState == null)) {
                    worldIn.playEvent(1010, pos, 0);
                    float f = 0.7F;
                    double d0 = (double) (worldIn.rand.nextFloat() * 0.7F) + (double) 0.15F;
                    double d1 = (worldIn.rand.nextFloat() * 0.7F) + (double) 0.060000002F + 0.6D;
                    double d2 = (double) (worldIn.rand.nextFloat() * 0.7F) + (double) 0.15F;
                    ItemStack itemstack1 = new ItemStack(blockState.getBlock());
                    ItemEntity itementity = new ItemEntity(worldIn, (double) pos.getX() + d0, (double) pos.getY() + d1, (double) pos.getZ() + d2, itemstack1);
                    itementity.setDefaultPickUpDelay();
                    worldIn.addFreshEntity(itementity);
                }
                blockState = frameBlockEntity.getMimic_2();
                if (!(blockState == null)) {
                    worldIn.playEvent(1010, pos, 0);
                    float f = 0.7F;
                    double d0 = (double) (worldIn.rand.nextFloat() * 0.7F) + (double) 0.15F;
                    double d1 = (worldIn.rand.nextFloat() * 0.7F) + (double) 0.060000002F + 0.6D;
                    double d2 = (double) (worldIn.rand.nextFloat() * 0.7F) + (double) 0.15F;
                    ItemStack itemstack1 = new ItemStack(blockState.getBlock());
                    ItemEntity itementity = new ItemEntity(worldIn, (double) pos.getX() + d0, (double) pos.getY() + d1, (double) pos.getZ() + d2, itemstack1);
                    itementity.setDefaultPickUpDelay();
                    worldIn.addFreshEntity(itementity);
                }
                frameBlockEntity.clear();
            }
        }
    }

    public void insertBlock(IWorld worldIn, BlockPos pos, BlockState state, BlockState handBlock) {
        BlockEntity tileentity = worldIn.getBlockEntity(pos);
        if (tileentity instanceof TwoBlocksFrameBlockTile) {
            if (!state.getValue(CONTAINS_BLOCK)) {
                TwoBlocksFrameBlockTile frameBlockEntity = (TwoBlocksFrameBlockTile) tileentity;
                frameBlockEntity.clear();
                frameBlockEntity.setMimic_1(handBlock);
                worldIn.setBlock(pos, state.setValue(CONTAINS_BLOCK, Boolean.TRUE), 2);
            } else if (state.getValue(DOUBLE_SLAB)) {
                TwoBlocksFrameBlockTile frameBlockEntity = (TwoBlocksFrameBlockTile) tileentity;
                frameBlockEntity.setMimic_2(handBlock);
                worldIn.setBlock(pos, state.setValue(CONTAINS_2ND_BLOCK, Boolean.TRUE), 2);
            }
        }
    }

    @Override
    public void onReplaced(BlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            dropContainedBlock(worldIn, pos);

            super.onReplaced(state, worldIn, pos, newState, isMoving);
        }
    }

    @Override
    public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) {
        if (state.getValue(LIGHT_LEVEL) > 15) {
            return 15;
        }
        return state.getValue(LIGHT_LEVEL);
    }

    @Override
    @Nonnull
    @SuppressWarnings("deprecation")
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
    }

    @Override
    @Nonnull
    public BlockState updatePostPlacement(BlockState stateIn, @Nonnull Direction facing, @Nonnull BlockState facingState, @Nonnull IWorld worldIn, @Nonnull BlockPos currentPos, @Nonnull BlockPos facingPos) {
        if (stateIn.get(WATERLOGGED)) {
            worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
        }

        return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isSideInvisible(BlockState state, BlockState adjacentBlockState, Direction side) {
        return adjacentBlockState.isIn(this) || super.isSideInvisible(state, adjacentBlockState, side);// || BlockCullingHelper.skipSideRendering(adjacentBlockState);
    }

    private void checkForVisibility(BlockState state, World world, BlockPos pos, TwoBlocksFrameBlockTile tileEntity) {
        for (Direction d : Direction.values()) {
            BlockPos.Mutable mutablePos = pos.toMutable();
            BlockState adjacentBlockState = world.getBlockState(mutablePos.move(d));
            tileEntity.setVisibileSides(d, !(adjacentBlockState.isSolid() || isSideInvisible(state, adjacentBlockState, d)));
        }
    }
}
//========SOLI DEO GLORIA========//