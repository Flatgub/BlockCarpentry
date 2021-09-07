package mod.pianomanu.blockcarpentry.util;

import net.minecraft.util.Direction;

import java.util.BitSet;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Objects;

public class SixSideSet implements Iterable<Direction>{
    private final EnumSet<Direction> faces;

    public SixSideSet() {
         faces = EnumSet.noneOf(Direction.class);
    }

    public SixSideSet(SixSideSet clone) {
        faces = EnumSet.copyOf(clone.faces);
    }

    public SixSideSet(EnumSet<Direction> initial) {
        faces = EnumSet.copyOf(initial);
    }

    public SixSideSet(boolean initial) {
        faces = initial ? EnumSet.allOf(Direction.class) : EnumSet.noneOf(Direction.class);
    }

    public void set(Direction face, boolean value) {
        if(value) {
            faces.add(face);
        }
        else {
            faces.remove(face);
        }
    }

    public boolean test(Direction face) {
        return faces.contains(face);
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof SixSideSet) {
            SixSideSet os = (SixSideSet) o;
            return faces.equals(os.faces);
        }
        return Objects.equals(this, o);
    }

    @Override
    public Iterator<Direction> iterator() {
        return faces.iterator();
    }
}
