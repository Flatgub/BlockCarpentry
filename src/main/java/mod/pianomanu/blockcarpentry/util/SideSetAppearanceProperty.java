package mod.pianomanu.blockcarpentry.util;

import net.minecraft.nbt.CompoundNBT;

public class SideSetAppearanceProperty extends AppearanceProperty<SixSideSet>  {
    SixSideSet value = new SixSideSet();

    public SideSetAppearanceProperty() {
        super();
    }

    public SideSetAppearanceProperty(SixSideSet defaultval) {
        super(defaultval);
    }

    @Override
    public void reset() {
        value = new SixSideSet();
    }

    @Override
    public CompoundNBT toNewNBT() {
        CompoundNBT tag = new CompoundNBT();
        tag.putInt("value", value.toInt());
        return tag;
    }

    @Override
    public CompoundNBT toNBT(CompoundNBT in) {
        in.putInt("value", value.toInt());
        return in;
    }

    @Override
    public boolean fromNBT(CompoundNBT in) {
        SixSideSet old = value;
        value = SixSideSet.newFromInt(in.getInt("value"));
        return !old.equals(value);
    }


}
