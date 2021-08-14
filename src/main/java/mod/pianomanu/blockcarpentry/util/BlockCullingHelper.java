package mod.pianomanu.blockcarpentry.util;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairsBlock;

public class BlockCullingHelper {

    public static boolean skipSideRendering(BlockState state) {
        if (state.isSolid())
            return true;
        Block b = state.getBlock();
        if (b instanceof StairsBlock)
            return false;
        if (b instanceof SlabBlock)
            return false;
        return true;
    }
}
//========SOLI DEO GLORIA========//