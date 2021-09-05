package mod.pianomanu.blockcarpentry.util;

import net.minecraft.util.Direction;

import java.util.BitSet;
import java.util.Objects;

public class SixSideSet {
    private BitSet bits;

    public SixSideSet() {
         bits = new BitSet(5);
    }

    public SixSideSet(boolean initial) {
         bits = new BitSet(5);
         bits.set(0,5,initial);
    }

    public void set(Direction face, boolean value) {
        if(value) {
            bits.set(face.getIndex());
        }
        else {
            bits.clear(face.getIndex());
        }
    }

    public boolean test(Direction face) {
        return bits.get(face.getIndex());
    }

    public static SixSideSet newFromInt(int in) {
        SixSideSet set = new SixSideSet();
        set.bits = BitSet.valueOf(new byte[]{(byte) in});
        return set;
    }

    public int toInt() {
        return bits.toByteArray()[0];
    }

    public void fromInt(int in) {
        bits = BitSet.valueOf(new byte[]{(byte) in});
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof SixSideSet) {
            SixSideSet os = (SixSideSet) o;
            return bits.equals(os.bits);
        }
        return Objects.equals(this, o);
    }
}
