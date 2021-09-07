package mod.pianomanu.blockcarpentry.util;

import mod.pianomanu.blockcarpentry.BlockCarpentryMain;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;

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

    public boolean isSet() {return value != null;}

    public abstract CompoundNBT toNewNBT();

    public abstract CompoundNBT toNBT(CompoundNBT in);

    public abstract boolean fromNBT(CompoundNBT in);

    // I don't know if this belongs here, but its the only location the type is known
    @SuppressWarnings("unchecked")
    public void addToBuilder(ModelDataMap.Builder builder, String as) {
        ModelProperty<T> modelprop = (ModelProperty<T>) AppearancePropertyCollection.modelPropertyMapping.get(as);
        builder.withInitial(modelprop, value);
    }

}
