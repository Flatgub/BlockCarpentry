package mod.pianomanu.blockcarpentry.item;

import mod.pianomanu.blockcarpentry.tileentity.SignFrameTile;
import net.minecraft.core.BlockPos;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.SignItem;
import net.minecraft.world.World;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class FrameSignItem extends SignItem {
    public FrameSignItem(Properties propertiesIn, Block floorBlockIn, Block wallBlockIn) {
        super(propertiesIn, floorBlockIn, wallBlockIn);
    }

    protected boolean onBlockPlaced(BlockPos pos, World worldIn, @Nullable PlayerEntity player, ItemStack stack, BlockState state) {
        boolean flag = super.onBlockPlaced(pos, worldIn, player, stack, state);
        if (!worldIn.isClientSide && !flag && player != null) {
            System.out.println("platziert");
            player.openSignEditor((SignFrameTile) worldIn.getBlockEntity(pos));
        }

        return flag;
    }
}
//========SOLI DEO GLORIA========//