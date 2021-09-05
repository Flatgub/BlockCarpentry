package mod.pianomanu.blockcarpentry.util;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;

import java.util.HashMap;
import java.util.Map;

public class FrameAppearanceData {
    public static final String APPEARANCE_NBT_NAME = "appearance";
    private HashMap<String, AppearanceProperty<?>> properties;

    public FrameAppearanceData() {
        properties = AppearancePropertyCollection.newCollection().withDefaults().get();
    }

    public FrameAppearanceData(HashMap<String, AppearanceProperty<?>> collection) {
        properties = collection;
    }

    public boolean hasProperty(String name) {
        return properties.containsKey(name);
    }

    @SuppressWarnings("unchecked")
    public <T> T getProperty(String name) {
        return (T) properties.get(name).getValue();
    }

    @SuppressWarnings("unchecked")
    public <T> void setProperty(String name, T value) {
        AppearanceProperty<T> prop = (AppearanceProperty<T>) properties.get(name);
        prop.setValue(value);
    }

    public void reset() {
        for(Map.Entry<String, AppearanceProperty<?>> entry: properties.entrySet()) {
            entry.getValue().reset();
        }
    }

    public CompoundNBT toNBT() {
        CompoundNBT tag = new CompoundNBT();
        for(Map.Entry<String, AppearanceProperty<?>> entry: properties.entrySet()) {
            tag.put(entry.getKey(), entry.getValue().toNewNBT());
        }
        return tag;
    }

    public boolean fromNBT(CompoundNBT in) {
        boolean changed = false;
        for(Map.Entry<String, AppearanceProperty<?>> entry: properties.entrySet()) {
            AppearanceProperty<?> prop = entry.getValue();
            if(in.contains(entry.getKey())) {
                if(prop.fromNBT(in.getCompound(entry.getKey()))) {
                    changed = true;
                }
            }
        }
        return changed;
    }

    public IModelData toModelData() {
        ModelDataMap.Builder builder = new ModelDataMap.Builder();
        for(Map.Entry<String, AppearanceProperty<?>> property: properties.entrySet()) {
            String name = property.getKey();
            property.getValue().addToBuilder(builder, name);
        }
        return builder.build();
    }
}
