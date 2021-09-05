package mod.pianomanu.blockcarpentry.util;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.BooleanProperty;

public abstract class AppearanceProperty<T> {
    protected T value = null;
    protected T defaultValue = null;

    public AppearanceProperty() {}
    public AppearanceProperty(T defaultval) {
        value = defaultval;
        defaultValue = defaultval;
    }

    public void setValue(T newVal) {
        value = newVal;
    }

    public T getValue() {
        return value;
    }

    public void reset() {
        value = defaultValue;
    }

    public abstract CompoundNBT toNewNBT();

    public abstract CompoundNBT toNBT(CompoundNBT in);

    public abstract boolean fromNBT(CompoundNBT in);

}
