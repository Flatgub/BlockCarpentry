package mod.pianomanu.blockcarpentry.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
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
        tag.put("value", NBTUtil.writeBlockState(value));
        return tag;
    }

    @Override
    public CompoundNBT toNBT(CompoundNBT in) {
        in.put("value", NBTUtil.writeBlockState(value));
        return in;
    }

    @Override
    public boolean fromNBT(CompoundNBT in) {
        BlockState old = value;
        value = NBTUtil.readBlockState(in.getCompound("value"));
        return !old.equals(value);
    }


}
