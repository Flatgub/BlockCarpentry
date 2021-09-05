package mod.pianomanu.blockcarpentry.util;

import net.minecraft.block.BlockState;
import net.minecraft.item.DyeColor;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.Direction;
import net.minecraftforge.common.util.Constants;

public class DyeColorAppearanceProperty extends AppearanceProperty<DyeColor>  {

    public DyeColorAppearanceProperty() {
        super();
    }

    public DyeColorAppearanceProperty(DyeColor defaultval) {
        super(defaultval);
    }

    @Override
    public CompoundNBT toNewNBT() {
        CompoundNBT tag = new CompoundNBT();
        tag.putInt("value", value.getId());
        return tag;
    }

    @Override
    public CompoundNBT toNBT(CompoundNBT in) {
        in.putInt("value", value.getId());
        return in;
    }

    @Override
    public boolean fromNBT(CompoundNBT in) {
        DyeColor old = value;
        value = DyeColor.byId(in.getInt("value"));
        return !old.equals(value);
    }


}
