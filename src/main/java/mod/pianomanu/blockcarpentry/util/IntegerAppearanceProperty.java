package mod.pianomanu.blockcarpentry.util;

import net.minecraft.nbt.CompoundNBT;

public class IntegerAppearanceProperty extends AppearanceProperty<Integer>  {

    public IntegerAppearanceProperty() {
        super();
        value = 0;
        defaultValue = 0;
    }

    public IntegerAppearanceProperty(Integer defaultval) {
        super(defaultval);
    }

    @Override
    public CompoundNBT toNewNBT() {
        CompoundNBT tag = new CompoundNBT();
        tag.putInt("value", value);
        return tag;
    }

    @Override
    public CompoundNBT toNBT(CompoundNBT in) {
        in.putInt("value", value);
        return in;
    }

    @Override
    public boolean fromNBT(CompoundNBT in) {
        int old = value;
        value = in.getInt("value");
        return old != value;
    }
}
