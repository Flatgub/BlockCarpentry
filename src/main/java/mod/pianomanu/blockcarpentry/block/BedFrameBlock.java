package mod.pianomanu.blockcarpentry.block;

import mod.pianomanu.blockcarpentry.BlockCarpentryMain;
import mod.pianomanu.blockcarpentry.setup.Registration;
import mod.pianomanu.blockcarpentry.setup.config.BCModConfig;
import mod.pianomanu.blockcarpentry.tileentity.BedFrameTile;
import mod.pianomanu.blockcarpentry.util.BCBlockStateProperties;
import mod.pianomanu.blockcarpentry.util.BlockAppearanceHelper;
import mod.pianomanu.blockcarpentry.util.BlockSavingHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockRenderType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraftforge.common.Tags;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

/**
 * Main class for frame beds - all important block info can be found here
 * Visit {@link FrameBlock} for a better documentation
 *
 * @author PianoManu
 * @version 1.5 05/27/21
 */
public class BedFrameBlock extends BedBlock {
    public static final BooleanProperty CONTAINS_BLOCK = BCBlockStateProperties.CONTAINS_BLOCK;
    public static final IntegerProperty LIGHT_LEVEL = BCBlockStateProperties.LIGHT_LEVEL;
    public static final EnumProperty<BedPart> PART = BlockStateProperties.BED_PART;
    public static final BooleanProperty OCCUPIED = BlockStateProperties.OCCUPIED;
    public static final DirectionProperty HORIZONTAL_FACING = BlockStateProperties.HORIZONTAL_FACING;

    public BedFrameBlock(DyeColor colorIn, Properties properties) {
        super(colorIn, properties);
        this.registerDefaultState(this.stateContainer.getBaseState().setValue(CONTAINS_BLOCK, false).setValue(LIGHT_LEVEL, 0).setValue(PART, BedPart.FOOT).setValue(OCCUPIED, Boolean.valueOf(false)).setValue(HORIZONTAL_FACING, Direction.NORTH));//.setValue(TEXTURE,0));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(CONTAINS_BLOCK, LIGHT_LEVEL, PART, OCCUPIED, HORIZONTAL_FACING);//, TEXTURE);
    }

    @Override
    public boolean hasBlockEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockState state, IBlockReader world) {
        return new BedFrameTile();
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitresult) {
        ItemStack item = player.getItemInHand(hand);
        if (!world.isClientSide) {
            if ((state.getValue(CONTAINS_BLOCK) && !item.getItem().is(Tags.Items.DYES) && !item.getItem().getRegistryName().getNamespace().equals(BlockCarpentryMain.MOD_ID)) || item.isEmpty()) {
                //Taken from BedBlock, should work similar to vanilla beds
                if (state.getValue(PART) != BedPart.HEAD) {
                    pos = pos.offset(state.getValue(HORIZONTAL_FACING));
                    state = world.getBlockState(pos);
                    if (!state.is(this)) {
                        return InteractionResult.CONSUME;
                    }
                }

                if (!func_235330_a_(world)) {
                    world.removeBlock(pos, false);
                    BlockPos blockpos = pos.offset(state.getValue(HORIZONTAL_FACING).getOpposite());
                    if (world.getBlockState(blockpos).is(this)) {
                        world.removeBlock(blockpos, false);
                    }

                    world.createExplosion((Entity) null, DamageSource.func_233546_a_(), (ExplosionContext) null, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, 5.0F, true, Explosion.Mode.DESTROY);
                    return InteractionResult.SUCCESS;
                } else if (state.getValue(OCCUPIED)) {
                    if (!this.func_226861_a_(world, pos)) {
                        player.displayClientMessage(new TranslatableComponent("block.minecraft.bed.occupied"), true);
                    }

                    return InteractionResult.SUCCESS;
                } else {
                    player.trySleep(pos).ifLeft((p_220173_1_) -> {
                        if (p_220173_1_ != null) {
                            player.displayClientMessage(p_220173_1_.getMessage(), true);
                        }

                    });
                    return InteractionResult.SUCCESS;
                }
            }
            if (state.getValue(CONTAINS_BLOCK) && player.isCrouching()) {
                this.dropContainedBlock(world, pos);
                state = state.setValue(CONTAINS_BLOCK, Boolean.FALSE);
                world.setBlock(pos, state, 2);
            } else {
                if (item.getItem() instanceof BlockItem) {
                    if (Objects.requireNonNull(item.getItem().getRegistryName()).getNamespace().equals(BlockCarpentryMain.MOD_ID)) {
                        return InteractionResult.PASS;
                    }
                    BlockEntity tileEntity = world.getBlockEntity(pos);
                    int count = player.getItemInHand(hand).getCount();
                    Block heldBlock = ((BlockItem) item.getItem()).getBlock();
                    if (tileEntity instanceof BedFrameTile && !item.isEmpty() && BlockSavingHelper.isValidBlock(heldBlock) && !state.getValue(CONTAINS_BLOCK)) {
                        BlockState handBlockState = ((BlockItem) item.getItem()).getBlock().defaultBlockState();
                        insertBlock(world, pos, state, handBlockState);
                        if (!player.isCreative())
                            player.getItemInHand(hand).setCount(count - 1);
                    }
                }
            }
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
            BlockAppearanceHelper.setWoolColor(world, pos, player, hand);
            BlockAppearanceHelper.setOverlay(world, pos, player, item);
            BlockAppearanceHelper.setRotation(world, pos, player, item);
        }
        return InteractionResult.SUCCESS;
    }

    private boolean func_226861_a_(World world, BlockPos pos) {
        List<VillagerEntity> list = world.getEntitiesWithinAABB(VillagerEntity.class, new AxisAlignedBB(pos), LivingEntity::isSleeping);
        if (list.isEmpty()) {
            return false;
        } else {
            list.get(0).wakeUp();
            return true;
        }
    }

    protected void dropContainedBlock(World worldIn, BlockPos pos) {
        if (!worldIn.isClientSide) {
            BlockEntity tileentity = worldIn.getBlockEntity(pos);
            if (tileentity instanceof BedFrameTile) {
                BedFrameTile frameBlockEntity = (BedFrameTile) tileentity;
                BlockState blockState = frameBlockEntity.getMimic();
                if (!(blockState == null)) {
                    worldIn.levelEvent(1010, pos, 0);
                    frameBlockEntity.clear();
                    float f = 0.7F;
                    double d0 = (double) (worldIn.rand.nextFloat() * 0.7F) + (double) 0.15F;
                    double d1 = (worldIn.rand.nextFloat() * 0.7F) + (double) 0.060000002F + 0.6D;
                    double d2 = (double) (worldIn.rand.nextFloat() * 0.7F) + (double) 0.15F;
                    ItemStack itemstack1 = blockState.getBlock().asItem().getDefaultInstance();
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
        if (tileentity instanceof BedFrameTile) {
            BedFrameTile frameBlockEntity = (BedFrameTile) tileentity;
            frameBlockEntity.clear();
            frameBlockEntity.setMimic(handBlock);
            worldIn.setBlock(pos, state.setValue(CONTAINS_BLOCK, Boolean.TRUE), 2);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            dropContainedBlock(worldIn, pos);

            super.onReplaced(state, worldIn, pos, newState, isMoving);
        }
    }

    @Override
    public int getLightEmission(BlockState state, IBlockReader world, BlockPos pos) {
        if (state.getValue(LIGHT_LEVEL) > 15) {
            return 15;
        }
        return state.getValue(LIGHT_LEVEL);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
}
//========SOLI DEO GLORIA========//