package mod.pianomanu.blockcarpentry.util;

import net.minecraft.nbt.*;
import net.minecraft.util.Direction;
import net.minecraftforge.common.util.Constants;

public class SideSetAppearanceProperty extends AppearanceProperty<SixSideSet>  {
    public SideSetAppearanceProperty() {
        super();
    }

    public SideSetAppearanceProperty(SixSideSet defaultval) {
        super(defaultval);
    }

    @Override
    public void reset() {
        value = new SixSideSet(defaultValue);
    }

    @Override
    public CompoundNBT toNewNBT() {
        CompoundNBT tag = new CompoundNBT();
        toNBT(tag);
        return tag;
    }

    @Override
    public CompoundNBT toNBT(CompoundNBT in) {
        ListNBT faces = new ListNBT();
        for(Direction face : value) {
            faces.add(StringNBT.valueOf(face.getName2()));
        }
        in.put("value", faces);
        return in;
    }

    @Override
    public boolean fromNBT(CompoundNBT in) {
        SixSideSet old = value;
        value = new SixSideSet();
        if(in.contains("value")) {
            ListNBT faces = in.getList("value", Constants.NBT.TAG_STRING);
            for(INBT face : faces) {
                String facename = face.getString();
                value.set(Direction.byName(facename),true);
            }
        }
        return !old.equals(value);
    }


}
