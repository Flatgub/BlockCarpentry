package mod.pianomanu.blockcarpentry.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;

public class BlockstateAppearanceProperty extends AppearanceProperty<BlockState>  {

    public BlockstateAppearanceProperty() {
        super();
    }

    public BlockstateAppearanceProperty(BlockState defaultval) {
        super(defaultval);
    }

    @Override
    public CompoundNBT toNewNBT() {
        CompoundNBT tag = new CompoundNBT();
        if(value != null) {
            tag.put("value", NBTUtil.writeBlockState(value));
        }
        return tag;
    }

    @Override
    public CompoundNBT toNBT(CompoundNBT in) {
        if(value != null) {
            in.put("value", NBTUtil.writeBlockState(value));
        }
        return in;
    }

    @Override
    public boolean fromNBT(CompoundNBT in) {
        BlockState old = value;
        if(in.contains("value")) {
            value = NBTUtil.readBlockState(in.getCompound("value"));
        }
        else {
            value = null;
        }
        if(old == null) {
            return value != null;
        }
        return !old.equals(value);
    }


}
