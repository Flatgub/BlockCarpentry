package mod.pianomanu.blockcarpentry.block;

import mod.pianomanu.blockcarpentry.tileentity.SignFrameTile;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.WoodType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateContainer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.core.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

import static mod.pianomanu.blockcarpentry.util.BCBlockStateProperties.CONTAINS_BLOCK;
import static mod.pianomanu.blockcarpentry.util.BCBlockStateProperties.LIGHT_LEVEL;

/**
 * Main class for standing frame signs - all important block info can be found here
 * Visit {@link FrameBlock} for a better documentation
 * @author PianoManu
 * @version 1.1 09/25/20
 */
public class StandingSignFrameBlock extends StandingSignBlock {
    public StandingSignFrameBlock(Properties properties) {
        super(properties, WoodType.OAK);
        this.registerDefaultState(this.stateContainer.getBaseState().with(ROTATION, 0).with(WATERLOGGED, Boolean.FALSE).with(CONTAINS_BLOCK, false).with(LIGHT_LEVEL, 0));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(ROTATION, WATERLOGGED, CONTAINS_BLOCK, LIGHT_LEVEL);
    }

    @Override
    public boolean hasBlockEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockState state, IBlockReader world) {
        System.out.println("new tile");
        return new SignFrameTile();
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        BlockEntity tile = worldIn.getBlockEntity(pos);
        if (tile instanceof SignFrameTile) {
            SignFrameTile signTile = (SignFrameTile) tile;
            player.openSignEditor(signTile);
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.FAIL;
    }
}
//========SOLI DEO GLORIA========//