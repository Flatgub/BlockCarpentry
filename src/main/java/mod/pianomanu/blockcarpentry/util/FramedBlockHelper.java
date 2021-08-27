package mod.pianomanu.blockcarpentry.util;

import mod.pianomanu.blockcarpentry.BlockCarpentryMain;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;

import java.util.Objects;

public class FramedBlockHelper {
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
        return BlockSavingHelper.isValidBlock(block.getBlock());
    }
}
